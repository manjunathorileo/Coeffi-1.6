package com.dfq.coeffi.Gate;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import javax.transaction.Transactional;
import java.util.List;

@EnableJpaRepositories
@Transactional
public interface GateAccessRepository extends JpaRepository<GateAccessMssql,Long> {

    List<GateAccessMssql> findByEmpIdAndControllerCode(String employeeCode, String gateNumber);
}
