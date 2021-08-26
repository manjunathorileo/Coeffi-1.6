package com.dfq.coeffi.controller.finance;

import com.dfq.coeffi.controller.BaseController;
import com.dfq.coeffi.dto.DateDto;
import com.dfq.coeffi.entity.finance.daybook.DayBook;
import com.dfq.coeffi.entity.finance.income.Income;
import com.dfq.coeffi.entity.finance.income.IncomeCategory;
import com.dfq.coeffi.entity.user.User;
import com.dfq.coeffi.service.UserService;
import com.dfq.coeffi.service.finance.DayBookService;
import com.dfq.coeffi.service.finance.IncomeService;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@Slf4j
public class IncomeController extends BaseController
{
	private IncomeService incomeService;
    private DayBookService dayBookService;
    private UserService userService;

	@Autowired
	public IncomeController(IncomeService incomeService, DayBookService dayBookService, UserService userService)
	{
        this.incomeService = incomeService;
        this.dayBookService = dayBookService;
        this.userService = userService;
	}
	
	/**
	 * @return all the available Income
	 */
	@GetMapping("income")
	public ResponseEntity<List<Income>> getAllIncome()
	{
		log.warn("Request for Income");
		List<Income> income = incomeService.listAllIncome();
		if (CollectionUtils.isEmpty(income))
		{
			throw new EntityNotFoundException("income");
		}
		return new ResponseEntity<>(income, HttpStatus.OK);
	}
	
	@PostMapping(headers = "content-type=multipart/*", consumes = "application/json",value = "income")
	public ResponseEntity<Income> createIncome(@Valid @RequestBody Income income, Principal principal)
	{
	    Optional<IncomeCategory> incomeCategoryObj = incomeService.getIncomeCategory(income.getIncomeCategory().getId());
	    if(!incomeCategoryObj.isPresent())
	    {
	        log.warn("Income category not found");
	        throw new EntityNotFoundException("incomeCategoryObj");
        }
        income.setCreatedOn(DateUtil.getTodayDate());
        income.setIncomeCategory(incomeCategoryObj.get());
        Income persistedIncome = incomeService.createIncome(income, loggedUser(principal));

        if(persistedIncome != null){
            log.info("Income entry created", "INCOME");
            dayBookService.daybookEntry(persistedIncome.getAmount(), persistedIncome.getDescription(), persistedIncome.getCreatedOn(),
                    persistedIncome.getId(), "INCOME");
            log.info("Income entry created in daybook", "INCOME");
        }
        return new ResponseEntity<>(persistedIncome, HttpStatus.CREATED);
	}
	
	/**
	 *
	 * @param id
	 * @return return single income object by passing id
	 */
	@GetMapping("income/{id}")
	private ResponseEntity<Income> getIncome(@PathVariable long id)
	{
		Optional<Income> income = incomeService.getIncome(id);
		if (!income.isPresent()) 
		{
			throw new EntityNotFoundException(Income.class.getSimpleName());
		}
		return new ResponseEntity<>(income.get(), HttpStatus.OK);
	}

	/**
	 * @param income
	 * @return to update the income object
	 */
	@PutMapping("income")
	public ResponseEntity<Income> updateIncome(@Valid @RequestBody Income income, Principal principal)
	{
		Income persistedIncome= incomeService.createIncome(income, loggedUser(principal));
        if(persistedIncome != null){
            log.info("Income updated", "LIABILITY");
            DayBook dayBook = dayBookService.getDaybookByRefName(persistedIncome.getId(), "INCOME");
            if(dayBook != null){
            	dayBookService.delete(dayBook.getId());
            	dayBook.setDescription(income.getTitle());
				dayBook.setAmount(persistedIncome.getAmount());
				dayBookService.save(dayBook);
				log.info("Income updated in daybook as well", "LIABILITY");

			}
        }
		return new ResponseEntity<>(income, HttpStatus.OK);
	}


	/**
	 * @param id
	 * @return permanent delete of income by provided id
	 */
	@DeleteMapping("income/{id}")
	public ResponseEntity<Income> deleteEIncome(@PathVariable long id)
	{
		Optional<Income> income = incomeService.getIncome(id);
		if (!income.isPresent()) 
		{
			log.warn("Unable to delete income with ID : {} not found", id);
			throw new EntityNotFoundException(Income.class.getSimpleName());
		}
		incomeService.deleteIncome(id);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	
	/**
	 * @return all the available income categories between given date
	 */
	
	@PostMapping("income/date-range")
	public ResponseEntity<List<Income>> listOfDetails(@RequestBody DateDto dateDto)
	{
		List<Income> income=incomeService.getIncomeBetweenDate(dateDto.startDate, dateDto.endDate);	
		return new ResponseEntity<>(income, HttpStatus.OK);
	}

	@PostMapping("income/income-category")
	public ResponseEntity<IncomeCategory> createIncomeCategory(@Valid @RequestBody IncomeCategory incomeCategory, Principal principal)
	{
		IncomeCategory persistedIncomeCategory = incomeService.createIncomeCategory(incomeCategory, loggedUser(principal));
		return new ResponseEntity<>(persistedIncomeCategory, HttpStatus.CREATED);
	}

	/**
	 * @return all the available income categories
	 */
	
	@GetMapping("income/income-category")
	public ResponseEntity<List<IncomeCategory>> getAllIncomeCategory()
	{
		List<IncomeCategory> incomeCategories = incomeService.listAllIncomeCategory();
		if (CollectionUtils.isEmpty(incomeCategories))
		{
			throw new EntityNotFoundException("incomeCategories");
		}
		return new ResponseEntity<>(incomeCategories, HttpStatus.OK);
	}
	
	@GetMapping("income/income-category/{id}")
	public ResponseEntity<IncomeCategory> getIncomeCategoryById(@PathVariable long id)
	{
		Optional<IncomeCategory> incomeCategory = incomeService.getIncomeCategory(id);
		if (!incomeCategory.isPresent()) 
		{
			throw new EntityNotFoundException(IncomeCategory.class.getSimpleName());
		}
		return new ResponseEntity<>(incomeCategory.get(), HttpStatus.OK);
	}

	@PutMapping("income/income-category/{id}")
	public ResponseEntity<IncomeCategory> update(@PathVariable long id, @Valid @RequestBody IncomeCategory incomeCategory, Principal principal)
	{
		Optional<IncomeCategory> incomeCategoryUpdate=incomeService.getIncomeCategory(id);
		if (!incomeCategoryUpdate.isPresent())
		{
			log.warn("incomeCategoryUpdate with ID {} not found", id);
			throw new EntityNotFoundException(IncomeCategory.class.getSimpleName());
		}
		incomeCategory.setId(id);
		incomeService.createIncomeCategory(incomeCategory, loggedUser(principal));
		return new ResponseEntity<>(incomeCategoryUpdate.get(), HttpStatus.OK);
	}
	
	@GetMapping("income/export-to-excel")
	public ResponseEntity getReport(HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException
	{
		OutputStream out = null;
		try
		{
			List<Income> income = incomeService.listAllIncome();
			String fileName = "STATION-" +income.get(0).getId();
			response.setContentType("application/vnd.ms-excel");
			response.setHeader("Content-Disposition", "attachment; filename="+fileName+".xls");
			WritableWorkbook w = Workbook.createWorkbook(response.getOutputStream());
			WritableSheet s = w.createSheet("Demo", 0);
			// Sheet labels;
			s.addCell(new Label(0, 0, "Title"));
			s.addCell(new Label(1, 0, "Amount"));
			s.addCell(new Label(2, 0, "Description"));
			s.addCell(new Label(3, 0, "Created On"));
			s.addCell(new Label(4, 0, "Income Category"));
			for(int i=0; i<income.size(); i++)
			{
				int j = 0;
				s.addCell(new Label(j, i+1, ""+income.get(i).getTitle()));
				s.addCell(new Label(j+1, i+1, ""+income.get(i).getAmount()));
				s.addCell(new Label(j+2, i+1, ""+income.get(i).getDescription()));
				s.addCell(new Label(j+3, i+1, ""+income.get(i).getCreatedOn()));
				s.addCell(new Label(j+4, i+1, ""+income.get(i).getIncomeCategory().getName()));
				j++;
			}
			w.write();
			w.close();
		}
		catch (Exception e)
		{
			throw new ServletException("Exception in excel download", e);
		}
		finally
		{
			if (out != null)
				out.close();
		}
		return new ResponseEntity<>(HttpStatus.OK);
	}

    @PostMapping("income/compare-by-month")
    public ResponseEntity<Map> compareExpense(@RequestBody DateDto dateDto) {
        Map<String, List<Income>> map = new HashMap<String, List<Income>>();
        List<Income> income_one = incomeService.getIncomeBetweenDate(dateDto.getFromStartDate(), dateDto.getFromEndDate());
        List<Income> income_two = incomeService.getIncomeBetweenDate(dateDto.getToStartDate(), dateDto.getToEndDate());
        map.put("From_Month", income_one);
        map.put("To_Month", income_two);
        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    private User loggedUser(Principal principal){
        User user = null;
        if(principal.getName() != null){
            user = userService.getUser(Long.parseLong(principal.getName()));
        }
        return user;
    }
    
    @RequestMapping(value = "income/export-to-pdf", method = RequestMethod.GET, produces = MediaType.APPLICATION_PDF_VALUE)
    ResponseEntity<InputStreamResource> newsReport() throws IOException, DocumentException, ParseException
	{
			List<Income> incomes = (List<Income>) incomeService.listAllIncome();
			ByteArrayInputStream bis = GeneratePdfReport.incomePdfReport(incomes);//IncomePdfReport.incomePdfReport(incomes);
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