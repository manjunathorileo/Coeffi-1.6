package com.dfq.coeffi.evacuationApi;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.Transactional;

@EnableJpaRepositories
@Transactional
public interface EvacuationTrackerRepository extends JpaRepository<EvacuationTracker, Long> {

}
