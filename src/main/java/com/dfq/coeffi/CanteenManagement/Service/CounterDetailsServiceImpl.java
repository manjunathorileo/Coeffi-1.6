package com.dfq.coeffi.CanteenManagement.Service;

import com.dfq.coeffi.CanteenManagement.Entity.BuildingDetails;
import com.dfq.coeffi.CanteenManagement.Entity.CounterDetailsAdv;
import com.dfq.coeffi.CanteenManagement.Repository.CounterDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CounterDetailsServiceImpl implements CounterDetailsService {

    @Autowired
    CounterDetailsRepository counterDetailsRepository;

    @Override
    public CounterDetailsAdv createCounterDetails(CounterDetailsAdv CounterDetailsAdv) {
        CounterDetailsAdv.setStatus(true);
        return counterDetailsRepository.save(CounterDetailsAdv);
    }

    @Override
    public List<CounterDetailsAdv> getAllCounterDetails() {
        List<CounterDetailsAdv> counterDetailAdvs = new ArrayList<>();
        List<CounterDetailsAdv> counterDetailsAdvList = counterDetailsRepository.findAll();
        for (CounterDetailsAdv counterDetailsAdvObj : counterDetailsAdvList) {
            if (counterDetailsAdvObj.getStatus().equals(true)) {
                if (counterDetailsAdvObj.getBuildingDetails().getStatus().equals(true)) {
                    counterDetailAdvs.add(counterDetailsAdvObj);
                } else {
                    counterDetailsAdvObj.setStatus(false);
                    counterDetailsRepository.save(counterDetailsAdvObj);
                }
            }
        }
        return counterDetailAdvs;
    }

    @Override
    public CounterDetailsAdv getCounterDetails(long id) {
        return counterDetailsRepository.findById(id);
    }

    @Override
    public CounterDetailsAdv deleteCounterDetails(long id) {
        CounterDetailsAdv counterDetailsAdv = counterDetailsRepository.findById(id);
        counterDetailsAdv.setStatus(false);
        return counterDetailsRepository.save(counterDetailsAdv);
    }

    @Override
    public List<CounterDetailsAdv> getCounterDetailsAdvByBuilding(BuildingDetails buildingDetails) {
        List<CounterDetailsAdv> counterDetailsAdvs = new ArrayList<>();
        List<CounterDetailsAdv> counterDetailsAdvsObj = counterDetailsRepository.findByBuildingDetails(buildingDetails);
        for (CounterDetailsAdv counterDetailsAdv:counterDetailsAdvsObj) {
            if (counterDetailsAdv.getStatus().equals(true)){
                counterDetailsAdvs.add(counterDetailsAdv);
            }
        }
        return counterDetailsAdvs;
    }
}
