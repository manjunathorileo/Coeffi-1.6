package com.dfq.coeffi.visitor.Repositories;

import com.dfq.coeffi.visitor.Entities.VisitorCompany;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import javax.transaction.Transactional;

@EnableJpaRepositories
@Transactional
public interface VisitorCompanyRepository extends JpaRepository<VisitorCompany,Long> {

}
