package com.dfq.coeffi.repository.hr;

import com.dfq.coeffi.entity.hr.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.*;

public interface DepartmentRepository extends JpaRepository<Department, Long> {

    @Query("UPDATE Department d SET d.status=false WHERE d.id=:id")
    @Modifying
    void deactivate(@Param("id") long id);

    @Query("SELECT d FROM Department d WHERE d.status=true")
    List<Department> activeDepartments();

    Optional<Department> findDepartmentByName(String name);
    Department findByName(String name);
}
