package com.dfq.coeffi.service.holiday;

import com.dfq.coeffi.entity.holiday.Holiday;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/*
 * @author Azhar razvi
 * 
 */

public interface HolidayService
{
	Holiday createHoliday(Holiday holiday);
	
	List<Holiday> findAllHoliday();
	
	Optional<Holiday> getHolidya(long id);
	
	void deleteHoliday(long id);

	List<Holiday> getHolidayBetweenStartDateAndEndDate(Date startDate, Date endDate);

	Holiday getHolidayByStartDate(Date inputDate);
}
