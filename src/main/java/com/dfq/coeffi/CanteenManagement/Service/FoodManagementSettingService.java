package com.dfq.coeffi.CanteenManagement.Service;

import com.dfq.coeffi.CanteenManagement.Entity.FoodManagementSetting;

import java.util.List;

public interface FoodManagementSettingService {

    FoodManagementSetting createFoodManagementSetting(FoodManagementSetting foodManagementSetting);
    List<FoodManagementSetting> getFoodManagementSetting();

}
