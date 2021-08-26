package com.dfq.coeffi.service.library;

import com.dfq.coeffi.entity.library.Book;

import java.util.List;
import java.util.Optional;

public interface BookService 
{
	Book saveBook(Book book);
	
	List<Book> findAll();
	
	void deleteBook(long id);

	Optional<Book> findOne(long id);

    List<Book> getIdleBooks();

    List<Book> getBooksCount();

    void loadBulkBooks();

}