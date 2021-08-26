package com.dfq.coeffi.controller.communication;

import com.dfq.coeffi.controller.BaseController;
import com.dfq.coeffi.entity.communication.ManageNews;
import com.dfq.coeffi.service.communication.ManageNewsService;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
public class ManageNewsController extends BaseController
{
	private static final String UPLOADED_FOLDER = null;
	@Autowired
	private ManageNewsService manageNewsService;

	@Autowired
	public ManageNewsController(ManageNewsService manageNewsService) {
		
		this.manageNewsService = manageNewsService;
	}
	
	
	@GetMapping("managenews")
	public ResponseEntity<List<ManageNews>> listofNews()
	{
		List<ManageNews> managenews=manageNewsService.listofNews();
		if(CollectionUtils.isEmpty(managenews))
		{
			throw new EntityNotFoundException("managenews");
		}
		return new ResponseEntity<>(managenews, HttpStatus.OK);
	}
	
	@PostMapping("managenews")
	public ResponseEntity<ManageNews> createNewNews(@Valid @RequestBody final ManageNews managenews)
	{
		ManageNews persisted=manageNewsService.createNews(managenews);
		return new ResponseEntity<ManageNews>(persisted, HttpStatus.CREATED);
		
	}
	
	@PutMapping("managenews/{id}")
	public ResponseEntity<ManageNews> update(@PathVariable long id, @RequestBody ManageNews managenews)
	{
		Optional<ManageNews> persistedManagenews = manageNewsService.getNews(id);
		if (!persistedManagenews.isPresent()) 
		{
			throw new EntityNotFoundException(ManageNews.class.getName());
		}
		managenews.setId(id);
		manageNewsService.createNews(managenews);
		return new ResponseEntity<ManageNews>(managenews, HttpStatus.OK);
	}
	
	@DeleteMapping("managenews/{id}")
    ResponseEntity<ManageNews> deleteManagenews(@PathVariable long id)
	{
		Optional<ManageNews> managenews = manageNewsService.getNews(id);
			if	(!managenews.isPresent())
		{
			throw new EntityNotFoundException("managenews");
		}
		manageNewsService.deletenews(id);
			return new ResponseEntity<>(HttpStatus.OK);
	}

	@GetMapping("managenews/todaynews/{date}")
	public ResponseEntity<List<ManageNews>> getTodayNews(@PathVariable("date")  @DateTimeFormat(pattern="yyyy-MM-dd")Date date)
	{
		List<ManageNews> managenews=manageNewsService.getTodayNews(date);
		System.out.println("========================getting data from date==================="+date);
		return new ResponseEntity<>(managenews, HttpStatus.OK);
	}
	
	@GetMapping("managenews/latestnews")
	public ResponseEntity<List<ManageNews>> getNewsListByDesc()
	{
		List<ManageNews> managenews=manageNewsService.getNewsListByDesc();

		if(CollectionUtils.isEmpty(managenews))
		{
			throw new EntityNotFoundException("managenews");
		}
		return new ResponseEntity<List<ManageNews>>(managenews, HttpStatus.OK);
		
	}

	@GetMapping("managenews/author/{name}")
	public ResponseEntity<List<ManageNews>> getNewsListByAuthor(@PathVariable("name") String name)
	{
		List<ManageNews> managenews=manageNewsService.getNewsListByAuthor(name);

		if(CollectionUtils.isEmpty(managenews))
		{
			throw new EntityNotFoundException("managenews");
		}
		return new ResponseEntity<List<ManageNews>>(managenews, HttpStatus.OK);

	}

	@GetMapping("managenews/venue/{place}")
	public ResponseEntity<List<ManageNews>> getNewsByVenue(@PathVariable("place") String place)
	{
		List<ManageNews> managenews=manageNewsService.getNewsByVenue(place);

		if(CollectionUtils.isEmpty(managenews))
		{
			throw new EntityNotFoundException("managenews");
		}
		return new ResponseEntity<List<ManageNews>>(managenews, HttpStatus.OK);

	}

	@PostMapping("managenews/import/{file}")
	public void mapReapExcelDatatoDB(@PathVariable("file") MultipartFile reapExcelDataFile) throws IOException {

	   List<ManageNews> tempStudentList = new ArrayList<ManageNews>();
		XSSFWorkbook workbook = new XSSFWorkbook(reapExcelDataFile.getInputStream());
		XSSFSheet worksheet = workbook.getSheetAt(0);

		for(int i=1;i<worksheet.getPhysicalNumberOfRows() ;i++) {
			ManageNews tempStudent = new ManageNews();

			XSSFRow row = worksheet.getRow(i);

			tempStudent.setId((int) row.getCell(0).getNumericCellValue());
			tempStudent.setContent(row.getCell(1).getStringCellValue());
				tempStudentList.add(tempStudent);
		}
	}
//    @RequestMapping(value = "/pdfreport", method = RequestMethod.GET, produces = MediaType.APPLICATION_PDF_VALUE)
//
//    ResponseEntity<InputStreamResource> newsReport() throws IOException {
//
//		List<ManageNews> cities = (List<ManageNews>) manageNewsService.findAll();
//
//		ByteArrayInputStream bis = GeneratePdfReport.citiesReport(cities);
//
//		HttpHeaders headers = new HttpHeaders();
//		headers.add("Content-Disposition", "inline; filename=citiesreport.pdf");
//
//		return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF)
//				.body(new InputStreamResource(bis));
//	}


   }

