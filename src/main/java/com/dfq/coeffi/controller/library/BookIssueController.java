package com.dfq.coeffi.controller.library;

import com.dfq.coeffi.controller.BaseController;
import com.dfq.coeffi.entity.library.Book;
import com.dfq.coeffi.entity.library.BookIssue;
import com.dfq.coeffi.entity.library.BookStatus;
import com.dfq.coeffi.service.library.BookIssueService;
import com.dfq.coeffi.service.library.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;

@RestController
public class BookIssueController extends BaseController
{
	private final BookIssueService bookIssueService;
	private final BookService bookService;
	
	@Autowired
	public BookIssueController(BookIssueService bookIssueService, BookService bookService)
	{
		this.bookIssueService=bookIssueService;
		this.bookService=bookService;
	}
	
	@GetMapping("book_issue")
	public ResponseEntity<List<BookIssue>> list()
	{
		List<BookIssue> bookIssue = bookIssueService.findAll();
		if (CollectionUtils.isEmpty(bookIssue))
		{
			throw new EntityNotFoundException("bookIssue");
		}
		return new ResponseEntity<List<BookIssue>>(bookIssue, HttpStatus.OK);
	}
	
	@GetMapping("book_issue/{bookId}")
	public ResponseEntity<List<BookIssue>> findByBook(@PathVariable long bookId)
	{
	 List<BookIssue> bookIssue= bookIssueService.findByBookId(bookId,BookStatus.ISSUED,BookStatus.RETURNED,BookStatus.RENWEL);
       if (CollectionUtils.isEmpty(bookIssue))
       {
           throw new EntityNotFoundException("bookIssue");
       }
	return new ResponseEntity<>(bookIssue, HttpStatus.OK);
		 
	}

    @DeleteMapping("book_issue/{id}")
    public ResponseEntity<BookIssue> deleteBookIssue(@PathVariable Long id) {
        Optional<BookIssue> bookIssue = bookIssueService.fineOne(id);

        if (!bookIssue.isPresent())
        {
            throw new EntityNotFoundException(BookIssue.class.getName());
        }

        BookIssue issuedBook = bookIssue.get();
        issuedBook.setBookStatus(BookStatus.RETURNED);
        bookIssueService.saveBookIssue(issuedBook);

        bookIssueService.deleteBookIssue(id);
        return new ResponseEntity<>(issuedBook,HttpStatus.OK);
    }

    @PostMapping("book_issue")
    public ResponseEntity<BookIssue> create(@Valid @RequestBody BookIssue bookIssue)
    {
        if(bookIssue.getBookStatus() == BookStatus.ISSUED || bookIssue.getBookStatus() == BookStatus.RENWEL ) {
            Calendar cal=Calendar.getInstance();
            cal.add(Calendar.DATE, 7);
            bookIssue.setDueDate(cal.getTime());
        }

        if (bookIssue.getBook()!= null) {
            Optional<Book> book = bookService.findOne(bookIssue.getBook().getId());
            book.get().setBookStatus(bookIssue.getBookStatus());
            Book bookPersisted = bookService.saveBook(book.get());
            bookIssue.setBook(book.get());
        }

        BookIssue persisted = bookIssueService.saveBookIssue(bookIssue);
        HttpHeaders headers = new HttpHeaders();
        return new ResponseEntity<>(persisted,HttpStatus.CREATED);
    }

	@PutMapping("book_issue/{id}")
	public ResponseEntity<BookIssue> update(@PathVariable long id, @Valid @RequestBody BookIssue bookIssue) {
		Optional<BookIssue> persistedBookIssue = bookIssueService.fineOne(id);
		if (!persistedBookIssue.isPresent())
		{
			throw new EntityNotFoundException(BookIssue.class.getSimpleName());
		}
		bookIssue.setId(id);

        Optional<Book> book = bookService.findOne(persistedBookIssue.get().getBook().getId());

        if (bookIssue.getBookStatus().toString() == "RETURNED")
        {
            book.get().setBookStatus(BookStatus.valueOf("AVAILABLE"));
        }
        else {
              book.get().setBookStatus(bookIssue.getBookStatus());
        }
        Book bookPersisted = bookService.saveBook(book.get());

		bookIssueService.saveBookIssue(bookIssue);
		return new ResponseEntity<>(bookIssue, HttpStatus.OK);
	}


    @GetMapping("book_issue/book-renwel/{id}")
    public ResponseEntity<BookIssue> getListId(@PathVariable("id") long id)
    {
        Optional<BookIssue> bookList= bookIssueService.getListId(id);
        bookList.get().setBookStatus(BookStatus.RENWEL);

        Calendar cal=Calendar.getInstance();
        cal.add(Calendar.DATE, 7);
        bookList.get().setDueDate(cal.getTime());

        bookIssueService.saveBookIssue(bookList.get());
        return new ResponseEntity<>(bookList.get(),HttpStatus.OK);
    }

//    @GetMapping("book-list/{studentId}")
//    public ResponseEntity<List<BookIssue>> getBookListByStudentId(@PathVariable ("studentId") long studentId) {
//        Optional<Student> student = studentService.getStudent(studentId);
//        List<BookIssue> bookIssues = bookIssueService.getBookIssueListByStudentId(student.get());
//        if (CollectionUtils.isEmpty(bookIssues)) {
//            throw new EntityNotFoundException("leaves");
//        }
//        return new ResponseEntity<>(bookIssues, HttpStatus.OK);
//    }

    @GetMapping("book_issue/return_book")
    public ResponseEntity<List<BookIssue>> returnedBookList()
    {
        List<BookIssue> bookIssue =   bookIssueService.returnedBookList();
        if (CollectionUtils.isEmpty(bookIssue))
        {
            throw new EntityNotFoundException("bookReturn");
        }
        return new ResponseEntity<List<BookIssue>>(bookIssue, HttpStatus.OK);
    }

    @GetMapping("book_issue/over-due")
    public ResponseEntity<List<BookIssue>> getOverDueList() {
        List<BookIssue> bookIssues = bookIssueService.overDueBookList(BookStatus.ISSUED);
        return new ResponseEntity<>(bookIssues,HttpStatus.OK);
    }
}