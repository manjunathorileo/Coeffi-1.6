package com.dfq.coeffi.employeePermanentContract.repositories;


import com.dfq.coeffi.employeePermanentContract.entities.SitePc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import javax.transaction.Transactional;

@EnableJpaRepositories
@Transactional
public interface SitePcRepository extends JpaRepository<SitePc,Long> {

}
