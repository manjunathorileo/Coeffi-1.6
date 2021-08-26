package com.dfq.coeffi.resource;

import com.dfq.coeffi.entity.communication.CommunicationStatus;
import com.dfq.coeffi.entity.communication.SMSLog;
import com.dfq.coeffi.service.communication.CircularService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class SMSLogResourceConverter {

	@Autowired
	private CircularService circularService;
	
	
	public List<SMSLog> toEntity(SMSLogResource smsLogResource)
	{
		ArrayList<SMSLog> smsLogs = new ArrayList<SMSLog>();
		try {
			for(Long studentId : smsLogResource.getStudentId())
			{
				SMSLog smsLog = new SMSLog();
				smsLog.setCommunicationStatus(CommunicationStatus.FAILED);
				smsLog.setCircular(circularService.getCircularById(smsLogResource.getCircularId()));
				smsLog.setMessage(smsLogResource.getMessage());
				smsLogs.add(smsLog);
			}
		}
		catch(Exception ex) {
			System.out.println(ex.getMessage());
			return null;
		}
		return smsLogs;
	}

	
}
