package com.dfq.coeffi.policy;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;
import javax.transaction.Transactional;
import java.util.Optional;

@Transactional
@EnableJpaRepositories
public interface CompanyPolicyRepository extends JpaRepository<CompanyPolicy,Long> {

    @Query("UPDATE CompanyPolicy c SET c.active=false WHERE c.id=:id")
    @Modifying
    void deleteCompanyPolicy(@Param("id") long id);

    Optional<CompanyPolicy> findById(long id);
}
