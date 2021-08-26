package com.dfq.coeffi.CanteenManagement.Repository;

import com.dfq.coeffi.CanteenManagement.Entity.BuildingDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.Transactional;

@EnableJpaRepositories
@Transactional
public interface BuildingDetailsRepository extends JpaRepository<BuildingDetails, Long> {
    BuildingDetails findById(long id);
}
