package com.dfq.coeffi.entity.finance.income;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Setter
@Getter
@Entity(name="Income_category")
@ToString
public class IncomeCategory
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -2540760368182026290L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column
    private String name;

    @Column
    private boolean active;
}
