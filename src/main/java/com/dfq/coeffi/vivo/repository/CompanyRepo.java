package com.dfq.coeffi.vivo.repository;

import com.dfq.coeffi.vivo.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories
public interface CompanyRepo extends JpaRepository<Company,Long>{
}
