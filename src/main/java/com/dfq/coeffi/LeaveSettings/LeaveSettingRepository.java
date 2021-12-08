package com.dfq.coeffi.LeaveSettings;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import javax.transaction.Transactional;

@EnableJpaRepositories
@Transactional
public interface LeaveSettingRepository extends JpaRepository<LeaveSetting,Long> {

}
