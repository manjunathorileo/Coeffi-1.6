package com.dfq.coeffi.repository.library;

import com.dfq.coeffi.entity.library.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long>
{
	@Query("update Book e set e.status=false where e.id=:id")
	@Modifying
	public void delete(@Param("id") long id);
	
	@Query("select f from Book f where f.status=true")
	List<Book> findAll();


    @Query("From Book as b Where b.id in (select book.id from BookIssue Where date(now()) - date(returnDate) > 1) OR b.bookStatus is NULL")
    List<Book> getIdleBooks();

    @Query("Select b.title, b.author, b.edition, count(1) as cnt from Book b group by b.title, b.author, b.edition")
    List<Book> getBooksCount();

    @Modifying
    @Transactional
    @Query(value= "LOAD DATA LOCAL INFILE 'D:/books.csv' INTO TABLE book FIELDS TERMINATED BY ',' " +
            " ENCLOSED BY '\"' " +
            " LINES TERMINATED BY '\\n' IGNORE 1 ROWS (@column1, @column2, @column3, @column4, @column5, @column6, @column7, @column8, @column9, @column10, @column11)" +
            " set author=@column1, book_number=@column2, cost=@column3, edition= @column4, no_of_copy=@column5, publisher=@column6, self_no=@column7, status=@column8, title=@column9, book_category_id=@column10, book_status=@column11 ", nativeQuery = true)
    public void loadBulkBooks();

}
