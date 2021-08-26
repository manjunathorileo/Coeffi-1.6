package com.dfq.coeffi.vivo.repository;

import com.dfq.coeffi.vivo.entity.VivoInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

@EnableJpaRepositories
public interface VivoInfoRepository extends JpaRepository<VivoInfo, Long> {

    List<VivoInfo> findByVehicleNumber(String vehicleNumber);

    @Query("SELECT vivoInfo FROM VivoInfo vivoInfo WHERE vivoInfo.loggedOn BETWEEN :startDate AND :endDate ")
    List<VivoInfo> getViviInfoFilterByDates(@Param("startDate") Date StartDate, @Param("endDate") Date endDate);

    @Query("SELECT vivo FROM VivoInfo vivo where vivo.vehicleNumber = :vehicleNumber and vivo.loggedOn =:startDate")
    VivoInfo getByVehicleNumberAndMarkedOn(@Param("startDate") Date startDate, @Param("vehicleNumber") String vehicleNumber);

    VivoInfo findByBayNumberAndSlotNumber(String bayNumber, String slotNumber);

    @Query("SELECT vivo FROM VivoInfo vivo where vivo.bayNumber = :bayNumber and vivo.vehicleType.typeOfVehicle =:typeOfVehicle and vivo.loggedOn =:date")
    List<VivoInfo> findByBayNumberAndTypeOfVehicle(@Param("bayNumber") String bayNumber, @Param("typeOfVehicle") String typeOfVehicle, @Param("date") Date date);

    List<VivoInfo> findByLoggedOn(Date loggedOn);

    @Query("SELECT vivo FROM VivoInfo vivo ORDER BY vivo.markedOn ASC ")
    List<VivoInfo> findByAscending();
}
