package com.dfq.coeffi.CanteenManagement.Repository;

import com.dfq.coeffi.CanteenManagement.Entity.BuildingDetails;
import com.dfq.coeffi.CanteenManagement.Entity.CounterDetailsAdv;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@EnableJpaRepositories
@Transactional
public interface CounterDetailsRepository extends JpaRepository<CounterDetailsAdv, Long> {
    CounterDetailsAdv findById(long id);

    List<CounterDetailsAdv> findByBuildingDetails(BuildingDetails buildingDetails);
}
