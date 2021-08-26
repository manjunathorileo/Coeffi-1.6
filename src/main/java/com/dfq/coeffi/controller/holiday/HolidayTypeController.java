package com.dfq.coeffi.controller.holiday;

import java.util.List;
import java.util.Optional;
import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;

import com.dfq.coeffi.controller.BaseController;
import com.dfq.coeffi.dto.HolidayTypeDto;
import com.dfq.coeffi.entity.holiday.HolidayType;
import com.dfq.coeffi.service.holiday.HolidayTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

/*
 * @author Azhar razvi
 */

@RestController
public class HolidayTypeController extends BaseController {
	private final HolidayTypeService holidayTypeService;

	@Autowired
	public HolidayTypeController(HolidayTypeService holidayTypeService) {
		this.holidayTypeService = holidayTypeService;
	}

	@GetMapping("holiday-type")
	public ResponseEntity<List<HolidayType>> listAll() {
		List<HolidayType> holidayType = holidayTypeService.listAllHolidayType();
		if (CollectionUtils.isEmpty(holidayType)) {
			throw new EntityNotFoundException("HolidayType");
		}
		return new ResponseEntity<>(holidayType, HttpStatus.OK);
	}

	@PostMapping("holiday-type")
	public ResponseEntity<HolidayTypeDto> saveTypes(@Valid @RequestBody HolidayTypeDto holidayTypeDto) {
		for (HolidayType types : holidayTypeDto.getTypes()) {
			holidayTypeService.createHolidayType(types);
		}
		return new ResponseEntity<>(HttpStatus.CREATED);
	}

	@PutMapping("holiday-type/{id}")
	public ResponseEntity<HolidayType> update(@PathVariable long id, @Valid @RequestBody HolidayType holidayType) {
		Optional<HolidayType> persistedHolidayType = holidayTypeService.getHolidayType(id);
		if (!persistedHolidayType.isPresent()) {
			throw new EntityNotFoundException(HolidayType.class.getSimpleName());
		}
		holidayType.getClass();
		holidayTypeService.createHolidayType(holidayType);
		return new ResponseEntity<>(holidayType, HttpStatus.OK);
	}

	@DeleteMapping("holiday-type/{id}")
	public ResponseEntity<HolidayType> deleteHolidayType(@PathVariable Long id) {
		Optional<HolidayType> holidayType = holidayTypeService.getHolidayType(id);
		if (!holidayType.isPresent()) {
			throw new EntityNotFoundException(HolidayType.class.getName());
		}
		holidayTypeService.deleteHolidayType(id);
		return new ResponseEntity<>(HttpStatus.OK);
	}
}