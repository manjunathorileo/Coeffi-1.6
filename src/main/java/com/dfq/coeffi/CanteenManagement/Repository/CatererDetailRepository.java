package com.dfq.coeffi.CanteenManagement.Repository;

import com.dfq.coeffi.CanteenManagement.Entity.CatererDetailsAdv;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.Transactional;

@EnableJpaRepositories
@Transactional
public interface CatererDetailRepository extends JpaRepository<CatererDetailsAdv,Long> {
}
