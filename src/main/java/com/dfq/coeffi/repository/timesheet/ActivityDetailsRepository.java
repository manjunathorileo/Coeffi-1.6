package com.dfq.coeffi.repository.timesheet;

import com.dfq.coeffi.entity.timesheet.ActivityDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import javax.transaction.Transactional;

@Transactional
@EnableJpaRepositories
public interface ActivityDetailsRepository extends JpaRepository<ActivityDetails, Long> {
}
