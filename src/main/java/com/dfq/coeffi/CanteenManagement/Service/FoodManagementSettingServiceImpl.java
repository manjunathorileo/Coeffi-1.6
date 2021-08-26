package com.dfq.coeffi.CanteenManagement.Service;

import com.dfq.coeffi.CanteenManagement.Entity.FoodManagementSetting;
import com.dfq.coeffi.CanteenManagement.Repository.FoodManagementSettingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FoodManagementSettingServiceImpl implements FoodManagementSettingService {

    @Autowired
    private FoodManagementSettingRepository foodManagementSettingRepository;

    @Override
    public FoodManagementSetting createFoodManagementSetting(FoodManagementSetting foodManagementSetting) {
        return foodManagementSettingRepository.save(foodManagementSetting);
    }

    @Override
    public List<FoodManagementSetting> getFoodManagementSetting() {
        return foodManagementSettingRepository.findAll();
    }
}
