package com.dfq.coeffi.foodManagement.orderTracking.foodOrderPunchLog;

import com.dfq.coeffi.foodManagement.orderTracking.foodOrderTracking.FoodOrderTracking;
import com.dfq.coeffi.foodManagement.orderTracking.foodOrderTracking.FoodOrderTrackingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FoodOrderPunchLogServiceImpl implements FoodOrderPunchLogService {

    @Autowired
    private FoodOrderPunchLogRepository foodOrderPunchLogRepository;

    @Override
    public FoodOrderPunchLog save(FoodOrderPunchLog foodOrderPunchLog) {
        FoodOrderPunchLog foodOrderPunchLogObj = foodOrderPunchLogRepository.save(foodOrderPunchLog);
        return foodOrderPunchLogObj;
    }

    @Override
    public List<FoodOrderPunchLog> getAll() {
        List<FoodOrderPunchLog> foodOrderPunchLogs = foodOrderPunchLogRepository.findAll();
        return foodOrderPunchLogs;
    }

    @Override
    public List<FoodOrderPunchLog> getByAkgStatus(Boolean status) {
        List<FoodOrderPunchLog> foodOrderPunchLogs = foodOrderPunchLogRepository.findByAkg(status);
        return foodOrderPunchLogs;
    }
}
