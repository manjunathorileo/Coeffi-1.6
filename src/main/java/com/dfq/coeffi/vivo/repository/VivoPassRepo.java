package com.dfq.coeffi.vivo.repository;

import com.dfq.coeffi.vivo.entity.VivoPass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;

@EnableJpaRepositories
@Transactional
public interface VivoPassRepo extends JpaRepository<VivoPass,Long> {

    VivoPass findByVehicleNumber(String vehicleNumer);

    @Query("SELECT vivoPass FROM VivoPass vivoPass where vivoPass.CardId = :cardId")
    VivoPass findByCardId(@Param("cardId") long cardId);
}
