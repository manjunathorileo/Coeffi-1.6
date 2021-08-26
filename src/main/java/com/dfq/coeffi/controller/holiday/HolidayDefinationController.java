package com.dfq.coeffi.controller.holiday;

import java.util.List;
import java.util.Optional;
import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;

import com.dfq.coeffi.controller.BaseController;
import com.dfq.coeffi.dto.DateDto;
import com.dfq.coeffi.dto.HolidayDefinationDto;
import com.dfq.coeffi.entity.holiday.HolidayDefination;
import com.dfq.coeffi.entity.holiday.HolidayType;
import com.dfq.coeffi.service.holiday.HolidayDefinationService;
import com.dfq.coeffi.service.holiday.HolidayTypeService;
import com.dfq.coeffi.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

@RestController
public class HolidayDefinationController extends BaseController {

	private final HolidayDefinationService holidayDefinationService;
	private final HolidayTypeService holidayTypeService;

	@Autowired
	public HolidayDefinationController(HolidayDefinationService holidayDefinationService,
			HolidayTypeService holidayTypeService) {
		this.holidayDefinationService = holidayDefinationService;
		this.holidayTypeService = holidayTypeService;
	}

	@GetMapping("holiday-defination")
	public ResponseEntity<List<HolidayDefination>> listAll() {
		List<HolidayDefination> holidayDefination = holidayDefinationService.getHolidayDefination();
		if (CollectionUtils.isEmpty(holidayDefination)) {
			throw new EntityNotFoundException("HolidayDefination");
		}
		return new ResponseEntity(holidayDefination, HttpStatus.OK);
	}

	@PostMapping("holiday-defination")
	public ResponseEntity<HolidayDefination> saveHolidayDefination(@Valid @RequestBody HolidayDefinationDto names) {

		if (names.getNames() != null) {
			for (HolidayDefination dto : names.getNames()) {

				if (dto.getHolidayType() != null) {
					Optional<HolidayType> holidayType = holidayTypeService.getHolidayType(dto.getHolidayType().getId());
					dto.setHolidayType(holidayType.get());
					holidayDefinationService.createHolidayDefination(dto);
				}
			}
		}
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PutMapping("holiday-defination/{id}")
	public ResponseEntity<HolidayDefination> update(@PathVariable long id,
			@Valid @RequestBody HolidayDefination holidayDefination) {
		Optional<HolidayDefination> persistedHolidayDefination = holidayDefinationService.getHolidayDefination(id);
		if (!persistedHolidayDefination.isPresent()) {
			throw new EntityNotFoundException(HolidayType.class.getSimpleName());
		}
		holidayDefination.getClass();
		holidayDefinationService.createHolidayDefination(holidayDefination);
		return new ResponseEntity<>(holidayDefination, HttpStatus.OK);
	}

	@DeleteMapping("holiday-defination/{id}")
	public ResponseEntity<HolidayDefination> deleteHolidayDefination(@PathVariable Long id) {
		Optional<HolidayDefination> holidayDefination = holidayDefinationService.getHolidayDefination(id);
		if (!holidayDefination.isPresent()) {
			throw new EntityNotFoundException(HolidayDefination.class.getName());
		}
		holidayDefinationService.deleteHolidayDefination(id);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PostMapping("holiday-defination/between-to-dates")
	public ResponseEntity<List<HolidayDefination>> getFixedHolidayDefinationBetweenStartDateAndEndDate(@RequestBody DateDto dateDto)
	{
		List<HolidayDefination>  holidayDefinations = holidayDefinationService.getFixedHolidayDefinationBetweenStartDateAndEndDate(DateUtil.convertDateToFormat(dateDto.getStartDate()),DateUtil.convertDateToFormat(dateDto.getEndDate()));
		if(CollectionUtils.isEmpty(holidayDefinations))
		{
			throw new EntityNotFoundException("Holiday");
		}
		return new ResponseEntity<>(holidayDefinations,HttpStatus.OK);
	}
}