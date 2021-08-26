package com.dfq.coeffi.servicesimpl.library;

import com.dfq.coeffi.entity.library.Book;
import com.dfq.coeffi.repository.library.BookRepository;
import com.dfq.coeffi.service.library.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static java.util.Optional.ofNullable;

@Service
public class BookServiceImpl implements BookService
{
	private final BookRepository bookRepository;
	
	@Autowired
	public BookServiceImpl(BookRepository bookRepository)
	{
		this.bookRepository=bookRepository;
	}

	@Override
	public Book saveBook(Book book)
	{
		return bookRepository.save(book);
	}

	@Override
	public List<Book> findAll()
	{
		return bookRepository.findAll();
	}

	@Override
	public void deleteBook(long id)
	{
		bookRepository.delete(id);	
	}

	@Override
	public Optional<Book> findOne(long id)
	{
		return ofNullable(bookRepository.findOne(id));
	}

    @Override
    public List<Book> getIdleBooks(){return bookRepository.getIdleBooks();}

    @Override
    public List<Book> getBooksCount(){return bookRepository.getBooksCount();}

    @Override
    public void loadBulkBooks() {bookRepository.loadBulkBooks();
	}
}
