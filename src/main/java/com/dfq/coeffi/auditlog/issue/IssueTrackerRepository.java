package com.dfq.coeffi.auditlog.issue;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

@Transactional
@EnableJpaRepositories
public interface IssueTrackerRepository extends JpaRepository<IssueTracker, Long> {

	@Query("SELECT it FROM IssueTracker it where it.trackedOn=:date")
	List<IssueTracker> getIssueTrackerListByDate(@Param("date") Date date);

}
