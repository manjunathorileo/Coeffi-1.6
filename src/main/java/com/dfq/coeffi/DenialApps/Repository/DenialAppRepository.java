package com.dfq.coeffi.DenialApps.Repository;

import com.dfq.coeffi.DenialApps.Entities.DenialApps;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import javax.transaction.Transactional;

@EnableJpaAuditing
@Transactional
public interface DenialAppRepository extends JpaRepository<DenialApps,Long> {
}
