package com.dfq.coeffi.controller.finance;

import com.dfq.coeffi.controller.BaseController;
import com.dfq.coeffi.entity.finance.daybook.DayBook;
import com.dfq.coeffi.service.finance.DayBookService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

@RestController
public class DayBookController extends BaseController {
	
	private final DayBookService dayBookService;

    public DayBookController(DayBookService dayBookService) {
        this.dayBookService = dayBookService;
    }
    
    @PostMapping("daybook")
    public ResponseEntity<DayBook> createDayBook(@RequestBody DayBook dayBook) throws URISyntaxException {
       
    	DayBook persistedObject = dayBookService.save(dayBook);
        return ResponseEntity.created(new URI("/" + persistedObject.getId()))
            .body(persistedObject);
    }
    
    @PutMapping("daybook/{id}")
    public ResponseEntity<DayBook> updateDayBook(@PathVariable long id, @Valid @RequestBody DayBook dayBook)
    {	
        Optional<DayBook> persistedObject = dayBookService.getDayBook(id);
        if (!persistedObject.isPresent()) {
            throw new EntityNotFoundException(DayBook.class.getSimpleName());
        }
        dayBook.setId(id);
        dayBookService.save(dayBook);
        return new ResponseEntity<>(dayBook, HttpStatus.OK);
    }
          
	@GetMapping("daybook")
    public ResponseEntity<List<DayBook>> getDayBooks() {
        List<DayBook> persistenceObject = dayBookService.findAllDaybook();
        if(CollectionUtils.isEmpty(persistenceObject))
        {
        	throw new EntityNotFoundException(DayBook.class.getName());
        }
		return new ResponseEntity<List<DayBook>>(persistenceObject, HttpStatus.OK);
    }
	
	@GetMapping("daybook/{id}")
    public ResponseEntity<DayBook> getDayBook(@PathVariable Long id) {
		Optional<DayBook> persistenceObject = dayBookService.getDayBook(id);
		if(!persistenceObject.isPresent())
		{
			throw new EntityNotFoundException(DayBook.class.getName());
		}
		return new ResponseEntity<DayBook>(persistenceObject.get(), HttpStatus.OK);
	}
	
	@DeleteMapping("daybook/{id}")
    public ResponseEntity<DayBook> deleteDayBook(@PathVariable Long id) {
		Optional<DayBook> persistenceObject = dayBookService.getDayBook(id);
		if(!persistenceObject.isPresent())
		{
			throw new EntityNotFoundException(DayBook.class.getName());
		}
		dayBookService.delete(id);
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	/*@GetMapping("{studentId}/{schoolId}")
    public ResponseEntity<List<DayBook>> getReportForCBSE(@PathVariable Long studentId,@PathVariable Long schoolId) 
	{
		List<DayBook> persistenceObject = dayBookService.getDayBookFilter(studentId, schoolId);
		return new ResponseEntity<List<DayBook>>(persistenceObject,HttpStatus.OK);   
	}*/
}