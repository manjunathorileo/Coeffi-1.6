package com.dfq.coeffi.foodManagement.orderTracking.foodEstimationTimings;

import java.util.List;

public interface FoodEstimationTimingsService {

    FoodEstimationTimings save(FoodEstimationTimings foodEstimationTimings);
    List<FoodEstimationTimings> getAll();
    FoodEstimationTimings getById(long id);
    void delete(long id);
}
