package com.dfq.coeffi.auditlog.log;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

@Transactional
@EnableJpaRepositories
public interface ApplicationLogRepository extends JpaRepository<ApplicationLog, Long> {

	@Query("SELECT al FROM ApplicationLog al where al.loggedOn=:date")
	List<ApplicationLog> getIApplicationLogsByDate(@Param("date") Date date);


    @Query("SELECT al FROM ApplicationLog al where al.loggedUserId=:loggedUserId order by al.id desc")
    List<ApplicationLog> loggedUserLogs(@Param("loggedUserId") long loggedUserId);
}