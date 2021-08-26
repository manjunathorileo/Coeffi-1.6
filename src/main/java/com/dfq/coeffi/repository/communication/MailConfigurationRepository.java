package com.dfq.coeffi.repository.communication;

import com.dfq.coeffi.entity.communication.CommunicationConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MailConfigurationRepository extends JpaRepository<CommunicationConfiguration, Long> {

    Optional<CommunicationConfiguration> findByStatus(boolean status);
}
