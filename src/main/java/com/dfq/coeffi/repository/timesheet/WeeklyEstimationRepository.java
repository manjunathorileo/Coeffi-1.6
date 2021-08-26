package com.dfq.coeffi.repository.timesheet;

import com.dfq.coeffi.entity.timesheet.WeeklyEstimation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;
import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

@Transactional
@EnableJpaRepositories
public interface WeeklyEstimationRepository extends JpaRepository<WeeklyEstimation, Long> {

//    @Query("SELECT w FROM WeeklyEstimation w where w.projectId= :projectId and w.employeeId = :employeeId and w.weeklySchedules.date between :startDate and :endDate")
//    List<WeeklyEstimation> getWeeklyEstimation(@Param("projectId") Long projectId, @Param("employeeId") Long employeeId, @Param("startDate") Date startDate, @Param("endDate") Date endDate);

//    @Query("SELECT w FROM WeeklyEstimation w where w.projectId= :projectId and w.employeeId = :employeeId and w.weeklySchedules.date between :startDate and :endDate")
//    List<WeeklyEstimation> getWeeklyEstimation(@Param("projectId") Long projectId, @Param("employeeId") Long employeeId, @Param("startDate") Date startDate, @Param("endDate") Date endDate);
}

//    String query = "select r from ReportQS r join r.projects p where p.code = :code";
//    List<ReportQS> reports = em.createQuery(query,ReportQS.class).setParameter("code","grnl").getResultList();
