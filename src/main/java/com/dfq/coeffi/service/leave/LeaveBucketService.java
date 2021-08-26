package com.dfq.coeffi.service.leave;

import com.dfq.coeffi.entity.leave.LeaveBucket;

import java.util.List;
import java.util.Optional;

public interface LeaveBucketService {
	
	LeaveBucket applyLeaveBucket(LeaveBucket leaveBucket);
    List<LeaveBucket> getLeaveBuckets();
    List<LeaveBucket> getLeaveBucketByRefNameAndRefId();
    List<LeaveBucket> getLeaveBucketByYear(long year);
    Optional<LeaveBucket> getLeaveBucket(long id);
    boolean isLeaveBucketExists(long id);
    void deactivateLeaveBucket(long id);

}
