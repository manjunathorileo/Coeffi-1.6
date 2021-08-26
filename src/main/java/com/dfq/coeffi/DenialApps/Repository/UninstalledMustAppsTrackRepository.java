package com.dfq.coeffi.DenialApps.Repository;

import com.dfq.coeffi.DenialApps.Entities.UninstalledMustAppsTrack;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

@EnableJpaAuditing
@Transactional
public interface UninstalledMustAppsTrackRepository extends JpaRepository<UninstalledMustAppsTrack, Long> {
    List<UninstalledMustAppsTrack> findByUninstalledOn(Date uninstalledOn);
}
