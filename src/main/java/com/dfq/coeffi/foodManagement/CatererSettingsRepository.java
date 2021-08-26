package com.dfq.coeffi.foodManagement;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.Transactional;

@EnableJpaRepositories
@Transactional
public interface CatererSettingsRepository extends JpaRepository<CatererSettings, Long> {

    CatererSettings findByFoodTypeAndEmployeeType(String foodType, String employeeType);


}
