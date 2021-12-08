package com.dfq.coeffi.foodManagement.orderTracking.foodEstimationTimings;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FoodEstimationTimingsServiceImpl implements FoodEstimationTimingsService {

    @Autowired
    private FoodEstimationTimingsRepository foodEstimationTimingsRepository;

    @Override
    public FoodEstimationTimings save(FoodEstimationTimings foodEstimationTimings) {
        FoodEstimationTimings foodEstimationTimingsObj = foodEstimationTimingsRepository.save(foodEstimationTimings);
        return foodEstimationTimingsObj;
    }

    @Override
    public List<FoodEstimationTimings> getAll() {
        List<FoodEstimationTimings> foodEstimationTimings = foodEstimationTimingsRepository.findAll();
        return foodEstimationTimings;
    }

    @Override
    public FoodEstimationTimings getById(long id) {
        FoodEstimationTimings foodEstimationTimings = foodEstimationTimingsRepository.findOne(id);
        return foodEstimationTimings;
    }

    @Override
    public void delete(long id) {
        foodEstimationTimingsRepository.delete(id);
    }
}
