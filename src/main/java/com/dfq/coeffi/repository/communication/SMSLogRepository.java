package com.dfq.coeffi.repository.communication;

import com.dfq.coeffi.entity.communication.SMSLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SMSLogRepository extends JpaRepository<SMSLog, Long> {

	@Query("select s from SMSLog s where " +
			 "s.communicationStatus = FAILED")
	public List<SMSLog> findFailedSMSs();
}
