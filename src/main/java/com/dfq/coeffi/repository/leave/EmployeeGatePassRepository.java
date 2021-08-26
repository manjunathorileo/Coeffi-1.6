package com.dfq.coeffi.repository.leave;

import com.dfq.coeffi.entity.leave.EmployeeGatePass;
import com.dfq.coeffi.entity.leave.GatePassStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

@EnableJpaRepositories
public interface EmployeeGatePassRepository extends JpaRepository<EmployeeGatePass,Long> {
    @Query("SELECT e FROM EmployeeGatePass e WHERE e.gatePassStatus =:gatePassStatus AND date(e.gatePassRequestOn) =:today")
    List<EmployeeGatePass> getApprovedEmployeeGatePass(@Param("gatePassStatus") GatePassStatus gatePassStatus, @Param("today") Date today);

    @Query("SELECT e FROM EmployeeGatePass e WHERE e.gatePassStatus =:gatePassStatus")
    List<EmployeeGatePass> getApprovedEmployeeGatePass(@Param("gatePassStatus") GatePassStatus gatePassStatus);

    List<EmployeeGatePass> findByEmployeeId(long employeeId);

    List<EmployeeGatePass> findByGatePassStatusAndEmployeeId(GatePassStatus gatePassStatus, long employeeId);

    List<EmployeeGatePass> findByGatePassStatusAndFirstApprover(GatePassStatus gatePassStatus, long employeeId);

    List<EmployeeGatePass> findByGatePassStatusAndSecondApprover(GatePassStatus gatePassStatus, long employeeId);

    @Query("SELECT e FROM EmployeeGatePass e WHERE e.firstApprover =:firstApprover OR e.secondApprover =:secondApprover")
    List<EmployeeGatePass> getAllGatePassByApprover(@Param("firstApprover") long firstApprover, @Param("secondApprover") long secondApprover);
}
