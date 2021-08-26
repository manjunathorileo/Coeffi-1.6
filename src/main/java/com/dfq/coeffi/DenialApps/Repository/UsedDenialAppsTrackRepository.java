package com.dfq.coeffi.DenialApps.Repository;

import com.dfq.coeffi.DenialApps.Entities.UsedDenialAppsTrack;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

@EnableJpaAuditing
@Transactional
public interface UsedDenialAppsTrackRepository extends JpaRepository<UsedDenialAppsTrack, Long> {
    List<UsedDenialAppsTrack> findByUsedOn(Date usedOn);
}
