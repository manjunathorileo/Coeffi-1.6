package com.dfq.coeffi.SOPDetails.Repositories;

import com.dfq.coeffi.SOPDetails.adherence.Adherence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;


@EnableJpaAuditing
@Transactional
public interface AdherenceRepository extends JpaRepository<Adherence,Long>
{

    List<Adherence> findByUserId(long userId);

    List<Adherence> findByDigitalSopIdAndUserId(long digitalSopId, long userId);

    @Modifying
    @Query("SELECT a FROM Adherence a WHERE a.digitalSopId =:sopId AND a.userId=:userId AND a.date BETWEEN :startDate AND :endDate")
    List<Adherence> filterAdherence(@Param("sopId") long sopId, @Param("startDate") Date startDate, @Param("endDate") Date endDate, @Param("userId") long userId);


}
