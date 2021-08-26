package com.dfq.coeffi.CanteenManagement.Service;

import com.dfq.coeffi.CanteenManagement.Entity.BuildingDetails;
import com.dfq.coeffi.CanteenManagement.Repository.BuildingDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BuildingDetailsServiceImpl implements BuildingDetailsService {

    @Autowired
    private BuildingDetailsRepository buildingDetailsRepository;


    @Override
    public BuildingDetails createBuildingDetails(BuildingDetails buildingDetails) {
        buildingDetails.setStatus(true);
        return buildingDetailsRepository.save(buildingDetails);
    }

    @Override
    public List<BuildingDetails> getAllBuildingDetails() {
        List<BuildingDetails> buildingDetails = new ArrayList<>();
        List<BuildingDetails> buildingDetailsList = buildingDetailsRepository.findAll();
        for (BuildingDetails buildingDetailsObj:buildingDetailsList) {
            if (buildingDetailsObj.getStatus().equals(true)){
                buildingDetails.add(buildingDetailsObj);
            }
        }
        return buildingDetails;
    }

    @Override
    public BuildingDetails getBuildingDetails(long id) {
        return buildingDetailsRepository.findById(id);
    }

    @Override
    public BuildingDetails deleteBuildingDetails(long id) {
        BuildingDetails buildingDetails = buildingDetailsRepository.findById(id);
        buildingDetails.setStatus(false);
        BuildingDetails buildingDetailsObj = buildingDetailsRepository.save(buildingDetails);
        return buildingDetailsObj;
    }
}
