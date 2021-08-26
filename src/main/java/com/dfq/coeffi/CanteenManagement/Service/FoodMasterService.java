package com.dfq.coeffi.CanteenManagement.Service;

import com.dfq.coeffi.CanteenManagement.Entity.FoodMaster;
import com.dfq.coeffi.CanteenManagement.Entity.FoodTimeMasterAdv;

import java.util.List;

public interface FoodMasterService {

    FoodMaster saveFoodMaster(FoodMaster foodMaster);
    List<FoodMaster> getAllFoodMaster();
    FoodMaster getFoodMaster(long id);
    List<FoodMaster> getFoodMasterByFoodType(FoodTimeMasterAdv foodType);
    FoodMaster deleteFoodMaster(long id);
}
