package com.dfq.coeffi.Gate.Repository;

import com.dfq.coeffi.Gate.Entity.Gate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import javax.transaction.Transactional;

@EnableJpaAuditing
@Transactional
public interface GateRepository extends JpaRepository<Gate,Long> {
    Gate findByGateNumber(String num);
}
