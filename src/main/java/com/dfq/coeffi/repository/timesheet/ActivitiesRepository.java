package com.dfq.coeffi.repository.timesheet;

import com.dfq.coeffi.entity.timesheet.Activities;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.List;

@Transactional
@EnableJpaRepositories
public interface ActivitiesRepository extends JpaRepository<Activities, Long>
{

}