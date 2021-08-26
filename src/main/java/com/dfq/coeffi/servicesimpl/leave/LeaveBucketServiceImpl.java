package com.dfq.coeffi.servicesimpl.leave;

import com.dfq.coeffi.entity.leave.LeaveBucket;
import com.dfq.coeffi.repository.leave.LeaveBucketRepository;
import com.dfq.coeffi.service.leave.LeaveBucketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class LeaveBucketServiceImpl implements LeaveBucketService {
	
	@Autowired
    private LeaveBucketRepository leaveBucketRepository ;

	@Override
	public LeaveBucket applyLeaveBucket(LeaveBucket leaveBucket) {
		return leaveBucketRepository.save(leaveBucket);
	}

	@Override
	public List<LeaveBucket> getLeaveBuckets() {
		return leaveBucketRepository.findAll();
	}

	@Override
	public List<LeaveBucket> getLeaveBucketByRefNameAndRefId() {
		return null;
	}

	@Override
	public List<LeaveBucket> getLeaveBucketByYear(long year) {
		return null;
	}

	@Override
	public Optional<LeaveBucket> getLeaveBucket(long id) {
        return Optional.ofNullable(leaveBucketRepository.findOne(id));
	}

	@Override
	public boolean isLeaveBucketExists(long id) {
        return leaveBucketRepository.exists(id);
	}

	@Override
	public void deactivateLeaveBucket(long id) {
		
	}
}