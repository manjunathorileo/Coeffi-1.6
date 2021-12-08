package com.dfq.coeffi.foodManagement;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@EnableJpaRepositories
@Transactional
public interface FoodTrackerRepository extends JpaRepository<FoodTracker, Long> {
    List<FoodTracker> findByMarkedOnAndEmployeeTypeAndFoodType(Date markedOn, String employeeType, String foodType);

    List<FoodTracker> findByMarkedOn(Date markedOn);

    List<FoodTracker> findByMarkedOnAndEmployeeCode(Date markedOn, String employeeCode);

    List<FoodTracker> findByMarkedOnAndEmployeeType(Date markedOn,String employeeType);

    @Query("SELECT ft FROM FoodTracker ft WHERE ft.employeeType = :employeeType AND ft.employeeCode = :employeeCode")
    List<FoodTracker> findByEmployeeTypeByEmployeeCode(@Param("employeeType") String employeeType, @Param("employeeCode") String employeeCode);



}
