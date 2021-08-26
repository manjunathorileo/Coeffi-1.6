package com.dfq.coeffi.dto;

import com.dfq.coeffi.entity.holiday.HolidayDefination;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class HolidayDefinationDto 
{
	private List<HolidayDefination> names;
}
