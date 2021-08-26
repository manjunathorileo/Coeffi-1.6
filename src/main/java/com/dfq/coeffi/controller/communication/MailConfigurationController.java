package com.dfq.coeffi.controller.communication;

import com.dfq.coeffi.controller.BaseController;
import com.dfq.coeffi.entity.communication.CommunicationConfiguration;
import com.dfq.coeffi.service.communication.MailConfigurationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

@Slf4j
@RestController
public class MailConfigurationController extends BaseController {
	@Autowired
    MailConfigurationService configurationService;
	

	@GetMapping("mail-configuration/{id}")
	public ResponseEntity<CommunicationConfiguration> getMailConfiguration(@PathVariable Long id) {
        CommunicationConfiguration persistenceObject = configurationService.getConfigurationById(id);
		return new ResponseEntity<CommunicationConfiguration>(persistenceObject, HttpStatus.OK);
    }

    /**
     * @return the active configuration
     */
    @GetMapping("mail-configuration/active")
    public ResponseEntity<CommunicationConfiguration> getActiveMailConfiguration() {
        Optional<CommunicationConfiguration> persistedObject = configurationService.getActiveConfigurationById(true);
        if(!persistedObject.isPresent()){
            log.error("Mail configuration not found");
            throw new EntityNotFoundException("MailConfiguration");
        }
        return new ResponseEntity<CommunicationConfiguration>(persistedObject.get(), HttpStatus.OK);
    }


	@PostMapping("mail-configuration")
    public ResponseEntity<CommunicationConfiguration> createMailConfiguration(@RequestBody CommunicationConfiguration configuration) throws URISyntaxException {

        configuration.setStatus(true);
        configuration.setStatus(true);
		configuration = configurationService.save(configuration);
		if(configuration==null)
		{
			System.out.println("Unable to save");
			return null;
		}
		else {
			return ResponseEntity.created(new URI("/" + configuration.getId()))
		            .body(configuration);
		}
        
    }
	
	@DeleteMapping("mail-configuration/{id}")
    public ResponseEntity<CommunicationConfiguration> deleteMailConfiguration(@PathVariable Long id) {
		CommunicationConfiguration persistenceObject = configurationService.getConfigurationById(id);
		if(persistenceObject==null)
		{
			throw new EntityNotFoundException(CommunicationConfiguration.class.getName());
		}
		configurationService.deleteMailConfigurationById(id);
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@DeleteMapping("mail-configuration")
    public ResponseEntity<CommunicationConfiguration> deleteAllMailConfiguration(@PathVariable Long id) {
		try {
			configurationService.deleteAllMailConfigurations();
		}
		catch(Exception ex) {
			System.out.println(ex.getMessage());
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
}
