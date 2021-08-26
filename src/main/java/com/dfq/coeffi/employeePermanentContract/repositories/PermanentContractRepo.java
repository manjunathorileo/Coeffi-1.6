package com.dfq.coeffi.employeePermanentContract.repositories;

import com.dfq.coeffi.employeePermanentContract.entities.EmpPermanentContract;
import com.dfq.coeffi.entity.hr.employee.EmployeeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.List;

@EnableJpaRepositories
@Transactional
public interface PermanentContractRepo extends JpaRepository<EmpPermanentContract, Long> {

    EmpPermanentContract findByEmployeeCode(String employeeCode);

    List<EmpPermanentContract> findByStatus(boolean status);

    List<EmpPermanentContract> findByEmployeeType(EmployeeType employeeType);

    List<EmpPermanentContract> findByContractCompany(String companyName);

    @Query("select e from EmpPermanentContract e where e.department.id=:departmentId ORDER BY e.employeeCode ASC")
    List<EmpPermanentContract> findByDepartment(@Param("departmentId") long departmentId);
}
