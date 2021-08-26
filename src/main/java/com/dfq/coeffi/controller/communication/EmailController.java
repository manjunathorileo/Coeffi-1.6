package com.dfq.coeffi.controller.communication;

import com.dfq.coeffi.controller.BaseController;
import com.dfq.coeffi.dto.EventDto;
import com.dfq.coeffi.util.notification.messages.email.EmailConfig;
import com.dfq.coeffi.util.notification.messages.email.MailService;
import com.dfq.coeffi.util.notification.messages.email.Mail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class EmailController extends BaseController {

	@Autowired
	private final MailService mailService;
	
	@Autowired
	public EmailController(MailService mailService)
	{
		this.mailService = mailService;
	}
	

	/*@PostMapping("email-send")
	public ResponseEntity<Mail> sendMail(@RequestBody EventDto eventdto)
	{
		 Map<String,Object> model = new HashMap<String,Object> ();
         model.put("name",eventdto.message);
         EmailConfig emailConfignew = new EmailConfig();
         
         Mail mailnew = emailConfignew.setMailCredentials("patilniranjan1994@gmail.com", eventdto.title, model);
         mailService.sendEmail(mailnew, "SampleMail.txt");
		return new ResponseEntity<>(HttpStatus.CREATED);
	}*/
}
