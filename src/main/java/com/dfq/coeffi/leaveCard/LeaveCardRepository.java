package com.dfq.coeffi.leaveCard;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import javax.transaction.Transactional;
import java.util.List;

@EnableJpaRepositories
@Transactional
public interface LeaveCardRepository extends JpaRepository<LeaveCard, Long> {

    List<LeaveCard> findByMonthAndYear(String month, String year);
}
