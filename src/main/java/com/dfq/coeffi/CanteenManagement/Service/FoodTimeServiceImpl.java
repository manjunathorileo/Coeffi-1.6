package com.dfq.coeffi.CanteenManagement.Service;

import com.dfq.coeffi.CanteenManagement.Entity.FoodTimeMasterAdv;
import com.dfq.coeffi.CanteenManagement.Repository.FoodTimeAdvRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class FoodTimeServiceImpl implements FoodTimeService {

    @Autowired
    private FoodTimeAdvRepository foodTimeRepository;

    @Override
    public FoodTimeMasterAdv createFoodTime(FoodTimeMasterAdv foodTimeMasterAdv) {
        Date today = new Date();
        foodTimeMasterAdv.setCreatedOn(today);
        foodTimeMasterAdv.setStatus(true);
        return foodTimeRepository.save(foodTimeMasterAdv);
    }

    @Override
    public List<FoodTimeMasterAdv> getFoodTimes() {
        List<FoodTimeMasterAdv> foodTimeMasterAdvList = new ArrayList<>();
        List<FoodTimeMasterAdv> foodTimeMasterAdvs = foodTimeRepository.findAll();
        for (FoodTimeMasterAdv foodTimeMasterAdv : foodTimeMasterAdvs) {
            if (foodTimeMasterAdv.getStatus().equals(true)){
                if (foodTimeMasterAdv.getCatererDetailsAdv().getStatus().equals(true)) {
                    foodTimeMasterAdvList.add(foodTimeMasterAdv);
                } else {
                    foodTimeMasterAdv.setStatus(false);
                    foodTimeRepository.save(foodTimeMasterAdv);
                }
            }
        }
        return foodTimeMasterAdvList;
    }

    @Override
    public FoodTimeMasterAdv getFoodTime(long id) {
        return foodTimeRepository.findById(id);
    }

    @Override
    public void deleteFoodTime(long id) {
        FoodTimeMasterAdv foodTimeMasterAdv = getFoodTime(id);
        foodTimeMasterAdv.setStatus(false);
        foodTimeRepository.save(foodTimeMasterAdv);
    }
}