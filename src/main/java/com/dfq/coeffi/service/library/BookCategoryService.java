package com.dfq.coeffi.service.library;

import com.dfq.coeffi.entity.library.BookCategory;

import java.util.List;
import java.util.Optional;

public interface BookCategoryService 
{
	BookCategory saveBookCategory(BookCategory bookCategory);
	
	List<BookCategory> findAll();
	
	void deleteBookCategory(long id);
	
	Optional<BookCategory> findOne(long id);
	
}
