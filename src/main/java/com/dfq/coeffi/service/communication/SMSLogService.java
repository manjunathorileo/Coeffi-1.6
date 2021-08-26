package com.dfq.coeffi.service.communication;

import com.dfq.coeffi.entity.communication.SMSLog;

import java.util.List;

public interface SMSLogService {
	SMSLog saveSMSLog(SMSLog smsLog);
	List<SMSLog> saveAllSMSLogs(List<SMSLog> smsLog);
	List<SMSLog> getAllSMSLogs();
	SMSLog getSMSLogById(Long id);
	void deleteSMSLogById(Long id);
	void deleteAllSMSLogs();
}
