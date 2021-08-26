package com.dfq.coeffi.repository.library;

import com.dfq.coeffi.entity.leave.Leave;
import com.dfq.coeffi.entity.leave.LeaveStatus;
import com.dfq.coeffi.entity.library.BookIssue;
import com.dfq.coeffi.entity.library.BookStatus;
import com.dfq.coeffi.entity.master.AcademicYear;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


public interface BookIssueRepository extends JpaRepository<BookIssue, Long>
{
	@Query("update BookIssue e set e.status=false where e.id=:id")
	@Modifying
	public void delete(@Param("id") long id);
	
	@Query("select f from BookIssue f where f.status=true")
	List<BookIssue> findAll();

	public List<BookIssue> findByBookId(long bookId, BookStatus issued, BookStatus renwel, BookStatus returned);

    @Query("select q from BookIssue q where q.id=:id AND (q.bookStatus='ISSUED' OR q.bookStatus='RENWEL')")
    Optional<BookIssue> getListId(@Param("id") long id);

    @Query("select q from BookIssue q where (q.returnDate is NULL AND CURDATE() > q.dueDate)")
    List<BookIssue> getOverdue();

    @Query("select f from BookIssue f where f.bookStatus='RETURNED'")
    List<BookIssue> returnedBookList();

    @Query("select f from BookIssue f where f.bookStatus= :bookStatus and ((f.dueDate) < date(now()))")
    List<BookIssue> overDueBookList(@Param("bookStatus") BookStatus bookStatus);

    @Query("select count(*) from Leave l where l.leaveStatus= :leaveStatus and l.academicYear= :academicYear")
    List<Leave> getCreatedLeaveCountByAcademicYear(@Param("leaveStatus") LeaveStatus leaveStatus, @Param("academicYear") AcademicYear academicYear);

}
