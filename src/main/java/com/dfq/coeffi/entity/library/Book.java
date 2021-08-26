package com.dfq.coeffi.entity.library;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

/*
 * @author Azhar razvi
 */

@Getter
@Setter
@Entity
@Table(name="book")
public class Book implements Serializable
{
	private static final long serialVersionUID = 1L;
    
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	
	@Column
	private String bookNumber;
	
	@Column
	private String title;
	
	@Column
	private String author;
	
	@Column
	private String edition;
	
	@Column
	private String publisher;
	
	@Column
	private int noOfCopy;
	
	@Column 
	private String selfNo;
	
	@Column
	private Boolean status=true;
	
	@Column
	private BigDecimal cost;
	
	@OneToOne
	private BookCategory bookCategory;

    @Enumerated(EnumType.STRING)
    private BookStatus bookStatus;

    @Column
	private String isbnNumber;

    @Column
	private String bookThumbnail;
}