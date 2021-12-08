package com.dfq.coeffi.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;
import java.util.List;

@Setter
@Getter
public class DateDto {

	@Temporal(TemporalType.DATE)
	@DateTimeFormat(pattern="yyyy-MM-dd")
	public Date startDate;
	
	@Temporal(TemporalType.DATE)
	@DateTimeFormat(pattern="yyyy-MM-dd")
	public Date endDate;


	// for compare two different months

    public Date fromStartDate;
    public Date fromEndDate;
    public Date toStartDate;
    public Date toEndDate;

	private long departmentId;
	private long designationId;

	private String departmentName;
	private String companyName;
	private String locationName;
	private String employeeCode;
	private String foodType;
	private String employeeType;

	private String reportType;
	private int year;
	private int month;

	private List<Long> employeeIds;

}
