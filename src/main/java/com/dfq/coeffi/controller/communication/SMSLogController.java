package com.dfq.coeffi.controller.communication;

import com.dfq.coeffi.controller.BaseController;
import com.dfq.coeffi.entity.communication.SMSLog;
import com.dfq.coeffi.service.communication.SMSLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

@RestController
public class SMSLogController extends BaseController {
	@Autowired
    SMSLogService logService;
	
	@GetMapping("smslog")
    public ResponseEntity<List<SMSLog>> getAllSMSLogs() {
        List<SMSLog> persistenceObject = logService.getAllSMSLogs();
		return new ResponseEntity<List<SMSLog>>(persistenceObject, HttpStatus.OK);
    }

	@GetMapping("smslog/{id}")
	public ResponseEntity<SMSLog> getMailConfiguration(@PathVariable Long id) {
		SMSLog persistenceObject = logService.getSMSLogById(id);
		return new ResponseEntity<SMSLog>(persistenceObject, HttpStatus.OK);
	}

	@PostMapping("smslog")
	public ResponseEntity<SMSLog> createMailConfiguration(@RequestBody SMSLog smsLog) throws URISyntaxException {

		smsLog = logService.saveSMSLog(smsLog);
		if(smsLog==null)
		{
			System.out.println("Unable to save");
			return null;
		}
		else {
			return ResponseEntity.created(new URI("/" + smsLog.getId()))
					.body(smsLog);
		}
	}

	@DeleteMapping("smslog/{id}")
	public ResponseEntity<SMSLog> deleteMailConfiguration(@PathVariable Long id) {
		SMSLog persistenceObject = logService.getSMSLogById(id);
		if(persistenceObject==null)
		{
			throw new EntityNotFoundException(SMSLog.class.getName());
		}
		logService.deleteSMSLogById(id);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@DeleteMapping("smslog")
	public ResponseEntity<SMSLog> deleteAllMailConfiguration(@PathVariable Long id) {
		try {
			logService.deleteAllSMSLogs();
		}
		catch(Exception ex) {
			System.out.println(ex.getMessage());
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
