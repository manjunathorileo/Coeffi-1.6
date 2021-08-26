package com.dfq.coeffi.servicesimpl.library;

import com.dfq.coeffi.entity.library.BookCategory;
import com.dfq.coeffi.repository.library.BookCategoryRepository;
import com.dfq.coeffi.service.library.BookCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static java.util.Optional.ofNullable;

@Service
public class BookCategoryServiceImpl implements BookCategoryService
{
	private final BookCategoryRepository bookCategoryRepository;
	
	@Autowired
	public BookCategoryServiceImpl(BookCategoryRepository bookCategoryRepository) 
	{
		this.bookCategoryRepository=bookCategoryRepository;
	}

	@Override
	public BookCategory saveBookCategory(BookCategory bookCategory) {
		
		return bookCategoryRepository.save(bookCategory);
	}

	@Override
	public List<BookCategory> findAll()
	{
		return bookCategoryRepository.findAll();
	}

	@Override
	public void deleteBookCategory(long id)
	{
		bookCategoryRepository.delete(id);
		
	}

	@Override
	public Optional<BookCategory> findOne(long id)
	{
		
		return ofNullable(bookCategoryRepository.findOne(id));
	}

		
	
}
