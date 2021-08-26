package com.dfq.coeffi.controller.leave;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import com.dfq.coeffi.controller.BaseController;
import com.dfq.coeffi.entity.leave.LeaveBucket;
import com.dfq.coeffi.entity.master.AcademicYear;
import com.dfq.coeffi.service.AcademicYearService;
import com.dfq.coeffi.service.leave.LeaveBucketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;
import lombok.extern.slf4j.Slf4j;
import java.util.List;
import java.util.Optional;

@RestController
@Slf4j
public class LeaveBucketController extends BaseController {

	private final LeaveBucketService leaveBucketService;

	@Autowired
	private AcademicYearService academicYearService;

	LeaveBucketController(LeaveBucketService leaveBucketService) {
		this.leaveBucketService = leaveBucketService;
	}

	@GetMapping("leave-bucket")
	public ResponseEntity<List<LeaveBucket>> getAllLeaveBuckets() {
		List<LeaveBucket> leaveBuckets = leaveBucketService.getLeaveBuckets();
		if (CollectionUtils.isEmpty(leaveBuckets)) {
			throw new EntityNotFoundException("leaveBuckets");
		}
		return new ResponseEntity<>(leaveBuckets, HttpStatus.OK);
	}

	@PostMapping("leave-bucket")
	public ResponseEntity<LeaveBucket> applyLeaveBucket(@Valid @RequestBody LeaveBucket leaveBucket, UriComponentsBuilder ucBuilder) {

		if (leaveBucket.getAcademicYear() != null) {
			Optional<AcademicYear> academicYear = academicYearService.getAcademicYear(leaveBucket.getAcademicYear().getId());
			leaveBucket.setAcademicYear(academicYear.get());
		}
		LeaveBucket persistedLeaveBucket = leaveBucketService.applyLeaveBucket(leaveBucket);
		return new ResponseEntity<>(persistedLeaveBucket, HttpStatus.CREATED);
	}

	@GetMapping("leave-bucket/{id}")
	private ResponseEntity<LeaveBucket> getLeaveBucket(@PathVariable long id) {
		Optional<LeaveBucket> leaveBucket = leaveBucketService.getLeaveBucket(id);
		if (!leaveBucket.isPresent()) {
			throw new EntityNotFoundException(LeaveBucket.class.getSimpleName());
		}
		return new ResponseEntity<>(leaveBucket.get(), HttpStatus.OK);
	}

	@DeleteMapping("leave-bucket/{id}")
	public ResponseEntity<LeaveBucket> deactivateLeaveBucket(@PathVariable long id) {
		if (!leaveBucketService.isLeaveBucketExists(id)) {
			log.warn("Unable to deactivate leave bucket with ID : {} not found", id);
			throw new EntityNotFoundException(LeaveBucket.class.getSimpleName());
		}
		leaveBucketService.deactivateLeaveBucket(id);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	@PutMapping("leave-bucket/{id}")
	public ResponseEntity<LeaveBucket> updateLeaveBucket(@PathVariable long id, @Valid @RequestBody LeaveBucket leaveBucket) {
		Optional<LeaveBucket> persistedLeaveBucket = leaveBucketService.getLeaveBucket(id);
		if (!persistedLeaveBucket.isPresent()) {
			log.warn("Leave Bucket with ID {} not found", id);
			throw new EntityNotFoundException(LeaveBucket.class.getSimpleName());
		}
		leaveBucket.setId(id);
		leaveBucketService.applyLeaveBucket(leaveBucket);
		return new ResponseEntity<>(leaveBucket, HttpStatus.OK);
	}
}