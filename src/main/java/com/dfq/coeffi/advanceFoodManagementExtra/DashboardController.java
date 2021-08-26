package com.dfq.coeffi.advanceFoodManagementExtra;

import com.dfq.coeffi.CanteenManagement.Entity.CounterDetailsAdv;
import com.dfq.coeffi.CanteenManagement.Entity.FoodTimeMasterAdv;
import com.dfq.coeffi.CanteenManagement.Service.*;
import com.dfq.coeffi.CanteenManagement.dtos.*;
import com.dfq.coeffi.CanteenManagement.employeeBalance.EmployeeBalance;
import com.dfq.coeffi.CanteenManagement.employeeBalance.EmployeeBalanceService;
import com.dfq.coeffi.controller.BaseController;
import com.dfq.coeffi.entity.hr.employee.Employee;
import com.dfq.coeffi.service.hr.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.EntityNotFoundException;
import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@Slf4j
public class DashboardController extends BaseController {

    private final EmployeeRechargeService employeeRechargeService;
    private final EmployeeBalanceService employeeBalanceService;
    private final EmployeeCanteenDetailsService employeeCanteenDetailsService;
    private final FoodTimeService foodTimeService;
    private final CounterDetailsService counterDetailsService;
    private final CatererSettingsService catererSettingsService;
    private final DailyFoodMenuService dailyFoodMenuService;
    private final EmployeeService employeeService;

    @Autowired
    public DashboardController(EmployeeRechargeService employeeRechargeService, EmployeeBalanceService employeeBalanceService, EmployeeCanteenDetailsService employeeCanteenDetailsService, FoodTimeService foodTimeService, CounterDetailsService counterDetailsService, CatererSettingsService catererSettingsService, DailyFoodMenuService dailyFoodMenuService, EmployeeService employeeService) {
        this.employeeRechargeService = employeeRechargeService;
        this.employeeBalanceService = employeeBalanceService;
        this.employeeCanteenDetailsService = employeeCanteenDetailsService;
        this.foodTimeService = foodTimeService;
        this.counterDetailsService = counterDetailsService;
        this.catererSettingsService = catererSettingsService;
        this.dailyFoodMenuService = dailyFoodMenuService;
        this.employeeService = employeeService;
    }

    long employeeCanteenDetailsId = 0;

    @GetMapping("canteen/counter-dashboard/{counterId}")
    public ResponseEntity<CounterDashboardDto> getCounterDashboard(@PathVariable long counterId) throws ParseException {
        CounterDashboardDto counterDashboardDto = new CounterDashboardDto();

        List<EmployeeTypeAdvanced> employeeTypeList = Arrays.asList(EmployeeTypeAdvanced.values());
        counterDashboardDto.setEmployeeTypes(employeeTypeList);

        List<EmployeeCanteenDetails> employeeCanteenDetailsList = employeeCanteenDetailsService.getEmployeeCanteenDetailsByCounterId(counterId);

        Collections.reverse(employeeCanteenDetailsList);
        EmployeeCanteenDetails employeeCanteenDetails = employeeCanteenDetailsList.get(0);

        FoodTimeMasterAdv foodTimeMasterAdv = getCurrentFoodTiming();
        counterDashboardDto.setFoodTimeMasterAdv(foodTimeMasterAdv);

        CounterDetailsAdv counterDetailsAdv = counterDetailsService.getCounterDetails(employeeCanteenDetails.getCounterId());
        counterDashboardDto.setCounterDetailsAdv(counterDetailsAdv);
        counterDashboardDto.setEmployeeName(employeeCanteenDetails.getEmployeeName());

        EmployeeBalance employeeBalance = new EmployeeBalance();
        if (employeeCanteenDetailsId != employeeCanteenDetails.getId()) {
            employeeBalance = employeeBalanceDeduction(foodTimeMasterAdv.getId(), employeeCanteenDetails.getEmpId(), employeeCanteenDetails.getEmployeeType(), employeeCanteenDetails.getCounterId());
        } else {
            Optional<Employee> employee = employeeService.getEmployee(employeeCanteenDetails.getEmpId());
            employeeBalance = employeeBalanceService.getByEmpId(employee.get());
        }
        counterDashboardDto.setBalance(employeeBalance.getActualBalance());
        counterDashboardDto.setMinBalance(employeeBalance.getMinimumBalanceAmount());

        if (employeeBalance.getActualBalance() < employeeBalance.getMinimumBalanceAmount()) {
            counterDashboardDto.setBalanceStatus("Your Balance Is Low Please Recharge!");
        } else {
            counterDashboardDto.setBalanceStatus(" ");
        }
        counterDashboardDto.setFoodStatus("Thank You You Availed It.");

        employeeCanteenDetailsId = employeeCanteenDetails.getId();
        return new ResponseEntity(counterDashboardDto, HttpStatus.OK);
    }

    @GetMapping("canteen/counter-dashboard-employee-list/{counterId}")
    public ResponseEntity<CounterDashboardDto> getCounterDashboardEmployeeList(@PathVariable long counterId) throws ParseException {
        CounterDashboardDto counterDashboardDto = new CounterDashboardDto();

        List<EmployeeTypeAdvanced> employeeTypeList = Arrays.asList(EmployeeTypeAdvanced.values());
        counterDashboardDto.setEmployeeTypes(employeeTypeList);

        List<EmployeeCanteenDetails> employeeCanteenDetailsList = employeeCanteenDetailsService.getEmployeeCanteenDetailsByCounterId(counterId);

        Collections.reverse(employeeCanteenDetailsList);
        EmployeeCanteenDetails employeeCanteenDetails = employeeCanteenDetailsList.get(0);

        FoodTimeMasterAdv foodTimeMasterAdv = getCurrentFoodTiming();
        counterDashboardDto.setFoodTimeMasterAdv(foodTimeMasterAdv);

        List<EmployeeTypeDto> employeeTypeDtos = employeeTypeDtos(employeeCanteenDetails.getCounterId(), foodTimeMasterAdv.getId(), true);
        counterDashboardDto.setEmployeeTypeDtos(employeeTypeDtos);

        long grandTotal = 0;
        counterDashboardDto.setGrandTotal(grandTotal);
        counterDashboardDto = grandTotalCalculationCounterWise(counterDashboardDto);
        return new ResponseEntity(counterDashboardDto, HttpStatus.OK);
    }

    @GetMapping("canteen/caterer-dashboard")
    public ResponseEntity<CatererDashboardDto> getCatererDashBoard() throws ParseException {
        CatererDashboardDto catererDashboardDto = new CatererDashboardDto();
        FoodTimeMasterAdv foodTimeMasterAdv = getCurrentFoodTiming();
        catererDashboardDto.setFoodTimeMasterAdv(foodTimeMasterAdv);
        List<EmployeeTypeAdvanced> employeeTypeList = Arrays.asList(EmployeeTypeAdvanced.values());
        catererDashboardDto.setEmployeeTypes(employeeTypeList);
        List<CounterDto> counterDtos = new ArrayList<>();
        List<CounterDetailsAdv> counterDetailsAdvList = counterDetailsService.getAllCounterDetails();

        for (CounterDetailsAdv counterDetailsAdvObj : counterDetailsAdvList) {
            CounterDto counterDto = new CounterDto();
            counterDto.setCounterId(counterDetailsAdvObj.getId());
            counterDto.setCounterName(counterDetailsAdvObj.getCounterName());
            counterDto.setCounterNo(counterDetailsAdvObj.getCounterNo());

            List<EmployeeTypeDto> employeeTypeDtos = employeeTypeDtos(counterDetailsAdvObj.getId(), foodTimeMasterAdv.getId(), false);
            counterDto.setEmployeeTypeDtos(employeeTypeDtos);
            counterDtos.add(counterDto);
        }
        catererDashboardDto.setCounterDtos(counterDtos);
        CatererDashboardDto catererDashboardDtoObj = grandTotalCalculation(catererDashboardDto);
        return new ResponseEntity(catererDashboardDtoObj, HttpStatus.OK);
    }

    private CounterDashboardDto grandTotalCalculationCounterWise(CounterDashboardDto counterDashboardDto) {
        CounterDto counterDto = new CounterDto();
        List<EmployeeTypeAdvanced> employeeTypes = Arrays.asList(EmployeeTypeAdvanced.values());
        List<EmployeeTypeDto> employeeTypeDtoList = new ArrayList<>();

        for (EmployeeTypeAdvanced employeeType : employeeTypes) {
            EmployeeTypeDto employeeTypeDtoObj = new EmployeeTypeDto();
            long totalEmployee = 0;
            List<EmployeeTypeDto> employeeTypeDtos = counterDashboardDto.getEmployeeTypeDtos();
            for (EmployeeTypeDto employeeTypeDto : employeeTypeDtos) {
                if (employeeTypeDto.getEmpType().equals(employeeType.name())) {
                    totalEmployee = totalEmployee + employeeTypeDto.getTotalEmployee();
                }
            }
            employeeTypeDtoObj.setEmpType(employeeType.name());
            employeeTypeDtoObj.setTotalEmployee(totalEmployee);
            employeeTypeDtoList.add(employeeTypeDtoObj);

        }

        counterDto.setCounterId(0);
        counterDto.setCounterName("Grand Total");
        counterDto.setEmployeeTypeDtos(employeeTypeDtoList);
        return counterDashboardDto;
    }

    private CatererDashboardDto grandTotalCalculation(CatererDashboardDto catererDashboardDto) {
        CounterDto counterDto = new CounterDto();
        List<EmployeeTypeAdvanced> employeeTypes = Arrays.asList(EmployeeTypeAdvanced.values());
        List<CounterDto> counterDtos = catererDashboardDto.getCounterDtos();
        long grandTotal = 0;
        List<EmployeeTypeDto> employeeTypeDtoList = new ArrayList<>();
        for (EmployeeTypeAdvanced employeeType : employeeTypes) {
            long totalEmployee = 0;
            EmployeeTypeDto employeeTypeDtoObj = new EmployeeTypeDto();
            for (CounterDto counterDtoObj : counterDtos) {
                List<EmployeeTypeDto> employeeTypeDtos = counterDtoObj.getEmployeeTypeDtos();
                for (EmployeeTypeDto employeeTypeDto : employeeTypeDtos) {
                    if (employeeTypeDto.getEmpType().equals(employeeType.name())) {
                        totalEmployee = totalEmployee + employeeTypeDto.getTotalEmployee();
                    }
                }
            }
            employeeTypeDtoObj.setEmpType(employeeType.name());
            employeeTypeDtoObj.setTotalEmployee(totalEmployee);
            employeeTypeDtoList.add(employeeTypeDtoObj);
        }
        counterDto.setCounterId(0);
        counterDto.setCounterName("Grand Total");
        counterDto.setEmployeeTypeDtos(employeeTypeDtoList);
        counterDtos.add(counterDto);
        catererDashboardDto.setCounterDtos(counterDtos);
        catererDashboardDto.setGrandTotal(grandTotal);
        return catererDashboardDto;
    }

    private List<EmployeeTypeDto> employeeTypeDtos(long counterId, long foodTypeId, boolean isCounter) {
        Date today = new Date();
        Date todayDate = mySqlFormatDate(today);
        List<EmployeeTypeDto> employeeTypeDtos = new ArrayList<>();


        List<EmployeeTypeAdvanced> employeeTypes = Arrays.asList(EmployeeTypeAdvanced.values());
        List<EmployeeCanteenDetails> employeeCanteenDetailsList = employeeCanteenDetailsService.getEmployeeCanteenDetailsByCounterByFoodType(counterId, foodTypeId);

        for (EmployeeTypeAdvanced employeeType : employeeTypes) {
            long totalEmployee = 0;
            List<EmployeeDetailsDto> employeeDetailsDtos = new ArrayList<>();
            EmployeeTypeDto employeeTypeDtoForEmployee = new EmployeeTypeDto();
            for (EmployeeCanteenDetails employeeCanteenDetailObj : employeeCanteenDetailsList) {
                EmployeeDetailsDto employeeDetailsDto = new EmployeeDetailsDto();
                Date punchDate = mySqlFormatDate(employeeCanteenDetailObj.getPunchDate());
                if (punchDate.equals(todayDate)) {
                    if (employeeCanteenDetailObj.getEmployeeType().equals(employeeType.name())) {
                        employeeDetailsDto.setId(employeeCanteenDetailObj.getEmpId());
                        employeeDetailsDto.setEmpName(employeeCanteenDetailObj.getEmployeeName());
                        employeeDetailsDtos.add(employeeDetailsDto);
                        totalEmployee = totalEmployee + 1;
                    }
                }

            }
            employeeTypeDtoForEmployee.setEmpType(employeeType.name());
            employeeTypeDtoForEmployee.setTotalEmployee(totalEmployee);
            if (isCounter == true) {
                employeeTypeDtoForEmployee.setEmployeeDetailsDtos(employeeDetailsDtos);
            }
            employeeTypeDtos.add(employeeTypeDtoForEmployee);
        }

        return employeeTypeDtos;
    }

    private EmployeeBalance employeeBalanceDeduction(long foodTypeId, long employeeId, String employeeType, long counterId) {

        EmployeeBalance employeeBalance = new EmployeeBalance();
        Optional<Employee> employee = employeeService.getEmployee(employeeId);
        EmployeeBalance employeeBalanceObj = employeeBalanceService.getByEmpId(employee.get());
        employeeBalance = employeeBalanceObj;
        if (employeeBalanceObj == null) {
            throw new EntityNotFoundException("Please Recharge!");
        }

        return employeeBalance;
    }

    private FoodTimeMasterAdv getCurrentFoodTiming() throws ParseException {
        Date today = new Date();
        Time currentTime = getTimeFromString(today);
        List<FoodTimeMasterAdv> foodTimeMasterAdv = foodTimeService.getFoodTimes();
        FoodTimeMasterAdv foodTimeMasterAdvDetails = new FoodTimeMasterAdv();
        for (FoodTimeMasterAdv foodTimeMasterAdvObj : foodTimeMasterAdv) {
            Time fromTime = getTimeFromString(foodTimeMasterAdvObj.getTimeFrom());
            Time toTime = getTimeFromString(foodTimeMasterAdvObj.getTimeTo());
            if ((fromTime.before(currentTime) == true) && (toTime.after(currentTime) == true)) {
                foodTimeMasterAdvDetails = foodTimeMasterAdvObj;
            }
        }
        return foodTimeMasterAdvDetails;
    }

    public Time getTimeFromString(Date dateValue) throws ParseException {
        DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        String formattedDate = timeFormat.format(dateValue);
        long formatedTime = timeFormat.parse(formattedDate).getTime();
        Time newTime = new Time(formatedTime);
        return newTime;
    }

    public Date mySqlFormatDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            date = sdf.parse(sdf.format(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }
}