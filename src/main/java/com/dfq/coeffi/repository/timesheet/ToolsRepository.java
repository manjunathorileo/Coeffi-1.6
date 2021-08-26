package com.dfq.coeffi.repository.timesheet;

import com.dfq.coeffi.entity.timesheet.Tools;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import javax.transaction.Transactional;

@Transactional
@EnableJpaRepositories
public interface ToolsRepository extends JpaRepository<Tools, Long> {

}
