package com.dfq.coeffi.servicesimpl.holiday;

import java.util.*;

import com.dfq.coeffi.entity.holiday.Holiday;
import com.dfq.coeffi.repository.holiday.HolidayRepository;
import com.dfq.coeffi.service.holiday.HolidayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import static java.util.Optional.ofNullable;

/*
 * @author Azhar razvi
 * 
 */

@Service
public class HolidayServiceImpl implements HolidayService
{
	private final HolidayRepository holidayRepository;
	
	@Autowired
	public HolidayServiceImpl(HolidayRepository holidayRepository)
	{
		this.holidayRepository=holidayRepository;
	}

	@Override
	public Holiday createHoliday(Holiday holiday)
	{
		return holidayRepository.save(holiday);
	}

	@Override
	public List<Holiday> findAllHoliday()
	{
		return holidayRepository.findAll();
	}

	@Override
	public Optional<Holiday> getHolidya(long id)
	{
		return ofNullable(holidayRepository.getOne(id));
	}

	@Override
	public void deleteHoliday(long id)
	{
		holidayRepository.delete(id);
	}

	@Override
	public List<Holiday> getHolidayBetweenStartDateAndEndDate(Date startDate, Date endDate) {
		Calendar stDate = Calendar.getInstance();
		Calendar enDate = Calendar.getInstance();
		stDate.setTime(startDate);
		stDate.add(Calendar.DATE, -1);
		Date fromDate = stDate.getTime();
		enDate.setTime(endDate);
		enDate.add(Calendar.DATE, 1);
		Date toDate = enDate.getTime();
		return holidayRepository.getHolidayList(fromDate,toDate);
	}

	@Override
	public Holiday getHolidayByStartDate(Date inputDate) {
		return holidayRepository.findByStartDate(inputDate);
	}

}
