package com.dfq.coeffi.servicesimpl.library;

import com.dfq.coeffi.entity.library.BookIssue;
import com.dfq.coeffi.entity.library.BookStatus;
import com.dfq.coeffi.repository.library.BookIssueRepository;
import com.dfq.coeffi.service.library.BookIssueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static java.util.Optional.ofNullable;

@Service
public class BookIssueServiceImpl implements BookIssueService
{
	private final BookIssueRepository bookIssueRepository;
	
	@Autowired
	public BookIssueServiceImpl(BookIssueRepository bookIssueRepository) 
	{
		this.bookIssueRepository=bookIssueRepository;
	}
	
	@Override
	public BookIssue saveBookIssue(BookIssue bookIssue)
	{
		return bookIssueRepository.save(bookIssue);
	}

	@Override
	public List<BookIssue> findAll()
	{
		return bookIssueRepository.findAll();
	}

	@Override
	public void deleteBookIssue(long id)
	{
		bookIssueRepository.delete(id);
	}

	@Override
	public Optional<BookIssue> fineOne(long id)
	{
		return ofNullable(bookIssueRepository.findOne(id));
	}

	@Override
	public List<BookIssue> findByBookId(long bookId, BookStatus issued, BookStatus returned, BookStatus renwel)
	{
		return bookIssueRepository.findByBookId(bookId, issued, renwel, returned);
	}

    @Override
    public Optional<BookIssue> getListId(long id) {
        return bookIssueRepository.getListId(id);
    }

    @Override
    public List<BookIssue> getOverdue() {
        return bookIssueRepository.getOverdue();
    }

	@Override
	public List<BookIssue> returnedBookList() {
		return bookIssueRepository.returnedBookList();
	}

	@Override
	public List<BookIssue> overDueBookList(BookStatus bookStatus) {
		return bookIssueRepository.overDueBookList(bookStatus);
	}

}
