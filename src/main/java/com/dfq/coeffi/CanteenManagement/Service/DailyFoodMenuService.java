package com.dfq.coeffi.CanteenManagement.Service;

import com.dfq.coeffi.CanteenManagement.Entity.CounterDetailsAdv;
import com.dfq.coeffi.CanteenManagement.Entity.DailyFoodMenu;
import com.dfq.coeffi.CanteenManagement.Entity.FoodTimeMasterAdv;

import java.util.Date;
import java.util.List;

public interface DailyFoodMenuService {

    DailyFoodMenu saveDailyFood(DailyFoodMenu dailyFoodMenu);
    List<DailyFoodMenu> getDailyFood();
    DailyFoodMenu getDailyFoodMenu(long id);
    List<DailyFoodMenu> getDailyFoodMenuByCounterDetails(CounterDetailsAdv counterDetailsAdv);
    List<DailyFoodMenu> getDailyFoodMenuByCounterByFoodType(CounterDetailsAdv counterDetailsAdv, FoodTimeMasterAdv foodType);
    List<DailyFoodMenu> getDailyFoodMenuByFoodType(FoodTimeMasterAdv foodType);
    void deleteDailyFoodMenu(long id);
    List<DailyFoodMenu> getTodaysDailyFoodMenu(Date today, long foodTypeId, long counterId);
    List<DailyFoodMenu> getTodaysDailyFoodMenuByFoodtype(Date today, long foodTypeId);
}
