package com.dfq.coeffi.entity.finance.daybook;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Setter
@Getter
@Entity
@Table(name="day_book")
public class DayBook implements Serializable{

	private static final long serialVersionUID = 7572641206266292176L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@Column(name="reciept_number")
	private String recieptNumber;
	
	@Column(name="mode_of_pay")
	private String modeOfPay;
	
	@Column(name="cheque_number")
	private String chequeNumber;
	
	@Column(name="account_number")
	private String accountNumber;	
	
	@Temporal(TemporalType.TIMESTAMP)
    @UpdateTimestamp
    @JsonFormat(pattern ="yyyy-MM-dd")
	@Column(name="paid_date")
	private Date paidDate;
	
	@Column
	private BigDecimal amount;
	
	private String student;
	
	private String academicYear;
	
	private String studentClass;
	
	private String section;

	private String description;

    @Column(nullable = true)
    private long refId;

	@Column
	private String refName;
}