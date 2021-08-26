package com.dfq.coeffi.repository.timesheet;

import com.dfq.coeffi.entity.hr.employee.Employee;
import com.dfq.coeffi.entity.timesheet.Projects;
import com.dfq.coeffi.entity.timesheet.Timesheet;
import com.dfq.coeffi.entity.timesheet.WeeklySchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

@Transactional
@EnableJpaRepositories
public interface WeeklyScheduleRepository extends JpaRepository<WeeklySchedule, Long> {

    @Query("SELECT w FROM WeeklySchedule w where w.projectId= :projectId and w.employeeId = :employeeId and w.date between :startDate and :endDate ")
    List<WeeklySchedule> getWeeklySchedule(@Param("projectId") Long projectId, @Param("employeeId") Long employeeId, @Param("startDate") Date startDate, @Param("endDate") Date endDate);
}
