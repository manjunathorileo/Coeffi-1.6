package com.dfq.coeffi.CanteenManagement.Service;

import com.dfq.coeffi.CanteenManagement.Entity.BuildingDetails;
import com.dfq.coeffi.CanteenManagement.Entity.CounterDetailsAdv;

import java.util.List;

public interface CounterDetailsService {

    CounterDetailsAdv createCounterDetails(CounterDetailsAdv CounterDetailsAdv);
    List<CounterDetailsAdv> getAllCounterDetails();
    CounterDetailsAdv getCounterDetails(long id);
    CounterDetailsAdv deleteCounterDetails(long id);
    List<CounterDetailsAdv> getCounterDetailsAdvByBuilding(BuildingDetails buildingDetails);
}
