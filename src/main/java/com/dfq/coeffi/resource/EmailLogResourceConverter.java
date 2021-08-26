package com.dfq.coeffi.resource;

import com.dfq.coeffi.entity.communication.CommunicationStatus;
import com.dfq.coeffi.entity.communication.EmailLog;
import com.dfq.coeffi.service.communication.CircularService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class EmailLogResourceConverter {

	@Autowired
	private CircularService circularService;
	
	public List<EmailLog> toEntity(EmailLogResource emailLogResource)
	{
		ArrayList<EmailLog> emailLogs = new ArrayList<EmailLog>();
		try {
			for(Long studentId : emailLogResource.getStudentId())
			{
				EmailLog emailLog = new EmailLog();
				emailLog.setCommunicationStatus(CommunicationStatus.FAILED);
				emailLog.setCircular(circularService.getCircularById(emailLogResource.getCircularId()));
				emailLog.setMessage(emailLogResource.getMessage());
				emailLog.setSubject(emailLogResource.getSubject());
				emailLog.setDate(new Date());
				emailLogs.add(emailLog);
			}
		}
		catch(Exception ex) {
			System.out.println(ex.getMessage());
			return null;
		}
		return emailLogs;
	}

	
}
