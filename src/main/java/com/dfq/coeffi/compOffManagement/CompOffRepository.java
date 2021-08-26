package com.dfq.coeffi.compOffManagement;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import javax.transaction.Transactional;
import java.util.List;

@EnableJpaRepositories
@Transactional
public interface CompOffRepository extends JpaRepository<CompOff, Long> {

    List<CompOff> findByEmployeeId(long empId);

    List<CompOff> findByFirstMgrId(long mgrId);
}
