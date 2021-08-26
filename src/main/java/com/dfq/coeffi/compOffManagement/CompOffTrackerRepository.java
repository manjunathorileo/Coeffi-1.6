package com.dfq.coeffi.compOffManagement;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import javax.transaction.Transactional;
import java.util.List;

@EnableJpaRepositories
@Transactional
public interface CompOffTrackerRepository extends JpaRepository<CompOffTracker,Long> {

    List<CompOffTracker> findByEmployeeId(long employeeId);

    CompOffTracker findByEmployeeIdAndMonthAndYear(long employeeId,String month, String year);
}
