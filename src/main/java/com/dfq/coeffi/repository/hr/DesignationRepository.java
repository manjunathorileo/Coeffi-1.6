package com.dfq.coeffi.repository.hr;

import com.dfq.coeffi.entity.hr.Department;
import com.dfq.coeffi.entity.hr.Designation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DesignationRepository extends JpaRepository<Designation, Long> {

    List<Designation> findByDepartment(Department department);

    @Query("UPDATE Designation d SET d.status=false WHERE d.id=:id")
    @Modifying
    void deactivate(@Param("id") long id);

    List<Designation> findByStatus(boolean status);

    Designation findByName(String role);
}
