package com.dfq.coeffi.controller.finance;

import com.dfq.coeffi.auditlog.issue.IssueTrackerService;
import com.dfq.coeffi.auditlog.issue.Priority;
import com.dfq.coeffi.controller.BaseController;
import com.dfq.coeffi.dto.DateDto;
import com.dfq.coeffi.entity.finance.daybook.DayBook;
import com.dfq.coeffi.entity.finance.expense.Asset;
import com.dfq.coeffi.entity.user.User;
import com.dfq.coeffi.exception.finance.AssetNotFoundException;
import com.dfq.coeffi.service.UserService;
import com.dfq.coeffi.service.finance.AssetService;
import com.dfq.coeffi.service.finance.DayBookService;
import com.dfq.coeffi.util.DateUtil;
import com.dfq.coeffi.util.GeneratePdfReport;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.Principal;
import java.text.ParseException;
import java.util.List;
import java.util.Optional;

@Slf4j
@Getter
@Setter
@RestController

public class AssetController extends BaseController
{
	@Autowired
    private final AssetService assetService;
    private final DayBookService dayBookService;
    private final UserService userService;
    private final IssueTrackerService issueTrackerService;

	@Autowired
	public AssetController(AssetService assetService, DayBookService dayBookService, UserService userService, IssueTrackerService issueTrackerService)
	{
        this.assetService = assetService;
        this.dayBookService = dayBookService;
        this.userService = userService;
        this.issueTrackerService = issueTrackerService;
	}
	
	/**
	 * @return all the available assets
	 */
	@GetMapping("asset")
	public ResponseEntity<List<Asset>> listAllAsset()
	{
        List<Asset> assets;
	    try{
            assets=assetService.listAllAsset();
        }catch (AssetNotFoundException ne){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
		return new ResponseEntity<>(assets, HttpStatus.OK);
	}

	@GetMapping("asset/{page}/{size}")
	public ResponseEntity<List<Asset>> assetsWithPageable(@PathVariable int page, @PathVariable int size){
		List<Asset> assets = assetService.listAllAsset(page, size);
		return new ResponseEntity<>(assets, HttpStatus.OK);
	}
	
	@PostMapping("asset")
	public ResponseEntity<Asset> createAsset(@Valid @RequestBody Asset asset, Principal principal) {
		asset.setCreatedOn(DateUtil.getTodayDate());
		Asset persistedAsset = assetService.createAsset(asset, loggedUser(principal));

		if(persistedAsset != null){
		    log.info("Asset created", "ASSET");
			String message = asset.getTitle();
			dayBookService.daybookEntry(asset.getAmount(), message, asset.getCreatedOn(),
                    persistedAsset.getId(), "ASSET");
            log.info("Asset entry created in daybook as well", "ASSET");
        }
        return new ResponseEntity<>(persistedAsset, HttpStatus.CREATED);
	}
	
	@PutMapping("asset")
	public ResponseEntity<Asset> update(@Valid @RequestBody Asset asset, Principal principal) {
		Asset persistedAsset = assetService.createAsset(asset, loggedUser(principal));
        if(persistedAsset != null){
            log.info("Asset updated");
            DayBook dayBook = dayBookService.getDaybookByRefName(asset.getId(), "ASSET");
            if(dayBook != null){
            	dayBookService.delete(dayBook.getId());
            	dayBook.setDescription(asset.getTitle());
				dayBook.setAmount(asset.getAmount());
				dayBookService.save(dayBook);
				log.info("Entry updated in daybook as well");
			}
        }
		return new ResponseEntity<>(persistedAsset, HttpStatus.OK);
	}
	
	@DeleteMapping("asset/{id}")
	public ResponseEntity<Asset> deleteAsset(@PathVariable long id)
	{
		Optional<Asset> asset=assetService.getAsset(id);
		
		if (!asset.isPresent())
		{
			throw new EntityNotFoundException(Asset.class.getName());
		}
		assetService.deleteAsset(id);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	
	/**
	 * @return all the available assets between given date
	 */
	@PostMapping("asset/date-range")
	public ResponseEntity<List<Asset>> listOfDetails(@RequestBody DateDto dateDto)
	{
		List<Asset> assets=assetService.getAssetBetweenDate(dateDto.startDate, dateDto.endDate);	
		return new ResponseEntity<>(assets, HttpStatus.OK);
	}

    private User loggedUser(Principal principal){
        User user = null;
        if(principal.getName() != null){
            user = userService.getUser(Long.parseLong(principal.getName()));
        }
        return user;
    }

    /**
     *
     * @param id
     * @return the particular object based on passed ID
     */
    @GetMapping("asset/{id}")
    public ResponseEntity<Asset> getAsset(@PathVariable("id") long id)
    {
        Optional<Asset> object = assetService.getAsset(id);
        if(!object.isPresent()) {
            issueTrackerService.recordIssueTracker("Asset not found "+id, "Asset", Priority.MEDIUM);
            throw new AssetNotFoundException("asset not found "+id);
        }
        return new ResponseEntity<>(object.get(), HttpStatus.OK);
    }

	@GetMapping("asset/export-to-excel")
	public ResponseEntity exportToExcel(HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException {

		OutputStream out = null;
		try
		{
			List<Asset> assets = assetService.listAllAsset();

			String fileName = "STATION-" +assets.get(0).getId();
			response.setContentType("application/vnd.ms-excel");
			response.setHeader("Content-Disposition", "attachment; filename="+fileName+".xls");

			WritableWorkbook w = Workbook.createWorkbook(response.getOutputStream());
			WritableSheet s = w.createSheet("Demo", 0);

			// Sheet labels;

			s.addCell(new Label(0, 0, "Title"));
			s.addCell(new Label(1, 0, "Amount"));
			s.addCell(new Label(2, 0, "Description"));
			s.addCell(new Label(3, 0, "Created On"));

			for(int i=0; i<assets.size(); i++){

				int j = 0;

				s.addCell(new Label(j, i+1, ""+assets.get(i).getTitle()));
				s.addCell(new Label(j+1, i+1, ""+assets.get(i).getAmount()));
				s.addCell(new Label(j+2, i+1, ""+assets.get(i).getDescription()));
				s.addCell(new Label(j+4, i+1, ""+assets.get(i).getCreatedOn()));

				j++;
			}

			w.write();
			w.close();

		} catch (Exception e){
			throw new ServletException("Exception in excel download", e);
		} finally{
			if (out != null)
				out.close();
		}
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@RequestMapping(value = "asset/export-to-pdf", method = RequestMethod.GET, produces = MediaType.APPLICATION_PDF_VALUE)
    ResponseEntity<InputStreamResource> newsReport() throws IOException, DocumentException, ParseException {
		List<Asset> asset = (List<Asset>)assetService.listAllAsset(); //incomeService.listAllIncome();
		System.out.println("------------------------------------");
		ByteArrayInputStream bis = GeneratePdfReport.assetPdfReport(asset);
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Disposition", "inline; filename=income.pdf");
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		Document document = new Document(PageSize.A4, 20, 20, 50, 25);
		PdfWriter writer = PdfWriter.getInstance(document, bos);
		GeneratePdfReport event = new GeneratePdfReport();
		writer.setPageEvent(event);
		return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(new InputStreamResource(bis));
	}
}
