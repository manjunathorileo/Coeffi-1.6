package com.dfq.coeffi.StoreManagement.Repository;

import com.dfq.coeffi.StoreManagement.Entity.Materials;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MaterialsRequestRepository extends JpaRepository<Materials,Long> {
    List<Materials> findByEmployeeId(long employeeId);

}
