package com.dfq.coeffi.SOPDetails.Event.eventMaster;

import com.dfq.coeffi.entity.hr.employee.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.List;

@EnableJpaRepositories
public interface EventMasterRepository extends JpaRepository<EventMaster,Long> {

    List<EventMaster> findByAttachedTo(Employee employee);
}
