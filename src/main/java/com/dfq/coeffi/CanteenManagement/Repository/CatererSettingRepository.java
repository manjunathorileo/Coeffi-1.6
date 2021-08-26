package com.dfq.coeffi.CanteenManagement.Repository;

import com.dfq.coeffi.CanteenManagement.Entity.CatererSettingsAdv;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

@EnableJpaRepositories
@Transactional
public interface CatererSettingRepository extends JpaRepository<CatererSettingsAdv, Long> {
    CatererSettingsAdv findByFoodTypeAndEmployeeType(String foodType, String employeeType);

    CatererSettingsAdv findById(long id);

    @Query("SELECT cs FROM CatererSettingsAdv cs WHERE cs.counterDetailsAdv.id = :counterId AND cs.foodTimeMasterAdv.id = :foodTypeId" +
            " AND cs.employeeType = :employeeType AND cs.status = true ")
    CatererSettingsAdv findByFoodTypeByEmployeeTypeByCounter(@Param("foodTypeId") long foodTypeId, @Param("employeeType") String employeeType, @Param("counterId") long counterId);
}