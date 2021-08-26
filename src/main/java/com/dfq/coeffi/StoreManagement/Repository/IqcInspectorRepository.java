package com.dfq.coeffi.StoreManagement.Repository;

import com.dfq.coeffi.StoreManagement.Entity.EmployeeRequest;
import com.dfq.coeffi.StoreManagement.Entity.IqcInspector;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface IqcInspectorRepository extends JpaRepository<IqcInspector,Long> {

    @Query("SELECT emp FROM IqcInspector emp where emp.markedOn =:startDate")
    List<IqcInspector> getByIqcAndMarkedOn(@Param("startDate") Date startDate);
}
