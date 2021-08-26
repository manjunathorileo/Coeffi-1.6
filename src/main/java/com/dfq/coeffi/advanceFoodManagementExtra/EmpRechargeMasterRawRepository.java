package com.dfq.coeffi.advanceFoodManagementExtra;

import com.dfq.coeffi.advanceFoodManagementExtra.EmpRechargeMasterRaw;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.Transactional;

@EnableJpaRepositories
@Transactional
public interface EmpRechargeMasterRawRepository extends JpaRepository<EmpRechargeMasterRaw,Long> {

}
