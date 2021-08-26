package com.dfq.coeffi.CanteenManagement.Repository;

import com.dfq.coeffi.CanteenManagement.Entity.EmployeeRecharge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@EnableJpaRepositories
@Transactional
public interface EmployeeRechargeRepository extends JpaRepository<EmployeeRecharge,Long> {

    @Query("SELECT e FROM EmployeeRecharge e where e.rechargeDate >= :startDate AND e.rechargeDate <= :endDate")
    List<EmployeeRecharge> getEmployeeRechargeBetweenDate(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

    EmployeeRecharge findById(long id);

    List<EmployeeRecharge> findByEmpId(long empId);
}
