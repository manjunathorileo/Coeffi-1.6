package com.dfq.coeffi.DenialApps.Repository;

import com.dfq.coeffi.DenialApps.Entities.HealthStatusTrack;
import com.dfq.coeffi.DenialApps.Entities.MustApps;
import com.dfq.coeffi.DenialApps.Entities.UninstalledMustAppsTrack;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

@EnableJpaAuditing
@Transactional
public interface HealthStatusTrackRepository extends JpaRepository<HealthStatusTrack, Long> {
    List<HealthStatusTrack> findBySubmittedOn(Date sunmittedOn);
}
