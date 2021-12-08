package com.dfq.coeffi.foodManagement.orderTracking.foodEstimationTimings;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.Transactional;

@EnableJpaRepositories
@Transactional
public interface FoodEstimationTimingsRepository extends JpaRepository<FoodEstimationTimings,Long> {

}
