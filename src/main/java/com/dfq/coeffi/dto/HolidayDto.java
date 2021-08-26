package com.dfq.coeffi.dto;

import com.dfq.coeffi.entity.holiday.Holiday;
import com.dfq.coeffi.entity.holiday.HolidayType;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import java.util.Date;
import java.util.List;

@Setter
@Getter
public class HolidayDto
{
	private List<Holiday> holidayList;

	@Column
	private Date startDate;

	@Column
	private Date endDate;

	private String holidayType;

	private String holidayName;

}
