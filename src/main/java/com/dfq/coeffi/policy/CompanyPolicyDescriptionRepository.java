package com.dfq.coeffi.policy;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;
import javax.transaction.Transactional;

@Transactional
@EnableJpaRepositories
public interface CompanyPolicyDescriptionRepository extends JpaRepository<CompanyPolicyDescription,Long> {

    @Query("UPDATE CompanyPolicyDescription cd SET cd.active=false WHERE cd.id=:id")
    @Modifying
    void deleteCompanyPolicyDescription(@Param("id") long id);
}
