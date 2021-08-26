package com.dfq.coeffi.controller.communication;

import com.dfq.coeffi.controller.BaseController;
import com.dfq.coeffi.entity.communication.EmailLog;
import com.dfq.coeffi.entity.communication.SMSLog;
import com.dfq.coeffi.resource.EmailLogResource;
import com.dfq.coeffi.resource.EmailLogResourceConverter;
import com.dfq.coeffi.resource.SMSLogResource;
import com.dfq.coeffi.resource.SMSLogResourceConverter;
import com.dfq.coeffi.service.communication.CommunicationService;
import com.dfq.coeffi.service.communication.EmailLogService;
import com.dfq.coeffi.service.communication.SMSLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URISyntaxException;
import java.util.List;

@RestController
public class CommunicationController extends BaseController {
	
	@Autowired
    CommunicationService communicationService;
	
	@Autowired
    EmailLogResourceConverter emailLogResoucreConverter;
	
	@Autowired
    SMSLogResourceConverter smsLogResoucreConverter;
	
	@Autowired
    EmailLogService emailLogService;
	
	@Autowired
    SMSLogService smsLogService;
	
	
	@PostMapping("communication/sendmails")
	public ResponseEntity<EmailLog> sendMails(@RequestBody EmailLogResource emailLogResource) throws URISyntaxException {
	    List<EmailLog> emailLogs = emailLogResoucreConverter.toEntity(emailLogResource);
	    emailLogs = emailLogService.saveAllEmailLogs(emailLogs);
		if(emailLogs==null)
		{
			return null;
		}
		else {
			if(communicationService.sendEmail(emailLogs))
			{
				return new ResponseEntity<>(HttpStatus.OK);
			}
			else {
				return new ResponseEntity<>(HttpStatus.NO_CONTENT);
			}
		}
    }

	@PostMapping("communication/sendsms")
	public ResponseEntity<SMSLog> sendSMSs(@RequestBody SMSLogResource smsLogResource) throws URISyntaxException {
	    List<SMSLog> smsLogs = smsLogResoucreConverter.toEntity(smsLogResource);
	    smsLogs = smsLogService.saveAllSMSLogs(smsLogs);
		if(smsLogs==null)
		{
			System.out.println("Unable to save");
			return null;
		}
		else {
			if(communicationService.sendSMS(smsLogs))
			{
				return new ResponseEntity<>(HttpStatus.OK);
			}
			else
			{
				return new ResponseEntity<>(HttpStatus.NO_CONTENT);
			}
			
		}
        
    }
	
}
