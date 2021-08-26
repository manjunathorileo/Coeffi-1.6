package com.dfq.coeffi.visitor.Repositories;

import com.dfq.coeffi.visitor.Entities.VisitorPass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import javax.transaction.Transactional;

@EnableJpaRepositories
@Transactional
public interface VisitorPassRepository extends JpaRepository<VisitorPass,Long> {
    VisitorPass findByMobileNumber(String mobileNumber);
}
