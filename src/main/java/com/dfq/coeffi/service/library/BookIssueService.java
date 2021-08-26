package com.dfq.coeffi.service.library;

import com.dfq.coeffi.entity.library.BookIssue;
import com.dfq.coeffi.entity.library.BookStatus;

import java.util.List;
import java.util.Optional;

public interface BookIssueService
{
	BookIssue saveBookIssue(BookIssue bookIssue);
	
	List<BookIssue> findAll();
	
	void deleteBookIssue(long id);
	
	Optional<BookIssue> fineOne(long id);
	
	List<BookIssue> findByBookId(long bookId, BookStatus issued, BookStatus returned, BookStatus renwel);

    Optional<BookIssue> getListId(long id);

    List<BookIssue> getOverdue();

	List<BookIssue> returnedBookList();

	List<BookIssue> overDueBookList(BookStatus bookStatus);
}
