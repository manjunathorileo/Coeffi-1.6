package com.dfq.coeffi.service.holiday;

import com.dfq.coeffi.entity.holiday.HolidayDefination;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/*
 * @author Azhar razvi
 * 
 */

public interface HolidayDefinationService
{
	HolidayDefination createHolidayDefination(HolidayDefination holidayDefination);
	
	List<HolidayDefination> getHolidayDefination();
	
	Optional<HolidayDefination> getHolidayDefination(long id);

	void deleteHolidayDefination(long id);

	List<HolidayDefination> getFixedHolidayDefinationBetweenStartDateAndEndDate(Date startDate, Date endDate);

}
