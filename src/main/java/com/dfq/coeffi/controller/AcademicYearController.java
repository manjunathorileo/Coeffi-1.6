package com.dfq.coeffi.controller;

import com.dfq.coeffi.controller.BaseController;
import com.dfq.coeffi.entity.master.AcademicYear;
import com.dfq.coeffi.service.AcademicYearService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
public class AcademicYearController extends BaseController {
	
	@Autowired
	private AcademicYearService academicYearService;
	
	@GetMapping("/academic-year")
	public ResponseEntity<List<AcademicYear>> getAcademicYears() {
		List<AcademicYear> academicYears = academicYearService.getAcademicYears();
		if (CollectionUtils.isEmpty(academicYears)) {
			throw new EntityNotFoundException("academicYears");
		}
		return new ResponseEntity<>(academicYears, HttpStatus.OK);
	}

	/**
	 * @param
	 *            : save object to database and return the saved objects
	 * @return
	 */

	@PostMapping("/academic-year")
	public ResponseEntity<AcademicYear> createNewAcademicYear(@Valid @RequestBody AcademicYear academicYear) {
		academicYear.setYear(academicYear.getRange().substring(0,4));
		AcademicYear persistedObject = academicYearService.createAcademicYear(academicYear);
		return new ResponseEntity<>(persistedObject, HttpStatus.CREATED);
	}

	/**
	 *
	 * @param id
	 * @return return single sport object by passing sport id
	 */
	@GetMapping("academic-year/{id}")
	private ResponseEntity<AcademicYear> getAcademicYear(@PathVariable long id) {
		Optional<AcademicYear> academicYear = academicYearService.getAcademicYear(id);
		if (!academicYear.isPresent()) {
			throw new EntityNotFoundException(AcademicYear.class.getSimpleName());
		}
		return new ResponseEntity<>(academicYear.get(), HttpStatus.OK);
	}

	/**
	 * @param id
	 * @return permanent delete of sport by provided id
	 */
	@DeleteMapping("academic-year")
	public ResponseEntity<AcademicYear> delete(@PathVariable long id) {
		if (!academicYearService.isAcademicYearExists(id)) {
			log.warn("Unable to delete AcademicYear. AcademicYear with ID : {} not found", id);
			throw new EntityNotFoundException(AcademicYear.class.getSimpleName());
		}
		academicYearService.deleteAcademicYear(id);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	/**
	 * @param id
	 * @param
	 * @return to update the sport object
	 */
	@PutMapping("academic-year")
	public ResponseEntity<AcademicYear> updateAcademicYear(@PathVariable long id, @Valid @RequestBody AcademicYear academicYear) {

		Optional<AcademicYear> persistedAcademicYear = academicYearService.getAcademicYear(id);
		if (!persistedAcademicYear.isPresent()) {
			log.warn("AcademicYear with ID {} not found", id);
			throw new EntityNotFoundException(AcademicYear.class.getSimpleName());
		}
		academicYear.setId(id);
		academicYearService.createAcademicYear(academicYear);
		return new ResponseEntity<>(academicYear, HttpStatus.OK);
	}
}