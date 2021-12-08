package com.dfq.coeffi.leaveEncashMent;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import javax.transaction.Transactional;

@EnableJpaRepositories
@Transactional
public interface LeaveEncashRepository extends JpaRepository<LeaveEncash,Long> {

}
