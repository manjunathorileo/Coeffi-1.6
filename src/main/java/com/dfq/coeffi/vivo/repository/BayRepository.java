package com.dfq.coeffi.vivo.repository;

import com.dfq.coeffi.vivo.entity.Bay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import javax.transaction.Transactional;
import java.util.List;

@EnableJpaRepositories
@Transactional
public interface BayRepository extends JpaRepository<Bay, Long> {

    List<Bay> findByBayNumber(String bayNumber);

    Bay findByBayNumberAndBayVehicleType(String bayNumber, String bayVehicleType);

    List<Bay> findByBayVehicleType(String typeOfVehicle);
}
