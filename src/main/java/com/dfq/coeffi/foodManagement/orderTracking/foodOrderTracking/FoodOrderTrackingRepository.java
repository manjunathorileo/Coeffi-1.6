package com.dfq.coeffi.foodManagement.orderTracking.foodOrderTracking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@EnableJpaRepositories
@Transactional
public interface FoodOrderTrackingRepository extends JpaRepository<FoodOrderTracking,Long> {

    @Query("SELECT fot FROM FoodOrderTracking fot WHERE fot.foodType = :foodType")
    List<FoodOrderTracking> findByFoodType(@Param("foodType") String foodType);

    @Query("SELECT fot FROM FoodOrderTracking fot WHERE fot.employeeType = :employeeType AND fot.employeeCode = :employeeCode")
    List<FoodOrderTracking> findByEmployeeTypeByEmplCode(@Param("employeeType") String employeeType, @Param("employeeCode") String employeeCode);

    List<FoodOrderTracking> findByEmployeeTypeAndEmployeeCodeAndMarkedOn(String employeeType, String employeeCode, Date markedOn);
}
