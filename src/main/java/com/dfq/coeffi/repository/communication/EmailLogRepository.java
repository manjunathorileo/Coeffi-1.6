package com.dfq.coeffi.repository.communication;

import com.dfq.coeffi.entity.communication.EmailLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface EmailLogRepository extends JpaRepository<EmailLog, Long> {
	@Query("select e from EmailLog e where " +
			 "e.communicationStatus = FAILED")
	public List<EmailLog> findFailedMails();
}
