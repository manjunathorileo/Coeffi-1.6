package com.dfq.coeffi.entity.finance.expense;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Setter
@Getter
@Entity
@Table(name="asset_tracking")
public class Asset {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@Column(length = 45)
	private String title;

	@Column(length = 1000)
    private String description;

	private Date createdOn;

	@Column(length = 25)
    private BigDecimal amount;
	
}