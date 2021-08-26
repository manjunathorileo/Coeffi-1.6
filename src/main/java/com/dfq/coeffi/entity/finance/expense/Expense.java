package com.dfq.coeffi.entity.finance.expense;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @Auther H Kapil Kumar on 19/3/18.
 * @Company Orileo Technologies
 */

@Setter
@Getter
@Entity
@ToString
public class Expense implements Serializable{

	/**
	 * 
	 */
	
	private static final long serialVersionUID = -3347760999933537332L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	private BigDecimal amount;

    private String title;

    private String description;

	@Temporal(TemporalType.DATE)
	@DateTimeFormat(pattern="yyyy-MM-dd")
	private Date createdOn;	
	
	
    @Enumerated(EnumType.STRING)
    @Column(length = 25)
    private ExpenseType  expenseType;

    @OneToOne
    private Category category;

    @OneToOne
    private SubCategory subCategory;

    @OneToOne
    private SubCategoryOne subCategoryOne;

    @OneToOne
    private SubCategoryTwo subCategoryTwo;

    private boolean approval;

    private String approvedBy;

    @ElementCollection
    @LazyCollection(LazyCollectionOption.FALSE)
    @Embedded
    private List<ExpenseRejection> rejectionMessage;

}