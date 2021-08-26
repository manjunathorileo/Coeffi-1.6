package com.dfq.coeffi.vivo.service;

import com.dfq.coeffi.vivo.entity.VivoInfo;

import java.util.Date;
import java.util.List;

public interface VivoInfoService{
    VivoInfo save(VivoInfo vivoInfo);

    List<VivoInfo> getAll();

    VivoInfo get(long id);

    List<VivoInfo> getByVehicleNumber(String vehicleNumber);

    VivoInfo getByAllocatedInfoByBayAndSlot(String bayNumber,String slotNumber);

    List<VivoInfo> filterByDates(Date startDate, Date endDate);

    VivoInfo getByVehicleNumberAndLoggedOn(Date loggedOn,String vehicleNumber);

    List<VivoInfo> getByMarkedOn(Date loggedOn);

    List<VivoInfo> getByAllocatedInfoByBay(String bayNumber,String typeOfVehicle,Date date);

    List<VivoInfo> getUpdatesAscendingVehicle();
}
