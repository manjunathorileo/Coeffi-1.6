package com.dfq.coeffi.entity.holiday;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name="holiday_defination")
public class HolidayDefination 
{	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column
	private String name;
	
	@Column
	private String startDate;
	
	@Column
	private String endDate;
	
	@Column
	private Boolean isFixed;
	
	@OneToOne(cascade = CascadeType.ALL)
	private HolidayType holidayType;
}