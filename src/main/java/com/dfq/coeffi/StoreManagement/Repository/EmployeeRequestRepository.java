package com.dfq.coeffi.StoreManagement.Repository;

import com.dfq.coeffi.StoreManagement.Entity.EmployeeRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface EmployeeRequestRepository extends JpaRepository<EmployeeRequest,Long> {
    EmployeeRequest findByEmployeeId(long employeeId);

    @Query("SELECT emp FROM EmployeeRequest emp where emp.markedOn =:startDate")
    List<EmployeeRequest> getByEmployeeRequestAndMarkedOn(@Param("startDate") Date startDate);



}
