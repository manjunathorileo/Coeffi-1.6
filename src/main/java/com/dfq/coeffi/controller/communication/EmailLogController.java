package com.dfq.coeffi.controller.communication;

import com.dfq.coeffi.controller.BaseController;
import com.dfq.coeffi.entity.communication.EmailLog;
import com.dfq.coeffi.service.communication.EmailLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

@RestController
public class EmailLogController {
	@Autowired
	EmailLogService emailLogService;

	// List all Homeworks
	@GetMapping("emaillog")
	public ResponseEntity<List<EmailLog>> getAllEmailLogs() {
		List<EmailLog> persistenceObject = emailLogService.getAllEmailLogs();
		return new ResponseEntity<List<EmailLog>>(persistenceObject, HttpStatus.OK);
	}

	@GetMapping("emaillog/{id}")
	public ResponseEntity<EmailLog> getMailConfiguration(@PathVariable Long id) {
		EmailLog persistenceObject = emailLogService.getEmailLogById(id);
		return new ResponseEntity<EmailLog>(persistenceObject, HttpStatus.OK);
	}

	@PostMapping("emaillog")
	public ResponseEntity<EmailLog> createMailConfiguration(@RequestBody EmailLog emailLog) throws URISyntaxException {

		emailLog = emailLogService.saveEmailLog(emailLog);
		if(emailLog==null)
		{
			System.out.println("Unable to save");
			return null;
		}
		else {
			return ResponseEntity.created(new URI("/" + emailLog.getId()))
					.body(emailLog);
		}

	}

	@DeleteMapping("emaillog/{id}")
	public ResponseEntity<EmailLog> deleteMailConfiguration(@PathVariable Long id) {
		EmailLog persistenceObject = emailLogService.getEmailLogById(id);
		if(persistenceObject==null)
		{
			throw new EntityNotFoundException(EmailLog.class.getName());
		}
		emailLogService.deleteEmailLogById(id);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@DeleteMapping("emaillog")
	public ResponseEntity<EmailLog> deleteAllMailConfiguration(@PathVariable Long id) {
		try {
			emailLogService.deleteAllEmailLogs();
		}
		catch(Exception ex) {
			System.out.println(ex.getMessage());
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return new ResponseEntity<>(HttpStatus.OK);
	}
}
