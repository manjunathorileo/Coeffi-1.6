package com.dfq.coeffi.foodManagement.orderTracking.foodOrderPunchLog;

import java.util.List;

public interface FoodOrderPunchLogService {

    FoodOrderPunchLog save(FoodOrderPunchLog foodOrderPunchLog);
    List<FoodOrderPunchLog> getAll();
    List<FoodOrderPunchLog> getByAkgStatus(Boolean status);
}
