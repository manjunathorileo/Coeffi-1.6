package com.dfq.coeffi.foodManagement.orderTracking.foodOrderPunchLog;

import com.dfq.coeffi.foodManagement.orderTracking.foodOrderTracking.FoodOrderTracking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@EnableJpaRepositories
@Transactional
public interface FoodOrderPunchLogRepository extends JpaRepository<FoodOrderPunchLog,Long> {

    List<FoodOrderPunchLog> findByAkg(Boolean status);
}
