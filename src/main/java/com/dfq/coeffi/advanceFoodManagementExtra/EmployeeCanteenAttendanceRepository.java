package com.dfq.coeffi.advanceFoodManagementExtra;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@EnableJpaRepositories
@Transactional
public interface EmployeeCanteenAttendanceRepository extends JpaRepository<EmployeeCanteenAttendance,Long> {

    EmployeeCanteenAttendance findByDeviceLogId(String deviceLogId);

    List<EmployeeCanteenAttendance> findByMarkedOn(Date date);


}
