package com.dfq.coeffi.service.communication;

import com.dfq.coeffi.entity.communication.CommunicationConfiguration;

import java.util.Optional;

public interface MailConfigurationService {
	CommunicationConfiguration save(CommunicationConfiguration configuration);
	void deleteMailConfigurationById(Long id);
	void deleteAllMailConfigurations();
    CommunicationConfiguration getConfigurationById(Long id);
    Optional<CommunicationConfiguration> getActiveConfigurationById(boolean status);
}
