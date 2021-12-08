package com.dfq.coeffi.foodManagement.orderTracking.foodOrderPunchLog;

import com.dfq.coeffi.employeePermanentContract.repositories.PermanentContractRepo;
import com.dfq.coeffi.entity.hr.employee.Employee;
import com.dfq.coeffi.entity.hr.employee.EmployeeType;
import com.dfq.coeffi.foodManagement.FoodTimeMaster;
import com.dfq.coeffi.foodManagement.FoodTimeRepository;
import com.dfq.coeffi.foodManagement.orderTracking.foodEstimationTimings.FoodEstimationTimings;
import com.dfq.coeffi.foodManagement.orderTracking.foodEstimationTimings.FoodEstimationTimingsService;
import com.dfq.coeffi.foodManagement.orderTracking.foodOrderTracking.FoodOrderTracking;
import com.dfq.coeffi.foodManagement.orderTracking.foodOrderTracking.FoodOrderTrackingService;
import com.dfq.coeffi.service.hr.EmployeeService;
import com.dfq.coeffi.util.DateUtil;
import com.dfq.coeffi.visitor.Services.VisitorPassService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Optional;

@EnableScheduling
@RestController
public class FoodOrderPunchLogScheduler {

    private final FoodOrderPunchLogService foodOrderPunchLogService;
    private final FoodOrderTrackingService foodOrderTrackingService;
    private final EmployeeService employeeService;
    private final PermanentContractRepo permanentContractRepo;
    private final VisitorPassService visitorService;
    private final FoodTimeRepository foodTimeRepository;
    private final FoodEstimationTimingsService foodEstimationTimingsService;

    @Autowired
    public FoodOrderPunchLogScheduler(FoodOrderPunchLogService foodOrderPunchLogService, FoodOrderTrackingService foodOrderTrackingService, EmployeeService employeeService, PermanentContractRepo permanentContractRepo, VisitorPassService visitorService, FoodTimeRepository foodTimeRepository, FoodEstimationTimingsService foodEstimationTimingsService) {
        this.foodOrderPunchLogService = foodOrderPunchLogService;
        this.foodOrderTrackingService = foodOrderTrackingService;
        this.employeeService = employeeService;
        this.permanentContractRepo = permanentContractRepo;
        this.visitorService = visitorService;
        this.foodTimeRepository = foodTimeRepository;
        this.foodEstimationTimingsService = foodEstimationTimingsService;
    }

    @Scheduled(cron = "*/5 * * * * *")
    public void trackFoodEstimation() throws ParseException {

        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
        List<FoodOrderPunchLog> foodOrderPunchLogObj = foodOrderPunchLogService.getByAkgStatus(false);
        for (FoodOrderPunchLog foodOrderPunchLog : foodOrderPunchLogObj) {
            System.out.println("*********************FoodEstimationLog*******************");

            String employeeType = "";
            String foodType = "";
            String shiftName="";


            int orderHour = DateUtil.getRunningHour(foodOrderPunchLog.getOrderedOn());
            List<FoodEstimationTimings> foodEstimationTimingsList = foodEstimationTimingsService.getAll();
            for (FoodEstimationTimings foodEstimationTimings:foodEstimationTimingsList){
                int startHour = DateUtil.getRunningHour(foodEstimationTimings.getTimeFrom());
                int endHour = DateUtil.getRunningHour(foodEstimationTimings.getTimeTo());
                if (orderHour>=startHour && orderHour<=endHour){
                    foodType = foodEstimationTimings.getFoodType();
                    shiftName = foodEstimationTimings.getShiftName();

                }
            }

            FoodOrderTracking foodOrderTracking = new FoodOrderTracking();
            Optional<Employee> employeeOptional = employeeService.checkEmployeeCode(foodOrderPunchLog.getEmployeeCode());
            Employee employee = employeeOptional.get();

            if (employee.getEmployeeType().equals(EmployeeType.PERMANENT) || employee.getEmployeeType().equals(EmployeeType.PERMANENT_WORKER)) {
                employeeType = "EMPLOYEE";
            } else if (employee.getEmployeeType().equals(EmployeeType.CONTRACT)) {
                employeeType = "CONTRACT";
            } else {
                employeeType = "VISITOR";
            }

            foodOrderTracking.setEmployeeId(String.valueOf(employee.getId()));
            foodOrderTracking.setEmployeeCode(employee.getEmployeeCode());
            foodOrderTracking.setEmployeeName(employee.getFirstName());
            foodOrderTracking.setDept(employee.getDepartmentName());
            foodOrderTracking.setCompanyName(employee.getCompany());
            foodOrderTracking.setLocation(employee.getLocation());
            foodOrderTracking.setOrderedOn(foodOrderPunchLog.getOrderedOn());
            foodOrderTracking.setEmployeeType(employeeType);
            foodOrderTracking.setFoodType(foodType);
            foodOrderTracking.setMarkedOn(foodOrderPunchLog.getOrderedOn());
            foodOrderTracking.setShiftName(shiftName);
            FoodOrderTracking foodOrderTrackingObj = foodOrderTrackingService.save(foodOrderTracking);

            foodOrderPunchLog.setAkg(true);
            FoodOrderPunchLog foodOrderPunchLogSaved = foodOrderPunchLogService.save(foodOrderPunchLog);
        }
    }
}
