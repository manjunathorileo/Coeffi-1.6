package com.dfq.coeffi.compOffManagement;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import javax.transaction.Transactional;

@EnableJpaRepositories
@Transactional
public interface DatesForCompOffRepo extends JpaRepository<DatesForCompOff,Long> {

}
