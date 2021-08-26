package com.dfq.coeffi.StoreManagement.Repository;

import com.dfq.coeffi.StoreManagement.Entity.EmployeeRequest;
import com.dfq.coeffi.StoreManagement.Entity.ProductionItemQualityCheck;
import com.dfq.coeffi.entity.payroll.EmployeeAttendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;
@Transactional
@EnableJpaRepositories
public interface ProductionItemQualityCheckRepository extends JpaRepository<ProductionItemQualityCheck,Long> {

    @Query("SELECT p FROM ProductionItemQualityCheck p where p.createdOn =:startDate")
    List<ProductionItemQualityCheck> getByProductionQualityAndMarkedOn(@Param("startDate") Date startDate);

    List<ProductionItemQualityCheck> findByCreatedOn(Date date);

    @Query("SELECT e FROM ProductionItemQualityCheck e where e.createdOn between :startDate and :endDate")
    List<ProductionItemQualityCheck> getProductionBetweenDate(@Param("startDate") Date startDate, @Param("endDate") Date endDate);
}
