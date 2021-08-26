package com.dfq.coeffi.CanteenManagement.Service;

import com.dfq.coeffi.CanteenManagement.Entity.BuildingDetails;

import java.util.List;

public interface BuildingDetailsService {

    BuildingDetails createBuildingDetails(BuildingDetails buildingDetails);
    List<BuildingDetails> getAllBuildingDetails();
    BuildingDetails getBuildingDetails(long id);
    BuildingDetails deleteBuildingDetails(long id);
}
