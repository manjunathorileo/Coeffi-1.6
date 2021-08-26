package com.dfq.coeffi.vivo.repository;

import com.dfq.coeffi.vivo.entity.Recording;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories
public interface RecordingRepository extends JpaRepository<Recording,Long> {
}
