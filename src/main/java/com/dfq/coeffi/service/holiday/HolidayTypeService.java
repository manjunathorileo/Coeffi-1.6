package com.dfq.coeffi.service.holiday;

import com.dfq.coeffi.entity.holiday.HolidayType;
import java.util.List;
import java.util.Optional;

/*
 * @author Azhar razvi
 * 
 */

public interface HolidayTypeService
{
	HolidayType createHolidayType(HolidayType holidayType);
	
	List<HolidayType> listAllHolidayType();
	
	Optional<HolidayType> getHolidayType(long id);

	void deleteHolidayType(long id);

}
