package com.dfq.coeffi.CanteenManagement.Service;

import com.dfq.coeffi.CanteenManagement.Entity.FoodTimeMasterAdv;

import java.util.List;

public interface FoodTimeService {

    FoodTimeMasterAdv createFoodTime(FoodTimeMasterAdv foodTimeMasterAdv);
    List<FoodTimeMasterAdv> getFoodTimes();
    FoodTimeMasterAdv getFoodTime(long id);
    void deleteFoodTime(long id);
}
