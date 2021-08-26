package com.dfq.coeffi.dto;

import com.dfq.coeffi.entity.holiday.HolidayType;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class HolidayTypeDto 
{
	private List<HolidayType> types;
	
}
