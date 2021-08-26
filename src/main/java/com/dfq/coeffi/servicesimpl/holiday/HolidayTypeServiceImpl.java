package com.dfq.coeffi.servicesimpl.holiday;

import java.util.List;
import java.util.Optional;
import com.dfq.coeffi.entity.holiday.HolidayType;
import com.dfq.coeffi.repository.holiday.HolidayTypeRepository;
import com.dfq.coeffi.service.holiday.HolidayTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import static java.util.Optional.ofNullable;

/*
 * @author Azhar razvi
 * 
 */
@Service
public class HolidayTypeServiceImpl implements HolidayTypeService
{

	private final HolidayTypeRepository holidayTypeRepository;

	@Autowired
	public HolidayTypeServiceImpl(HolidayTypeRepository holidayTypeRepository)
	{
		this.holidayTypeRepository=holidayTypeRepository;
	}

	@Override
	public HolidayType createHolidayType(HolidayType holidayType)
	{
		return holidayTypeRepository.save(holidayType);
	}

	@Override
	public List<HolidayType> listAllHolidayType()
	{
		return holidayTypeRepository.findAll();
	}

	@Override
	public Optional<HolidayType> getHolidayType(long id)
	{
		return ofNullable(holidayTypeRepository.getOne(id));
	}

	@Override
	public void deleteHolidayType(long id) {
		holidayTypeRepository.delete(id);
	}

}
