package com.dfq.coeffi.service.communication;

import com.dfq.coeffi.entity.communication.EmailLog;
import com.dfq.coeffi.entity.communication.SMSLog;

import java.util.List;

public interface CommunicationService {
	Boolean sendEmail(List<EmailLog> emailLogs);
	Boolean sendSMS(List<SMSLog> smsLogs);
}
