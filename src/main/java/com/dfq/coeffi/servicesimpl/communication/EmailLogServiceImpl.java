package com.dfq.coeffi.servicesimpl.communication;

import com.dfq.coeffi.entity.communication.EmailLog;
import com.dfq.coeffi.repository.communication.EmailLogRepository;
import com.dfq.coeffi.service.communication.EmailLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmailLogServiceImpl implements EmailLogService {
	
	@Autowired
	private EmailLogRepository emailLogRepository;

	@Override
	public EmailLog saveEmailLog(EmailLog emailLog) {
		// TODO Auto-generated method stub
		return emailLogRepository.save(emailLog);
	}

	@Override
	public List<EmailLog> getAllEmailLogs() {
		// TODO Auto-generated method stub
		return emailLogRepository.findAll();
	}

	@Override
	public EmailLog getEmailLogById(Long id) {
		// TODO Auto-generated method stub
		return emailLogRepository.findOne(id);
	}

	@Override
	public void deleteEmailLogById(Long id) {
		// TODO Auto-generated method stub
		emailLogRepository.delete(id);
	}

	@Override
	public void deleteAllEmailLogs() {
		// TODO Auto-generated method stub
		emailLogRepository.deleteAll();
	}

	@Override
	public List<EmailLog> saveAllEmailLogs(List<EmailLog> emailLogs) {
		// TODO Auto-generated method stub
		return emailLogRepository.save(emailLogs);
	}

}
