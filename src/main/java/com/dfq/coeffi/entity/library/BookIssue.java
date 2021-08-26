package com.dfq.coeffi.entity.library;

import com.dfq.coeffi.entity.master.GeneralMetadata;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/*
 * @author Azhar razvi
 */

@Getter
@Setter
@Entity
@Table(name="book_issue")
public class BookIssue extends GeneralMetadata implements Serializable
{
	private static final long serialVersionUID = -5401248291343677197L;
	
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@Enumerated(EnumType.STRING)
	@Column(length = 45)
	private BookStatus bookStatus;
	
	@Column(name="issue_date")
	private Date issueDate;

	@Column(name="return_date")
	@CreationTimestamp
	@Temporal(TemporalType.TIMESTAMP)
	@JsonFormat(pattern ="yyyy-MM-dd")
    private Date returnDate;
	
	@Column(name="renwel_date")
    private Date renwelDate;
	
	@Column(name="due_date")
    private Date dueDate;
	
	@Column
	private BigDecimal fine;
	
	@Column
	private Boolean status=true;
	
	@OneToOne(cascade = CascadeType.ALL)
	private Book book;
	
}
