package com.dfq.coeffi.controller.finance;


import com.dfq.coeffi.controller.BaseController;
import com.dfq.coeffi.dto.DateDto;
import com.dfq.coeffi.entity.finance.daybook.DayBook;
import com.dfq.coeffi.entity.finance.expense.Liability;
import com.dfq.coeffi.entity.user.User;
import com.dfq.coeffi.service.UserService;
import com.dfq.coeffi.service.finance.DayBookService;
import com.dfq.coeffi.service.finance.LiabilityService;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
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
@RestController
public class LiabilityController extends BaseController
{
    private final LiabilityService liabilityService;
    private final DayBookService dayBookService;
    private final UserService userService;

	@Autowired
	public LiabilityController(LiabilityService liabilityService, DayBookService dayBookService, UserService userService)
	{
        this.liabilityService = liabilityService;
        this.dayBookService = dayBookService;
        this.userService = userService;
	}
	
	@GetMapping("liability")
	public ResponseEntity<List<Liability>> listAllLiability()
	{
		List<Liability> liabilities = liabilityService.listAllLiability();
		if (CollectionUtils.isEmpty(liabilities))
		{
			throw new EntityNotFoundException("liabilities");
		}
		return new ResponseEntity<>(liabilities, HttpStatus.OK);
	}
	
	@PostMapping("liability")
	public ResponseEntity<Liability> createLiability(@Valid @RequestBody Liability liability, Principal principal)
	{
        liability.setCreatedOn(DateUtil.getTodayDate());
		Liability persistedLiability = liabilityService.createLiability(liability, loggedUser(principal));

		// Will send the data to daybook as well
        if(persistedLiability != null){
            log.info("Liability entry created", "LIABILITY");
			String message = persistedLiability.getTitle();

			dayBookService.daybookEntry(persistedLiability.getAmount(), message, persistedLiability.getCreatedOn(),
                    persistedLiability.getId(), "LIABILITY");
            log.info("Liability entry created in daybook", "LIABILITY");
        }
		return new ResponseEntity<>(persistedLiability, HttpStatus.CREATED);
	}
	
	@PutMapping("liability")
	public ResponseEntity<Liability> update(@Valid @RequestBody Liability liability, Principal principal)
	{
		Liability persistedLiability = liabilityService.createLiability(liability, loggedUser(principal));
        if(persistedLiability != null){
            log.info("liability updated", "LIABILITY");
            DayBook dayBook = dayBookService.getDaybookByRefName(persistedLiability.getId(), "LIABILITY");
            if(dayBook != null){

            	dayBookService.delete(dayBook.getId());
            	dayBook.setDescription(liability.getTitle());
				dayBook.setAmount(persistedLiability.getAmount());
				dayBookService.save(dayBook);
				log.info("Entry updated in daybook as well", "LIABILITY");
			}

        }
		return new ResponseEntity<>(persistedLiability, HttpStatus.OK);
	}
	
	/**
	 * @return all the available liability categories between given date
	 */
	@PostMapping("liability/date-range")
	public ResponseEntity<List<Liability>> listOfDetails(@RequestBody DateDto dateDto)
	{
		List<Liability> liability=liabilityService.getLiabilityBetweenDate(dateDto.startDate, dateDto.endDate);	
		return new ResponseEntity<>(liability, HttpStatus.OK);
	}
	
	
	@DeleteMapping("liability/{id}")
	public ResponseEntity<Liability> deleteLiability(@PathVariable long id)
	{
		Optional<Liability> liability=liabilityService.getLiability(id);
		
		if (!liability.isPresent())
		{
			throw new EntityNotFoundException(Liability.class.getName());
		}
		liabilityService.deleteLiability(id);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

    private User loggedUser(Principal principal){
        User user = null;
        if(principal.getName() != null){
            user = userService.getUser(Long.parseLong(principal.getName()));
        }
        return user;
    }

	@GetMapping("liability/export-to-excel")
	public ResponseEntity exportToExcel(HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException {

		OutputStream out = null;
		try
		{
			List<Liability> liabilities = liabilityService.listAllLiability();

			String fileName = "STATION-" +liabilities.get(0).getId();
			response.setContentType("application/vnd.ms-excel");
			response.setHeader("Content-Disposition", "attachment; filename="+fileName+".xls");

			WritableWorkbook w = Workbook.createWorkbook(response.getOutputStream());
			WritableSheet s = w.createSheet("Demo", 0);

			// Sheet labels;

			s.addCell(new Label(0, 0, "Title"));
			s.addCell(new Label(1, 0, "Amount"));
			s.addCell(new Label(2, 0, "Description"));
			s.addCell(new Label(3, 0, "Created On"));

			for(int i=0; i<liabilities.size(); i++){

				int j = 0;

				s.addCell(new Label(j, i+1, ""+liabilities.get(i).getTitle()));
				s.addCell(new Label(j+1, i+1, ""+liabilities.get(i).getAmount()));
				s.addCell(new Label(j+2, i+1, ""+liabilities.get(i).getDescription()));
				s.addCell(new Label(j+4, i+1, ""+liabilities.get(i).getCreatedOn()));

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
    
    @RequestMapping(value = "liability/export-to-pdf", method = RequestMethod.GET, produces = MediaType.APPLICATION_PDF_VALUE)
    ResponseEntity<InputStreamResource> newsReport() throws IOException, DocumentException, ParseException {
			List<Liability> liabilty = (List<Liability>) liabilityService.listAllLiability();
			ByteArrayInputStream bis = GeneratePdfReport.liabilityPdfReport1(liabilty);//IncomePdfReport.incomePdfReport(liabilty);
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