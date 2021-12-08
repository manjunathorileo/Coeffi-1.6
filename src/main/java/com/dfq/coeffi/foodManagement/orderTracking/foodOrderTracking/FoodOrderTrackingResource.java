package com.dfq.coeffi.foodManagement.orderTracking.foodOrderTracking;

import com.dfq.coeffi.controller.BaseController;
import com.dfq.coeffi.entity.hr.employee.EmployeeType;
import com.dfq.coeffi.foodManagement.FoodTimeMaster;
import com.dfq.coeffi.foodManagement.FoodTimeRepository;
import com.dfq.coeffi.foodManagement.orderTracking.foodOrderReport.FoodOrderReportDto;
import com.dfq.coeffi.util.DateUtil;
import jxl.format.VerticalAlignment;
import jxl.write.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.time.Month;
import java.util.*;

import static jxl.format.Alignment.CENTRE;

@RestController
@Slf4j
public class FoodOrderTrackingResource extends BaseController {

    private final FoodOrderTrackingService foodOrderTrackingService;
    private final FoodTimeRepository foodTimeRepository;

    @Autowired
    public FoodOrderTrackingResource(FoodOrderTrackingService foodOrderTrackingService, FoodTimeRepository foodTimeRepository) {
        this.foodOrderTrackingService = foodOrderTrackingService;
        this.foodTimeRepository = foodTimeRepository;
    }

    @GetMapping("/food-order-tracking")
    public ResponseEntity<List<FoodOrderTracking>> getAllFoodOrderTracking() {
        List<FoodOrderTracking> foodOrderTrackings = foodOrderTrackingService.getAll();
        if (foodOrderTrackings.isEmpty()) {
            throw new EntityNotFoundException("There is no food order present.");
        }
        return new ResponseEntity<>(foodOrderTrackings, HttpStatus.OK);
    }

    @GetMapping("/food-order-tracking/{id}")
    public ResponseEntity<FoodOrderTracking> getFoodOrderTracking(@PathVariable long id) {
        FoodOrderTracking foodOrderTrackings = foodOrderTrackingService.getById(id);
        return new ResponseEntity<>(foodOrderTrackings, HttpStatus.OK);
    }

    @GetMapping("/food-order-tracking-current-day")
    public ResponseEntity<FoodOrderTracking> getFoodOrderTrackingCurrentDay() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date today = new Date();
        String todayDate = dateFormat.format(today);
        Date currentDate = DateUtil.convertToDate(todayDate);

        List<FoodOrderTrackingDto> foodOrderTrackingDtos = new ArrayList<>();
        List<FoodTimeMaster> foodTypeList = foodTimeRepository.findAll();
        for (FoodTimeMaster foodType : foodTypeList) {
            FoodOrderTrackingDto foodOrderTrackingDto = new FoodOrderTrackingDto();
            List<FoodOrderTracking> foodOrderTrackingList = foodOrderTrackingService.getByfoodType(foodType.getFoodType());
            long totalEmployeeCount = 0;
            long totalVisitorCount = 0;
            long totalContractCount = 0;
            for (FoodOrderTracking foodOrderTrackingObj : foodOrderTrackingList) {
                String orderOn = dateFormat.format(foodOrderTrackingObj.getOrderedOn());
                Date orderOnDate = DateUtil.convertToDate(orderOn);
                if (orderOnDate.equals(currentDate)) {
                    if (foodOrderTrackingObj.getEmployeeType().equalsIgnoreCase("EMPLOYEE")) {
                        totalEmployeeCount = totalEmployeeCount +1;
                    } else if (foodOrderTrackingObj.getEmployeeType().equalsIgnoreCase("VISITOR")) {
                        totalVisitorCount = totalVisitorCount + 1;
                    } else if (foodOrderTrackingObj.getEmployeeType().equalsIgnoreCase("CONTRACT")) {
                        totalContractCount = totalContractCount + 1;
                    }
                }
            }
            foodOrderTrackingDto.setFoodType(foodType.getFoodType());
            foodOrderTrackingDto.setEmployeeCount(totalEmployeeCount);
            foodOrderTrackingDto.setVisitorCount(totalVisitorCount);
            foodOrderTrackingDto.setContractCount(totalContractCount);
            foodOrderTrackingDtos.add(foodOrderTrackingDto);
        }
        return new ResponseEntity(foodOrderTrackingDtos, HttpStatus.OK);
    }
}