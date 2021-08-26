package com.dfq.coeffi.evacuationApi;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@EnableJpaRepositories
@Transactional
public interface InsideCsvRawRepository extends JpaRepository<InsideCsvRaw,Long> {
    InsideCsvRaw findBySsoId(String ssoId);


}
