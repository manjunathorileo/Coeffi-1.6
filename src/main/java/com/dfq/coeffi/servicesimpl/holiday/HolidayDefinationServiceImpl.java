package com.dfq.coeffi.servicesimpl.holiday;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import com.dfq.coeffi.entity.holiday.HolidayDefination;
import com.dfq.coeffi.repository.holiday.HolidayDefinationRepository;
import com.dfq.coeffi.service.holiday.HolidayDefinationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import static java.util.Optional.ofNullable;

/*
 * @author Azhar razvi
 * 
 */

@Service
public class HolidayDefinationServiceImpl  implements HolidayDefinationService
{
	private final HolidayDefinationRepository holidayDefinationRepository;
	
	@Autowired
	public HolidayDefinationServiceImpl(HolidayDefinationRepository holidayDefinationRepository)
	{
		this.holidayDefinationRepository=holidayDefinationRepository;
	}
	
	@Override
	public HolidayDefination createHolidayDefination(HolidayDefination holidayDefination)
	{
		return holidayDefinationRepository.save(holidayDefination);
	}

	@Override
	public List<HolidayDefination> getHolidayDefination()
	{
		return holidayDefinationRepository.findAll();
	}

	@Override
	public Optional<HolidayDefination> getHolidayDefination(long id)
	{
		return ofNullable(holidayDefinationRepository.getOne(id));
	}

	@Override
	public void deleteHolidayDefination(long id) {
		holidayDefinationRepository.delete(id);
	}

	@Override
	public List<HolidayDefination> getFixedHolidayDefinationBetweenStartDateAndEndDate(Date startDate, Date endDate) {
		return holidayDefinationRepository.getHolidayDefinationList(startDate,endDate);
	}
}