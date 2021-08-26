package com.dfq.coeffi.vivo.repository;

import com.dfq.coeffi.vivo.entity.TimeConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories
public interface TimeConfigRepo extends JpaRepository<TimeConfig,Long> {

}
