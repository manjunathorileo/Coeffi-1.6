package com.dfq.coeffi.CanteenManagement.Service;

import com.dfq.coeffi.CanteenManagement.Entity.CounterDetailsAdv;
import com.dfq.coeffi.CanteenManagement.Entity.DailyFoodMenu;
import com.dfq.coeffi.CanteenManagement.Entity.FoodTimeMasterAdv;
import com.dfq.coeffi.CanteenManagement.Repository.DailyFoodRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class DailyFoodMenuServiceImpl implements DailyFoodMenuService {

    @Autowired
    private DailyFoodRepository dailyFoodRepository;

    @Override
    public DailyFoodMenu saveDailyFood(DailyFoodMenu dailyFoodMenu) {
        return dailyFoodRepository.save(dailyFoodMenu);
    }

    @Override
    public List<DailyFoodMenu> getDailyFood() {
        List<DailyFoodMenu> dailyFoodMenus = new ArrayList<>();
        List<DailyFoodMenu> dailyFoodMenusObj = dailyFoodRepository.findAll();
        for (DailyFoodMenu dailyFoodMenuObj:dailyFoodMenusObj) {
            if (dailyFoodMenuObj.getBuildingDetails().getStatus().equals(true)
                    && dailyFoodMenuObj.getCounterDetailsAdv().getStatus().equals(true)
                    && dailyFoodMenuObj.getFoodType().getStatus().equals(true)){
                dailyFoodMenus.add(dailyFoodMenuObj);
            } else {
                dailyFoodRepository.delete(dailyFoodMenuObj.getId());
            }
        }
        return dailyFoodMenus;
    }

    @Override
    public DailyFoodMenu getDailyFoodMenu(long id) {
        return dailyFoodRepository.findById(id);
    }

    @Override
    public List<DailyFoodMenu> getDailyFoodMenuByCounterDetails(CounterDetailsAdv counterDetailsAdv) {
        List<DailyFoodMenu> dailyFoodMenus = new ArrayList<>();
        List<DailyFoodMenu> dailyFoodMenusList = dailyFoodRepository.findByCounterDetailsAdv(counterDetailsAdv);
        for (DailyFoodMenu dailyFoodMenuObj:dailyFoodMenusList) {
            if (dailyFoodMenuObj.getBuildingDetails().getStatus().equals(true)
                    && dailyFoodMenuObj.getCounterDetailsAdv().getStatus().equals(true)
                    && dailyFoodMenuObj.getFoodType().getStatus().equals(true)) {
                dailyFoodMenus.add(dailyFoodMenuObj);
            }
        }
        return dailyFoodMenus;
    }

    @Override
    public List<DailyFoodMenu> getDailyFoodMenuByCounterByFoodType(CounterDetailsAdv counterDetailsAdv, FoodTimeMasterAdv foodType) {
        List<DailyFoodMenu> dailyFoodMenus = new ArrayList<>();
        List<DailyFoodMenu> dailyFoodMenusList = dailyFoodRepository.findByCounterDetailsByFoodType(counterDetailsAdv.getId(),foodType.getId());
        for (DailyFoodMenu dailyFoodMenuObj:dailyFoodMenusList) {
            if (dailyFoodMenuObj.getBuildingDetails().getStatus().equals(true)
                    && dailyFoodMenuObj.getCounterDetailsAdv().getStatus().equals(true)
                    && dailyFoodMenuObj.getFoodType().getStatus().equals(true)) {
                dailyFoodMenus.add(dailyFoodMenuObj);
            }
        }
        return dailyFoodMenus;
    }

    @Override
    public List<DailyFoodMenu> getDailyFoodMenuByFoodType(FoodTimeMasterAdv foodType) {
        List<DailyFoodMenu> dailyFoodMenus = new ArrayList<>();
        List<DailyFoodMenu> dailyFoodMenusList = dailyFoodRepository.findByFoodType(foodType);
        for (DailyFoodMenu dailyFoodMenuObj:dailyFoodMenusList) {
            if (dailyFoodMenuObj.getBuildingDetails().getStatus().equals(true)
                    && dailyFoodMenuObj.getCounterDetailsAdv().getStatus().equals(true)
                    && dailyFoodMenuObj.getFoodType().getStatus().equals(true)) {
                dailyFoodMenus.add(dailyFoodMenuObj);
            }
        }
        return dailyFoodMenus;
    }

    @Override
    public void deleteDailyFoodMenu(long id) {
        dailyFoodRepository.delete(id);
    }

    @Override
    public List<DailyFoodMenu> getTodaysDailyFoodMenu(Date today, long foodTypeId, long counterId) {
        List<DailyFoodMenu> dailyFoodMenus = new ArrayList<>();
        List<DailyFoodMenu> dailyFoodMenusList = dailyFoodRepository.findByTodaysDate(today,foodTypeId,counterId);
        for (DailyFoodMenu dailyFoodMenuObj:dailyFoodMenusList) {
            if (dailyFoodMenuObj.getBuildingDetails().getStatus().equals(true)
                    && dailyFoodMenuObj.getCounterDetailsAdv().getStatus().equals(true)
                    && dailyFoodMenuObj.getFoodType().getStatus().equals(true)) {
                dailyFoodMenus.add(dailyFoodMenuObj);
            }
        }
        return dailyFoodMenus;
    }

    @Override
    public List<DailyFoodMenu> getTodaysDailyFoodMenuByFoodtype(Date today, long foodTypeId) {
        List<DailyFoodMenu> dailyFoodMenus = new ArrayList<>();
        List<DailyFoodMenu> dailyFoodMenusList = dailyFoodRepository.findByTodaysDateByFoodtype(today,foodTypeId);
        for (DailyFoodMenu dailyFoodMenuObj:dailyFoodMenusList) {
            if (dailyFoodMenuObj.getBuildingDetails().getStatus().equals(true)
                    && dailyFoodMenuObj.getCounterDetailsAdv().getStatus().equals(true)
                    && dailyFoodMenuObj.getFoodType().getStatus().equals(true)) {
                dailyFoodMenus.add(dailyFoodMenuObj);
            }
        }
        return dailyFoodMenus;
    }
}