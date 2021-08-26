package com.dfq.coeffi.advanceFoodManagementExtra;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@EnableJpaRepositories
@Transactional
public interface EmployeeCanteenDetailsRepository extends JpaRepository<EmployeeCanteenDetails, Long> {

    EmployeeCanteenDetails findByEmpIdAndFoodTypeNameAndMarkedOn(long employeeId, String foodType, Date date);

    List<EmployeeCanteenDetails> findByCounterId(long counterId);

    @Query("SELECT employeeCanteenDetails FROM EmployeeCanteenDetails employeeCanteenDetails " +
            "WHERE employeeCanteenDetails.counterId = :counterId " +
            "AND employeeCanteenDetails.foodTypeId = :foodTypeId")
    List<EmployeeCanteenDetails> findByCounterIdAndFoodTypeId(@Param("counterId") long counterId, @Param("foodTypeId") long foodTypeId);

    EmployeeCanteenDetails findByMarkedOnAndEmployeeCodeAndFoodTypeName(Date markedOn, String employeeCode, String foodTypeName);

    List<EmployeeCanteenDetails> findByMarkedOnAndEmployeeCode(Date markedOn, String employeeCode);
}
