package com.dfq.coeffi.preventiveMaintenance.durationType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import javax.transaction.Transactional;
import java.util.Optional;

@EnableJpaRepositories
@Transactional
public interface DurationTypeRepository extends JpaRepository<DurationType, Long> {

    Optional<DurationType> findById(long id);
}
