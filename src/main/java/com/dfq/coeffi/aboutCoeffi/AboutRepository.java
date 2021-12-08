package com.dfq.coeffi.aboutCoeffi;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import javax.transaction.Transactional;

@EnableJpaRepositories
@Transactional
public interface AboutRepository extends JpaRepository<About,Long> {

}
