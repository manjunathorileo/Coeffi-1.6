package com.dfq.coeffi.advanceFoodManagementExtra;

import com.dfq.coeffi.CanteenManagement.Entity.CatererSettingsAdv;
import com.dfq.coeffi.CanteenManagement.Entity.EmployeeRecharge;
import com.dfq.coeffi.CanteenManagement.Entity.FoodTimeMasterAdv;
import com.dfq.coeffi.CanteenManagement.Service.CatererSettingsService;
import com.dfq.coeffi.CanteenManagement.Service.EmployeeRechargeService;
import com.dfq.coeffi.CanteenManagement.Service.FoodTimeService;
import com.dfq.coeffi.CanteenManagement.employeeBalance.EmployeeBalance;
import com.dfq.coeffi.CanteenManagement.employeeBalance.EmployeeBalanceService;
import com.dfq.coeffi.controller.BaseController;
import com.dfq.coeffi.entity.hr.employee.Employee;
import com.dfq.coeffi.foodManagement.FoodTracker;
import com.dfq.coeffi.service.hr.EmployeeService;
import com.dfq.coeffi.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.EntityNotFoundException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@Slf4j
@EnableScheduling
public class CanteenPunchController extends BaseController {

    @Autowired
    EmployeeCanteenAttendanceService employeeCanteenAttendanceService;
    @Autowired
    EmployeeService employeeService;
    @Autowired
    FoodTimeService foodTimeService;
    @Autowired
    EmployeeCanteenDetailsService employeeCanteenDetailsService;
    @Autowired
    CatererSettingsService catererSettingsService;
    @Autowired
    EmployeeBalanceService employeeBalanceService;
    @Autowired
    EmployeeRechargeService employeeRechargeService;


    @GetMapping("canteen-punch/sync-todays-entries")
    @Scheduled(initialDelay = 1114, fixedRate = 1111)
    public ResponseEntity<List<EmployeeCanteenAttendance>> synchTodaysCanteenEntrys() throws ParseException {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = simpleDateFormat.parse(simpleDateFormat.format(new Date()));
//        System.out.println("Datetoday canteen: " + date);
        List<EmployeeCanteenAttendance> realBioAttendances = employeeCanteenAttendanceService.getPunchedAttendance(date);

        for (EmployeeCanteenAttendance realBioAttendance : realBioAttendances) {
            //-----------Employee---------------------------------------
            System.out.println("id: " + realBioAttendance.getCardId());
            Employee employee = employeeService.getByRfid(realBioAttendance.getCardId());
            if (employee != null) {
                int exactTime = DateUtil.getRunningHour(realBioAttendance.getPunchDateTime());
                FoodTimeMasterAdv foodTimeMaster = getCurrentFoodType(exactTime);
                EmployeeCanteenDetails foodTracker = null;
                if (foodTimeMaster != null) {
                    foodTracker = employeeCanteenDetailsService.findByMarkedOnAndEmployeeCodeAndFoodTypeName(realBioAttendance.getPunchDateTime(), employee.getEmployeeCode(), foodTimeMaster.getFoodType());
                    EmployeeBalance employeeBalanceObj = employeeBalanceService.getByEmpId(employee);
                    CatererSettingsAdv catererSettingsAdv = catererSettingsService.getCatererSettingsAdvByFoodTypeByEmployeeTypeByCounter(foodTimeMaster.getId(), employee.getEmployeeType().name(), Long.valueOf(realBioAttendance.getCounter()));
                    if (foodTracker == null) {
                        if (employeeBalanceObj != null && catererSettingsAdv != null) {
                            foodTracker = new EmployeeCanteenDetails();
                            foodTracker = employeeBalanceDeduction(foodTimeMaster.getId(), employee.getId(), employee.getEmployeeType().name(), Long.valueOf(realBioAttendance.getCounter()), foodTracker);

                            foodTracker.setCaterer("");
                            if (employee.getCompany() != null) {
                                foodTracker.setCompanyName(employee.getCompany());
                            } else {
                                foodTracker.setCompanyName("");
                            }
                            if (employee.getDepartmentName() != null) {
                                foodTracker.setDepartmentName(employee.getDepartmentName());
                            } else {
                                foodTracker.setDepartmentName("");
                            }
                            foodTracker.setEmpId(employee.getId());
                            foodTracker.setEmployeeCode(employee.getEmployeeCode());
                            foodTracker.setEmployeeName(employee.getFirstName());
                            foodTracker.setEmployeeType(employee.getEmployeeType().name());
                            foodTracker.setFoodTypeName(foodTimeMaster.getFoodType());
                            foodTracker.setFoodTypeId(foodTimeMaster.getId());
                            foodTracker.setMarkedOn(realBioAttendance.getPunchDateTime());
                            foodTracker.setPunchDate(realBioAttendance.getPunchDateTime());
                            foodTracker.setCounterId(Long.parseLong(realBioAttendance.getCounter()));
                            if (employee.getLocation() != null) {
                                foodTracker.setLocationName(employee.getLocation());
                            } else {
                                foodTracker.setLocationName("");
                            }
                            employeeCanteenDetailsService.saveEmployeeCanteenDetails(foodTracker);
                        }
                    }
                }
            }
            //-----------Employee---------------------------------------

        }
        return new ResponseEntity<>(realBioAttendances, HttpStatus.OK);
    }


    private EmployeeCanteenDetails employeeBalanceDeduction(long foodTypeId, long employeeId, String employeeType, long counterId, EmployeeCanteenDetails employeeCanteenDetails) {

        CatererSettingsAdv catererSettingsAdv = catererSettingsService.getCatererSettingsAdvByFoodTypeByEmployeeTypeByCounter(foodTypeId, employeeType, counterId);
        if (catererSettingsAdv != null) {

            EmployeeBalance employeeBalance = new EmployeeBalance();
            Optional<Employee> employee = employeeService.getEmployee(employeeId);
            EmployeeBalance employeeBalanceObj = employeeBalanceService.getByEmpId(employee.get());
            employeeBalance = employeeBalanceObj;
            if (employeeBalanceObj == null) {
//                throw new EntityNotFoundException("Please Recharge!");
                EmployeeRecharge employeeRecharge = new EmployeeRecharge();
                employeeRecharge.setTotalRechargeAmount(0);
                employeeRecharge.setActualBalance(0);
                employeeRecharge.setIsNew(true);
                employeeRecharge.setEmpId(employeeId);
                employeeRecharge.setEmployee(employee.get());
                employeeRecharge.setEmployeeCategory(employee.get().getEmployeeType().name());
                employeeRecharge.setRechargeAmount(0);
                employeeRecharge = employeeRechargeService.saveRecharge(employeeRecharge);

                employeeBalance = new EmployeeBalance();
                employeeBalance.setTotaldebit(/*employeeBalance.getTotaldebit() +*/ catererSettingsAdv.getEmployeeRate());
                employeeBalance.setActualBalance(employeeBalanceObj.getActualBalance() - catererSettingsAdv.getEmployeeRate());

                employeeBalance.setEmpId(employeeRecharge.getEmpId());
                employeeBalance.setEmpName(employeeRecharge.getEmployee().getFirstName());
                employeeBalance.setEmpType(employeeRecharge.getEmployeeCategory());
                employeeBalance.setEmployee(employeeRecharge.getEmployee());
                employeeBalance.setMinimumBalanceAmount(employeeRecharge.getMinimumBalanceAmount());
                employeeBalance.setActualBalance(employeeRecharge.getActualBalance());
                employeeBalance.setIsBalanceLow(Boolean.FALSE);
                employeeBalance.setTotalCredit(employeeRecharge.getRechargeAmount());


                List<EmployeeRecharge> employeeRechargeList = employeeRechargeService.getEmployeeRechargeByEmpId(employeeId);
                Collections.reverse(employeeRechargeList);
                employeeRecharge = employeeRechargeList.get(0);
                employeeCanteenDetails.setOpeningBalance(employeeRechargeList.get(0).getActualBalance());
                employeeCanteenDetails.setFoodRate(catererSettingsAdv.getEmployeeRate());
                employeeRecharge.setActualBalance(employeeRechargeList.get(0).getActualBalance() - catererSettingsAdv.getEmployeeRate());
                employeeCanteenDetails.setClosingBalance(employeeRecharge.getActualBalance());
                employeeRechargeService.saveRecharge(employeeRecharge);
                employeeBalanceService.saveEmployeeBalance(employeeBalance);
            } else {
                employeeBalance.setTotaldebit(employeeBalance.getTotaldebit() + catererSettingsAdv.getEmployeeRate());
                employeeBalance.setActualBalance(employeeBalanceObj.getActualBalance() - catererSettingsAdv.getEmployeeRate());
                List<EmployeeRecharge> employeeRechargeList = employeeRechargeService.getEmployeeRechargeByEmpId(employeeId);
                Collections.reverse(employeeRechargeList);
                EmployeeRecharge employeeRecharge = employeeRechargeList.get(0);
                employeeCanteenDetails.setOpeningBalance(employeeRechargeList.get(0).getActualBalance());
                employeeCanteenDetails.setFoodRate(catererSettingsAdv.getEmployeeRate());
                employeeRecharge.setActualBalance(employeeRechargeList.get(0).getActualBalance() - catererSettingsAdv.getEmployeeRate());
                employeeCanteenDetails.setClosingBalance(employeeRecharge.getActualBalance());
                employeeRechargeService.saveRecharge(employeeRecharge);
                employeeBalanceService.saveEmployeeBalance(employeeBalance);

            }
        }


        return employeeCanteenDetails;
    }

    //Food management advance
    public boolean triggerEmployeeCanteenDetails(long employeeId, String foodType, Date punchedDate) throws ParseException {
        SimpleDateFormat dateFormate = new SimpleDateFormat("yyyy-MM-dd");
        String formatedDateString = dateFormate.format(punchedDate);
        Date formatedDate = dateFormate.parse(formatedDateString);
        EmployeeCanteenDetails employeeCanteenDetails = employeeCanteenDetailsService.getEmployeeCanteenDetailsByEmployeeIdAndFoodTypeAndDate(employeeId, foodType, formatedDate);
        boolean ismultipleEntry = false;
        if (employeeCanteenDetails == null) {
            ismultipleEntry = true;
            return ismultipleEntry;
        }
        if (employeeCanteenDetails != null) {
            long difference_In_Time = 0;
            if (employeeCanteenDetails.getPunchDate().before(punchedDate)) {
                difference_In_Time = punchedDate.getTime() - employeeCanteenDetails.getPunchDate().getTime();
            } else if (employeeCanteenDetails.getPunchDate().after(punchedDate)) {
                difference_In_Time = employeeCanteenDetails.getPunchDate().getTime() - punchedDate.getTime();
            }
            long difference_In_Seconds = (difference_In_Time / 1000) % 60;
            if (difference_In_Seconds <= 30) {
                //Ignore punch
            } else if (difference_In_Seconds > 30) {
                throw new EntityNotFoundException("Food already taken using this card " + employeeId);
            }
        }
        return ismultipleEntry;
    }


    public FoodTimeMasterAdv getCurrentFoodType(int exactTime) {
        System.out.println("hr::::::::::::::" + DateUtil.getRunningHour());
        int currentTime = exactTime;
        System.out.println("CurrentTime " + currentTime);
        List<FoodTimeMasterAdv> shiftList = new ArrayList<>();
        FoodTimeMasterAdv shift = null;
        List<FoodTimeMasterAdv> shifts = null;
        if (shifts == null) {
            shifts = foodTimeService.getFoodTimes();
        }
        for (FoodTimeMasterAdv runningShift : shifts) {
            if (currentTime >= DateUtil.getRunningHour(runningShift.getTimeFrom()) && currentTime < DateUtil.getRunningHour(runningShift.getTimeTo())) {
                shiftList.add(runningShift);
            }
        }
        if (shiftList.isEmpty()) {
            return null;
        } else {
            return shiftList.get(0);
        }
    }
}
