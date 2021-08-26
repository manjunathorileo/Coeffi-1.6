package com.dfq.coeffi.servicesimpl.communication;

import com.dfq.coeffi.entity.communication.CommunicationConfiguration;
import com.dfq.coeffi.repository.communication.MailConfigurationRepository;
import com.dfq.coeffi.service.communication.MailConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MailConfigurationServiceImpl implements MailConfigurationService {

	@Autowired
	private MailConfigurationRepository mailConfigurationRepository;
	
	@Override
	public CommunicationConfiguration save(CommunicationConfiguration configuration) {
		// TODO Auto-generated method stub
		return mailConfigurationRepository.save(configuration);
	}

	@Override
	public void deleteMailConfigurationById(Long id) {
		// TODO Auto-generated method stub
		mailConfigurationRepository.delete(id);
	}

	@Override
	public void deleteAllMailConfigurations() {
		// TODO Auto-generated method stub
		mailConfigurationRepository.deleteAll();
		
	}

	@Override
	public CommunicationConfiguration getConfigurationById(Long id) {
		// TODO Auto-generated method stub
		return mailConfigurationRepository.findOne(id);
	}

    @Override
    public Optional<CommunicationConfiguration> getActiveConfigurationById(boolean status) {
        return mailConfigurationRepository.findByStatus(status);
    }

}
