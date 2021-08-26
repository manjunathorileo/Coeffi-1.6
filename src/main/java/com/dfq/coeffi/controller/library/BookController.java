package com.dfq.coeffi.controller.library;

import com.dfq.coeffi.controller.BaseController;
import com.dfq.coeffi.dto.LibraryDashBoardDto;
import com.dfq.coeffi.entity.library.Book;
import com.dfq.coeffi.entity.library.BookCategory;
import com.dfq.coeffi.service.library.BookCategoryService;
import com.dfq.coeffi.service.library.BookIssueService;
import com.dfq.coeffi.service.library.BookService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
public class BookController extends BaseController
{
	private final BookService bookService;
	private final BookIssueService bookIssueService;
	private final BookCategoryService bookCategoryService;
	
	public BookController(BookService bookService,BookCategoryService bookCategoryService, BookIssueService bookIssueService)
	{
		this.bookService=bookService;
        this.bookCategoryService=bookCategoryService;
        this.bookIssueService=bookIssueService;
	}
	
	@GetMapping("book")
	public ResponseEntity<List<Book>> list()
	{
		List<Book> book = bookService.findAll();
		if (CollectionUtils.isEmpty(book))
		{
			throw new EntityNotFoundException("book");
		}
		return new ResponseEntity<List<Book>>(book, HttpStatus.OK);
	}

	@DeleteMapping("book/{id}")
	public ResponseEntity<Book> deleteBook(@PathVariable Long id)
	{
		Optional<Book> book = bookService.findOne(id);
		if (!book.isPresent())
		{
			throw new EntityNotFoundException(Book.class.getName());
		}
		bookService.deleteBook(id);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	
	@PutMapping("book/{id}")
	public ResponseEntity<Book> update(@PathVariable long id, @Valid @RequestBody Book book)
	{
		Optional<Book> persistedBook = bookService.findOne(id);
		if (!persistedBook.isPresent())
		{
			throw new EntityNotFoundException(Book.class.getSimpleName());
		}
		book.setId(id);
		bookService.saveBook(book);
		return new ResponseEntity<>(book, HttpStatus.OK);
	}
	
	@PostMapping("book")
	public ResponseEntity<Book> create(@Valid @RequestBody Book book)
	{
        if (book.getBookCategory()!= null) {
            Optional<BookCategory> bookCategory = bookCategoryService.findOne(book.getBookCategory().getId());
            book.setBookCategory(bookCategory.get());
        }
        Book persisted = bookService.saveBook(book);
        return new ResponseEntity<>(persisted,HttpStatus.CREATED);
	}


    @GetMapping("book/library-dashboard")
    public ResponseEntity<LibraryDashBoardDto> getLibraryDashBoard()
    {
        LibraryDashBoardDto libraryDashBoardDto = new LibraryDashBoardDto();
        long totalCount = bookService.findAll().size();
        long issuedCount = bookIssueService.findAll().size();
        long overdueCount = bookIssueService.getOverdue().size();
        long idleBookCount = 0;
        if (bookService.getIdleBooks() != null && bookService.getIdleBooks().size() != 0) {
            idleBookCount = bookService.getIdleBooks().size();
        }
        libraryDashBoardDto.setTotalBookCount(totalCount);
        libraryDashBoardDto.setIssuedCount(issuedCount);
        libraryDashBoardDto.setOverdueCount(overdueCount);
        libraryDashBoardDto.setIdleCount(idleBookCount);
        return new ResponseEntity<>(libraryDashBoardDto, HttpStatus.OK);
    }

    @GetMapping("book/book-count")
    public ResponseEntity<List<Book>> getBooksCount()
    {
        List<Book> bookCountDto = bookService.getBooksCount();
        return new ResponseEntity<>(bookCountDto, HttpStatus.OK);
    }

    @GetMapping("book/library-csv-import")
    public ResponseEntity<List<Book>> importExcel()
    {
        bookService.loadBulkBooks();
        return new ResponseEntity<>(HttpStatus.OK);
    }
}