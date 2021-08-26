package com.dfq.coeffi.CanteenManagement.Service;

import com.dfq.coeffi.CanteenManagement.Entity.FoodMaster;
import com.dfq.coeffi.CanteenManagement.Entity.FoodTimeMasterAdv;
import com.dfq.coeffi.CanteenManagement.Repository.FoodMasterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class FoodMasterServiceImpl implements FoodMasterService {

    @Autowired
    private FoodMasterRepository foodMasterRepository;

    @Override
    public FoodMaster saveFoodMaster(FoodMaster foodMaster) {
        foodMaster.setStatus(true);
        return foodMasterRepository.save(foodMaster);
    }

    @Override
    public List<FoodMaster> getAllFoodMaster() {
        List<FoodMaster> foodMasters = new ArrayList<>();
        List<FoodMaster> foodMasterList = foodMasterRepository.findAll();
        for (FoodMaster foodMaster:foodMasterList) {
            if (foodMaster.getStatus().equals(true)){
                if (foodMaster.getFoodType().getStatus().equals(true)) {
                    foodMasters.add(foodMaster);
                } else {
                    foodMaster.setStatus(false);
                    foodMasterRepository.save(foodMaster);
                }
            }
        }
        return foodMasters;
    }

    @Override
    public FoodMaster getFoodMaster(long id) {
        return foodMasterRepository.findById(id);
    }

    @Override
    public List<FoodMaster> getFoodMasterByFoodType(FoodTimeMasterAdv foodType) {
        List<FoodMaster> foodMasters = new ArrayList<>();
        List<FoodMaster> foodMasterList = foodMasterRepository.findByFoodType(foodType);
        for (FoodMaster foodMaster:foodMasterList) {
            if (foodMaster.getStatus().equals(true)){
                foodMasters.add(foodMaster);
            }
        }
        return foodMasters;
    }

    @Override
    public FoodMaster deleteFoodMaster(long id) {
        FoodMaster foodMaster = foodMasterRepository.findById(id);
        foodMaster.setStatus(false);
        return foodMasterRepository.save(foodMaster);
    }
}