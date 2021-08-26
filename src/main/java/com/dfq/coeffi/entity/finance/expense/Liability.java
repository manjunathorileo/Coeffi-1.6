package com.dfq.coeffi.entity.finance.expense;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
@Entity
@ToString
public class Liability implements Serializable
{

	private static final long serialVersionUID = 4037449474776023573L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column
	private String title;

	@Column(length = 1000)
	private String description;

	@Column(length = 25)
	private BigDecimal amount;

	private Date createdOn;
}