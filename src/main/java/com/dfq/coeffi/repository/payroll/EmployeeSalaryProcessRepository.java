package com.dfq.coeffi.repository.payroll;

import javax.transaction.Transactional;

import com.dfq.coeffi.entity.payroll.EmployeeSalaryProcess;
import com.dfq.coeffi.entity.payroll.SalaryApprovalStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Transactional
@EnableJpaRepositories
public interface EmployeeSalaryProcessRepository extends JpaRepository<EmployeeSalaryProcess, Long> {

    @Query("SELECT es FROM EmployeeSalaryProcess es where" +
            " ((es.refId in (:employeeId)) and " +
            " (es.salaryMonth in (:inputMonth)) and " +
            "(es.salaryYear in (:inputYear))) AND es.salaryApprovalStatus ='FINANCE_APPROVED' ")
    Optional<EmployeeSalaryProcess> getEmployeeSalaryProcessByMonth(@Param("employeeId") Long employeeId,
                                                                    @Param("inputMonth") String inputMonth,
                                                                    @Param("inputYear") String inputYear);

    List<EmployeeSalaryProcess> findBySalaryApprovalStatus(SalaryApprovalStatus salaryApprovalStatus);

    @Query("SELECT es FROM EmployeeSalaryProcess es where" +
            " ((es.refId in (:employeeId)) and " +
            " (es.salaryMonth in (:inputMonth)) and " +
            "(es.salaryYear in (:inputYear))) AND (es.salaryApprovalStatus ='FINANCE_APPROVED' OR es.salaryApprovalStatus ='HR_APPROVED') ")
    Optional<EmployeeSalaryProcess> getEmployeeSalaryCreatedByMonth(@Param("employeeId") Long employeeId,
                                                                    @Param("inputMonth") String inputMonth,
                                                                    @Param("inputYear") String inputYear);

    List<EmployeeSalaryProcess> findBySalaryMonthAndSalaryYearAndSalaryApprovalStatus(String inputMonth, String inputYear, SalaryApprovalStatus salaryApprovalStatus);

}