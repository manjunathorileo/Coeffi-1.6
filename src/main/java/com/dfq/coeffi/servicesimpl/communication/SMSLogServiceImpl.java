package com.dfq.coeffi.servicesimpl.communication;

import com.dfq.coeffi.entity.communication.SMSLog;
import com.dfq.coeffi.repository.communication.SMSLogRepository;
import com.dfq.coeffi.service.communication.SMSLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SMSLogServiceImpl implements SMSLogService {

	@Autowired
	SMSLogRepository smsLogRepository;
	
	@Override
	public SMSLog saveSMSLog(SMSLog smsLog) {
		// TODO Auto-generated method stub
		return smsLogRepository.save(smsLog);
	}

	@Override
	public List<SMSLog> getAllSMSLogs() {
		// TODO Auto-generated method stub
		return smsLogRepository.findAll();
	}

	@Override
	public SMSLog getSMSLogById(Long id) {
		// TODO Auto-generated method stub
		return smsLogRepository.findOne(id);
	}

	@Override
	public void deleteSMSLogById(Long id) {
		// TODO Auto-generated method stub
		smsLogRepository.delete(id);
	}

	@Override
	public void deleteAllSMSLogs() {
		// TODO Auto-generated method stub
		smsLogRepository.deleteAll();
	}

	@Override
	public List<SMSLog> saveAllSMSLogs(List<SMSLog> smsLog) {
		// TODO Auto-generated method stub
		return smsLogRepository.save(smsLog);
	}

}
