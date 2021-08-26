package com.dfq.coeffi.archiveData;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.Transactional;

@EnableJpaRepositories
@Transactional
public interface AttendanceRawDataRepository extends JpaRepository<AttendanceRawData, Long> {

}
