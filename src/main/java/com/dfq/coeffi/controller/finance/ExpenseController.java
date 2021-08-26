package com.dfq.coeffi.controller.finance;

import com.dfq.coeffi.controller.BaseController;
import com.dfq.coeffi.dto.DateDto;
import com.dfq.coeffi.entity.finance.daybook.DayBook;
import com.dfq.coeffi.entity.finance.expense.*;
import com.dfq.coeffi.entity.user.User;
import com.dfq.coeffi.resource.ExpenseResource;
import com.dfq.coeffi.resource.ExpenseResourceConverter;
import com.dfq.coeffi.service.UserService;
import com.dfq.coeffi.service.finance.DayBookService;
import com.dfq.coeffi.service.finance.ExpenseService;
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

/**
 * @Auther H Kapil Kumar on 19/3/18.
 * @Company Orileo Technologies
 */

@RestController
@Slf4j
public class ExpenseController extends BaseController
{

    private final ExpenseService expenseService;
    private final UserService userService;
    private final DayBookService dayBookService;
    private final ExpenseResourceConverter expenseResourceConverter;

	ExpenseController(ExpenseService expenseService, ExpenseResourceConverter expenseResourceConverter, DayBookService dayBookService, UserService userService) {
        this.expenseService = expenseService;
        this.dayBookService = dayBookService;
        this.expenseResourceConverter = expenseResourceConverter;
        this.userService = userService;
	}

	/**
	 * @return all the available expenses
	 */
	@GetMapping("expense")
	public ResponseEntity<List<Expense>> getAllExpenses() {
		List<Expense> expense = expenseService.getExpenses();
		if (CollectionUtils.isEmpty(expense)) {
			throw new EntityNotFoundException("expense");
		}
		return new ResponseEntity<>(expense, HttpStatus.OK);
	}

	/**
	 * @param year
	 * @return list of available Expense of the particular year
	 */
	@GetMapping("expense/{year}")
	public ResponseEntity<List<Expense>> getExpensesByYear(@PathVariable int year) {
		List<Expense> expenses = expenseService.getExpenseByYear(year);
		if (CollectionUtils.isEmpty(expenses)) {
			throw new EntityNotFoundException("expenses");
		}
		return new ResponseEntity<>(expenses, HttpStatus.OK);
	}

	/**
	 * @param expenseResource
	 * save object to database and return the saved object
	 * @return
	 */

	@PostMapping(headers = "content-type=multipart/*", consumes = "application/json",value = "expense")
	public ResponseEntity<Expense> createExpense(@Valid @RequestBody ExpenseResource expenseResource, Principal principal) {
	    Expense expense = expenseResourceConverter.toExpenseEntity(expenseResource);
		Expense persistedExpense = expenseService.createExpense(expense, loggedUser(principal));
		if(persistedExpense != null){
            log.info("Expense entry created", "EXPENSE");
            String message = persistedExpense.getTitle();
            dayBookService.daybookEntry(persistedExpense.getAmount(), message, persistedExpense.getCreatedOn(),
                    persistedExpense.getId(), "EXPENSE");
            log.info("Expense entry updated in daybook", "EXPENSE");
        }

        return new ResponseEntity<>(persistedExpense, HttpStatus.CREATED);
	}

	/**
	 *
	 * @param id
	 * @return return single expense object by passing id
	 */
	@GetMapping("expense/{id}")
	private ResponseEntity<Expense> getExpense(@PathVariable long id) {
		Optional<Expense> expense = expenseService.getExpense(id);
		if (!expense.isPresent()) {
			throw new EntityNotFoundException(Expense.class.getSimpleName());
		}
		return new ResponseEntity<>(expense.get(), HttpStatus.OK);
	}

	/**
	 * @param id
	 * @return permanent delete of expense route by provided id
	 */
	@DeleteMapping("expense/{id}")
	public ResponseEntity<Expense> deleteExpense(@PathVariable long id) {
		if (!expenseService.isExpenseExists(id)) {
			log.warn("Unable to delete expense route. expense with ID : {} not found", id);
			throw new EntityNotFoundException(Expense.class.getSimpleName());
		}
		expenseService.deleteExpense(id);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	
	
	
	/**
	 * @return all the available assets between given date
	 */
	@PostMapping("expense/filter-by-date")
	public ResponseEntity<List<Expense>> filterByDate(@RequestBody DateDto dateDto)
	{
		List<Expense> expenses=expenseService.getExpenseBetweenDate(dateDto.startDate, dateDto.endDate);	
		return new ResponseEntity<>(expenses, HttpStatus.OK);
		
	}

	/**
	 * @param expense
	 * @return to update the expense object
	 */
	@PutMapping("expense")
	public ResponseEntity<Expense> updateExpense(@Valid @RequestBody Expense expense, Principal principal) {
        Expense persistedExpense = expenseService.createExpense(expense, loggedUser(principal));
        if(persistedExpense != null){
            log.info("Expense updated", "EXPENSE");
            DayBook dayBook = dayBookService.getDaybookByRefName(persistedExpense.getId(), "EXPENSE");
            if(dayBook != null){
            	dayBookService.delete(dayBook.getId());
				dayBook.setDescription(expense.getTitle());
				dayBook.setAmount(persistedExpense.getAmount());
				dayBookService.save(dayBook);
				log.info("Expense updated in daybook as well", "EXPENSE");
			}
        }
		return new ResponseEntity<>(expense, HttpStatus.OK);
	}

	@PostMapping("expense/expense-category")
	public ResponseEntity<Category> createExpenseCategory(@Valid @RequestBody Category category, Principal principal) {
		Category persistedCategory = expenseService.createExpenseCategory(category, loggedUser(principal));

        return new ResponseEntity<>(persistedCategory, HttpStatus.CREATED);
	}

	/**
	 * @return all the available expense categories
	 */
	@GetMapping("expense/expense-category")
	public ResponseEntity<List<Category>> getAllExpenseCategories() {
		List<Category> expenseCategories = expenseService.getExpenseCategories();
		if (CollectionUtils.isEmpty(expenseCategories)) {
			throw new EntityNotFoundException("expenseCategories");
		}
		return new ResponseEntity<>(expenseCategories, HttpStatus.OK);
	}

	/**
	 * @param id
	 * @return expense category object w.r.t ID
	 */
	@GetMapping("expense/expense-category/{id}")
	public ResponseEntity<Category> getExpenseCategoryById(@PathVariable long id) {
		Optional<Category> expenseCategory = expenseService.getExpenseCategory(id);
		if (!expenseCategory.isPresent()) {
			throw new EntityNotFoundException(Expense.class.getSimpleName());
		}
		return new ResponseEntity<>(expenseCategory.get(), HttpStatus.OK);
	}

	@DeleteMapping("expense/expense-category/{id}")
	public ResponseEntity<Category> deleteExpenseCategory(@PathVariable long id) {
		if (!expenseService.isExpenseCategoryExists(id)) {
			log.warn("Unable to delete expense category with ID : {} not found", id);
			throw new EntityNotFoundException(Category.class.getSimpleName());
		}
		expenseService.deleteExpenseCategory(id);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	@PutMapping("expense/expense-category/{id}")
	public ResponseEntity<Category> updateExpenseCategory(@PathVariable long id,
                                                          @Valid @RequestBody Category category, Principal principal) {
		Optional<Category> persistedExpenseCategory = expenseService.getExpenseCategory(id);
		if (!persistedExpenseCategory.isPresent()) {
			log.warn("Expense with ID {} not found", id);
			throw new EntityNotFoundException(Expense.class.getSimpleName());
		}
		category.setId(id);
		expenseService.createExpenseCategory(category, loggedUser(principal));
		return new ResponseEntity<>(category, HttpStatus.OK);
	}

	@PostMapping("expense/expense-category/{categoryId}/subcategory")
	public ResponseEntity<SubCategory> createSubCategory(@PathVariable long categoryId,
														 @Valid @RequestBody SubCategory subCategory) {
		Optional<Category> category = expenseService.getExpenseCategory(categoryId);
		if (!category.isPresent()) {
		    log.error("Category not found");
			throw new EntityNotFoundException(Category.class.getSimpleName());
		} else {
			subCategory.setCategory(category.get());
			expenseService.createExpenseSubCategory(subCategory);
		}
		return new ResponseEntity<>(subCategory, HttpStatus.CREATED);
	}

	@GetMapping("expense/expense-category/{categoryId}/subcategory")
	public ResponseEntity<List<SubCategory>> getExpenseSubcategories(@PathVariable long categoryId) {

		List<SubCategory> subCategories = expenseService.getSubExpenseCategories(categoryId);
		if (CollectionUtils.isEmpty(subCategories)) {
			throw new EntityNotFoundException("subCategories");
		}
		return new ResponseEntity<>(subCategories, HttpStatus.OK);
	}

	// Sub Categories level one

	@PostMapping("expense/expense-category/{subCategoryId}/subcategoryone")
	public ResponseEntity<SubCategoryOne> createSubCategoryOne(@PathVariable long subCategoryId,
															   @Valid @RequestBody SubCategoryOne subCategoryOne) {
		Optional<SubCategory> subCategory = expenseService.getSubCategory(subCategoryId);
		if (!subCategory.isPresent()) {
			throw new EntityNotFoundException(SubCategory.class.getSimpleName());
		} else {
			subCategoryOne.setSubCategory(subCategory.get());
			expenseService.createSubCategoryOne(subCategoryOne);
		}
		return new ResponseEntity<>(subCategoryOne, HttpStatus.CREATED);
	}

	@GetMapping("expense/expense-category/{subCategoryId}/subcategoryone")
	public ResponseEntity<List<SubCategoryOne>> getExpenseSubcategoryOne(@PathVariable long subCategoryId) {

		List<SubCategoryOne> subCategoryOne = expenseService.getSubCategoriesOne(subCategoryId);
		if (CollectionUtils.isEmpty(subCategoryOne)) {
			throw new EntityNotFoundException("subCategoryOne");
		}
		return new ResponseEntity<>(subCategoryOne, HttpStatus.OK);
	}

	@PostMapping("expense/expense-category/{subCategoryOneId}/subcategorytwo")
	public ResponseEntity<SubCategoryTwo> createSubCategoryTwo(@PathVariable long subCategoryOneId,
															   @Valid @RequestBody SubCategoryTwo subCategoryTwo) {
		Optional<SubCategoryOne> subCategoryOne = expenseService.getSubCategoryOne(subCategoryOneId);
		if (!subCategoryOne.isPresent()) 
		{
			throw new EntityNotFoundException(SubCategoryOne.class.getSimpleName());
		}
		else 
		{
			subCategoryTwo.setSubCategoryOne(subCategoryOne.get());
			expenseService.createSubCategoryTwo(subCategoryTwo);
		}
		return new ResponseEntity<>(subCategoryTwo, HttpStatus.CREATED);
	}

	@GetMapping("expense/expense-category/{subCategoryOneId}/subcategorytwo")
	public ResponseEntity<List<SubCategoryTwo>> getExpenseSubcategoryTwo(@PathVariable long subCategoryOneId) {
		List<SubCategoryTwo> subCategoryTwo = expenseService.getSubCategoriesTwo(subCategoryOneId);
		if (CollectionUtils.isEmpty(subCategoryTwo)) {
			throw new EntityNotFoundException("subCategoryTwo");
		}
		return new ResponseEntity<>(subCategoryTwo, HttpStatus.OK);
	}

    @PostMapping("expense/compare-by-month")
    public ResponseEntity<Map> compareExpense(@RequestBody DateDto dateDto) {
        Map<String, List<Expense>> map = new HashMap<String, List<Expense>>();
        List<Expense> expenses_one = expenseService.getExpenseBetweenDate(dateDto.getFromStartDate(), dateDto.getFromEndDate());
        List<Expense> expenses_two = expenseService.getExpenseBetweenDate(dateDto.getToStartDate(), dateDto.getToEndDate());
        map.put("From_Month", expenses_one);
        map.put("To_Month", expenses_two);
        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    private User loggedUser(Principal principal){
        User user = null;
        if(principal.getName() != null){
            user = userService.getUser(Long.parseLong(principal.getName()));
        }
        return user;
    }
    
    @RequestMapping(value = "expense/export-to-pdf", method = RequestMethod.GET, produces = MediaType.APPLICATION_PDF_VALUE)
    ResponseEntity<InputStreamResource> newsReport() throws IOException, DocumentException, ParseException {
			List<Expense> incomes = (List<Expense>) expenseService.getExpenses();
			ByteArrayInputStream bis = GeneratePdfReport.expensePdfReport(incomes);
			HttpHeaders headers = new HttpHeaders();
			headers.add("Content-Disposition", "inline; filename=income.pdf");
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			Document document = new Document(PageSize.A4, 20, 20, 50, 25);
			PdfWriter writer = PdfWriter.getInstance(document, bos);
			GeneratePdfReport event = new GeneratePdfReport();
			writer.setPageEvent(event);
			return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(new InputStreamResource(bis));
	}

	@GetMapping("expense/export-to-excel")
	public ResponseEntity getReport(HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException {

		OutputStream out = null;
		try
		{
			List<Expense> expense = expenseService.getExpenses();

			String fileName = "STATION-" +expense.get(0).getId();
			response.setContentType("application/vnd.ms-excel");
			response.setHeader("Content-Disposition", "attachment; filename="+fileName+".xls");

			WritableWorkbook w = Workbook.createWorkbook(response.getOutputStream());
			WritableSheet s = w.createSheet("Demo", 0);

			// Sheet labels;

			s.addCell(new Label(0, 0, "Title"));
			s.addCell(new Label(1, 0, "Amount"));
			s.addCell(new Label(2, 0, "Description"));
			s.addCell(new Label(3, 0, "Created On"));
			s.addCell(new Label(4, 0, "Expense Category"));

			for(int i=0; i<expense.size(); i++){

				int j = 0;

				s.addCell(new Label(j, i+1, ""+expense.get(i).getTitle()));
				s.addCell(new Label(j+1, i+1, ""+expense.get(i).getAmount()));
				s.addCell(new Label(j+2, i+1, ""+expense.get(i).getDescription()));
				s.addCell(new Label(j+4, i+1, ""+expense.get(i).getCreatedOn()));
				s.addCell(new Label(j+3, i+1, ""+expense.get(i).getCategory().getName()));

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


}