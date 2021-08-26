package com.dfq.coeffi.repository.timesheet;

import com.dfq.coeffi.entity.hr.employee.Employee;
import com.dfq.coeffi.entity.leave.Leave;
import com.dfq.coeffi.entity.leave.LeaveStatus;
import com.dfq.coeffi.entity.master.AcademicYear;
import com.dfq.coeffi.entity.timesheet.Activities;
import com.dfq.coeffi.entity.timesheet.Projects;
import com.dfq.coeffi.entity.timesheet.Timesheet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

@Transactional
@EnableJpaRepositories
public interface TimesheetRepository extends JpaRepository<Timesheet, Long> {

    @Query("select t from Timesheet t where t.projects= :project and t.employee= :employee")
    Timesheet getTimesheet(@Param("project") Projects project, @Param("employee") Employee employee);

    @Query("SELECT t FROM Timesheet t where t.projects= :project and t.employee = :employee and t.createdDate between :startDate and :endDate ")
    List<Timesheet> getTimesheetByProject(@Param("project") Projects project, @Param("employee") Employee employee, @Param("startDate") Date startDate, @Param("endDate") Date endDate);
}
