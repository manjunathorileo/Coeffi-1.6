package com.dfq.coeffi.service.communication;

import com.dfq.coeffi.entity.communication.EmailLog;

import java.util.List;

public interface EmailLogService {
	EmailLog saveEmailLog(EmailLog emailLog);
	List<EmailLog> saveAllEmailLogs(List<EmailLog> emailLogs);
	List<EmailLog> getAllEmailLogs();
	EmailLog getEmailLogById(Long id);
	void deleteEmailLogById(Long id);
	void deleteAllEmailLogs();
}
