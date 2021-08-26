package com.dfq.coeffi.E_Learning.repository;

import com.dfq.coeffi.E_Learning.modules.UserTest;
import com.dfq.coeffi.entity.hr.employee.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import javax.transaction.Transactional;
import java.util.List;

@Transactional
@EnableJpaRepositories
public interface UserTestRepository extends JpaRepository<UserTest, Long> {
    List<UserTest> findByStatus(boolean status);

    void deleteById(long id);

    List<UserTest> findByEmployee(Employee employee);
}
