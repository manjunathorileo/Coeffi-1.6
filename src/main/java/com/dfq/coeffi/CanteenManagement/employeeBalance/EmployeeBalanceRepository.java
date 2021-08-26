package com.dfq.coeffi.CanteenManagement.employeeBalance;

import com.dfq.coeffi.entity.hr.employee.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.Transactional;

@EnableJpaRepositories
@Transactional
public interface EmployeeBalanceRepository extends JpaRepository<EmployeeBalance,Long> {
    EmployeeBalance findByEmployee(Employee employee);

}
