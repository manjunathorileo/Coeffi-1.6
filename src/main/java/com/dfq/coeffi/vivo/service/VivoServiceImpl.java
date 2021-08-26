package com.dfq.coeffi.vivo.service;

import com.dfq.coeffi.vivo.entity.VivoInfo;
import com.dfq.coeffi.vivo.repository.VivoInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class VivoServiceImpl implements VivoInfoService {
    @Autowired
    VivoInfoRepository vivoInfoRepository;

    @Override
    public VivoInfo save(VivoInfo vivoInfo) {
        return vivoInfoRepository.save(vivoInfo);
    }

    @Override
    public List<VivoInfo> getAll() {
        return vivoInfoRepository.findAll();
    }

    @Override
    public VivoInfo get(long id) {
        return vivoInfoRepository.findOne(id);
    }

    @Override
    public List<VivoInfo> getByVehicleNumber(String vehicleNumber) {
        return vivoInfoRepository.findByVehicleNumber(vehicleNumber);
    }

    @Override
    public VivoInfo getByAllocatedInfoByBayAndSlot(String bayNumber, String slotNumber) {
        return vivoInfoRepository.findByBayNumberAndSlotNumber(bayNumber,slotNumber);
    }

    @Override
    public List<VivoInfo> filterByDates(Date startDate, Date endDate) {
        return  vivoInfoRepository.getViviInfoFilterByDates(startDate,endDate);
    }

    @Override
    public VivoInfo getByVehicleNumberAndLoggedOn(Date loggedOn, String vehicleNumber) {
        return vivoInfoRepository.getByVehicleNumberAndMarkedOn(loggedOn,vehicleNumber);
    }

    @Override
    public List<VivoInfo> getByMarkedOn(Date loggedOn) {
        return vivoInfoRepository.findByLoggedOn(loggedOn);
    }

    @Override
    public List<VivoInfo> getByAllocatedInfoByBay(String bayNumber,String typeOfVehicle,Date date) {
        return vivoInfoRepository.findByBayNumberAndTypeOfVehicle(bayNumber,typeOfVehicle,date);
    }

    @Override
    public List<VivoInfo> getUpdatesAscendingVehicle() {
        return vivoInfoRepository.findByAscending();
    }


}
