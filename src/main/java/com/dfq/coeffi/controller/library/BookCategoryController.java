package com.dfq.coeffi.controller.library;

import com.dfq.coeffi.controller.BaseController;
import com.dfq.coeffi.entity.library.BookCategory;
import com.dfq.coeffi.service.library.BookCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
public class BookCategoryController extends BaseController
{
	private final BookCategoryService bookCategoryService;
	
	@Autowired
	public BookCategoryController(BookCategoryService bookCategoryService)
	{
		this.bookCategoryService=bookCategoryService;
	}
	
	 @GetMapping("category_book")
	    public ResponseEntity<List<BookCategory>> list()
	    {
	        List<BookCategory> bookCategory = bookCategoryService.findAll();
	        if (CollectionUtils.isEmpty(bookCategory))
	        {
	             throw new EntityNotFoundException("bookCategory");
	        }
	        return new ResponseEntity<>(bookCategory, HttpStatus.OK);
	    }

	 @PostMapping("category_book")
		public ResponseEntity<BookCategory> create(@Valid @RequestBody BookCategory bookCategory, UriComponentsBuilder ucBuilder)
		{
		 BookCategory persisted = bookCategoryService.saveBookCategory(bookCategory);
		  return new ResponseEntity<>(persisted,HttpStatus.CREATED);
		}
	 
	 @PutMapping("category_book/{id}")
	    public ResponseEntity<BookCategory> update(@PathVariable long id, @Valid @RequestBody BookCategory bookCategory)
	    {
		 Optional<BookCategory> persistedBookCategory = bookCategoryService.findOne(id);
		 if (!persistedBookCategory.isPresent())
		 	{
				throw new EntityNotFoundException(BookCategory.class.getSimpleName());
			}
		 bookCategory.getClass();
		 bookCategoryService.saveBookCategory(bookCategory);
	     return new ResponseEntity<>(bookCategory, HttpStatus.OK);
	    }
	   
	 @DeleteMapping("category_book/{id}")
	    public ResponseEntity<BookCategory> deleteBookCategory(@PathVariable Long id)
	    {
		 Optional<BookCategory> bookCategory = bookCategoryService.findOne(id);
		 if (!bookCategory.isPresent()) 
		 {
				throw new EntityNotFoundException(BookCategory.class.getName());
		 }
	       bookCategoryService.deleteBookCategory(id);
	       return new ResponseEntity<>(HttpStatus.OK);
	    }
}
