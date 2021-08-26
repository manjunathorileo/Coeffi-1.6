package com.dfq.coeffi.employeePerformanceManagement.repository;

import com.dfq.coeffi.employeePerformanceManagement.entity.EmployeePerformanceManagement;
import com.dfq.coeffi.employeePerformanceManagement.entity.GoalStatusEnum;
import com.dfq.coeffi.entity.hr.employee.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface EmployeePerformanceManagementRepository extends JpaRepository<EmployeePerformanceManagement, Long> {
    Optional<EmployeePerformanceManagement> findById(long employeePerformanceManagementId);

    List<EmployeePerformanceManagement> findByEmployee(Employee employee);

    List<EmployeePerformanceManagement> findByGoalStatus(String goalStatus);


    @Query("select ep from EmployeePerformanceManagement ep where ep.goalStatus= :goalStatus and ep.status= :status and ep.firstManager = :firstManager")
    List<EmployeePerformanceManagement> findByGoalStatusAndStatus(@Param("goalStatus") GoalStatusEnum goalStatus, @Param("status") boolean status, @Param("firstManager") Employee firstManager);

    @Query("select ep from EmployeePerformanceManagement ep where ep.goalStatus= :goalStatus and ep.status= :status and ep.secondManager = :secondManager")
    List<EmployeePerformanceManagement> findByGoalStatusAndStatusAnd2ndMgr(@Param("goalStatus") GoalStatusEnum goalStatus, @Param("status") boolean status, @Param("secondManager") Employee secondManager);

    @Query("select ep from EmployeePerformanceManagement ep where ep.goalStatus= :goalStatus and ep.status= :status and ep.employee = :employee")
    List<EmployeePerformanceManagement> findByGoalStatusAndStatusAnd2Employee(@Param("goalStatus") GoalStatusEnum goalStatus, @Param("status") boolean status, @Param("employee") Employee employee);

}
