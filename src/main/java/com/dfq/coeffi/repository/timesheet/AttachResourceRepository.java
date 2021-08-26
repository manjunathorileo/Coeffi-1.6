package com.dfq.coeffi.repository.timesheet;

import com.dfq.coeffi.entity.hr.employee.Employee;
import com.dfq.coeffi.entity.leave.LeaveStatus;
import com.dfq.coeffi.entity.master.AcademicYear;
import com.dfq.coeffi.entity.timesheet.Activities;
import com.dfq.coeffi.entity.timesheet.AttachResource;
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
public interface AttachResourceRepository extends JpaRepository<AttachResource, Long> {

    @Query("select a from AttachResource a where a.projects= :projects and a.employee= :employee")
    AttachResource getAttachResourceByProjectAndEmployee(@Param("projects") Projects projects, @Param("employee") Employee employee);

    @Query("select a from AttachResource a where a.projects= :projects and a.employee= :employee")
    List<AttachResource> getAttachResourcesByProjectAndEmployee(@Param("projects") Projects projects, @Param("employee") Employee employee);
}
