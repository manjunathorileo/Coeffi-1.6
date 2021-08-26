package com.dfq.coeffi.superadmin.Repository;

import com.dfq.coeffi.superadmin.Entity.CompanyLogo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import javax.transaction.Transactional;

@Transactional
@EnableJpaRepositories
public interface CompanyLogoRepository extends JpaRepository<CompanyLogo,Long> {
}
