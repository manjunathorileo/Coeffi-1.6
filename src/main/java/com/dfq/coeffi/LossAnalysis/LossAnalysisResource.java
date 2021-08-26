package com.dfq.coeffi.LossAnalysis;

import com.dfq.coeffi.LossAnalysis.LossCategory.LossCategory;
import com.dfq.coeffi.LossAnalysis.LossCategory.LossCategoryService;
import com.dfq.coeffi.LossAnalysis.LossSubCategory.LossSubCategory;
import com.dfq.coeffi.LossAnalysis.LossSubCategory.LossSubCategoryService;
import com.dfq.coeffi.LossAnalysis.currentProductionRate.CurrentProductionRate;
import com.dfq.coeffi.LossAnalysis.currentProductionRate.CurrentProductionRateService;
import com.dfq.coeffi.LossAnalysis.dto.LossAnalysisDto;
import com.dfq.coeffi.LossAnalysis.dto.LossCategoryDto;
import com.dfq.coeffi.LossAnalysis.dto.LossSubCategoryDto;
import com.dfq.coeffi.LossAnalysis.lossAnalysisEntryTimeRule.LossAnalysisEntryTimeRule;
import com.dfq.coeffi.LossAnalysis.lossAnalysisEntryTimeRule.LossAnalysisEntryTimeRuleService;
import com.dfq.coeffi.LossAnalysis.machine.MachineMaster;
import com.dfq.coeffi.LossAnalysis.machine.MachineMasterService;
import com.dfq.coeffi.LossAnalysis.productionLine.ProductionLineMaster;
import com.dfq.coeffi.LossAnalysis.productionLine.ProductionLineMasterService;
import com.dfq.coeffi.SOPDetails.SOPCategory.SopCategoryService;
import com.dfq.coeffi.controller.BaseController;
import com.dfq.coeffi.master.shift.Shift;
import com.dfq.coeffi.master.shift.ShiftController;
import com.dfq.coeffi.master.shift.ShiftService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

@RestController
@Slf4j
public class LossAnalysisResource extends BaseController {

    private final LossAnalysisService lossAnalysisService;
    private final SopCategoryService SOPCategoryService;
    private final LossCategoryService lossCategoryService;
    private final LossSubCategoryService lossSubCategoryService;
    private final ShiftService shiftService;
    private final ProductionLineMasterService productionLineMasterService;
    private final MachineMasterService machineMasterService;
    private final CurrentProductionRateService currentProductionRateService;
    private final ShiftController shiftController;
    private final LossAnalysisEntryTimeRuleService lossAnalysisEntryTimeRuleService;

    @Autowired
    public LossAnalysisResource(LossAnalysisService lossAnalysisService, SopCategoryService SOPCategoryService, LossCategoryService lossCategoryService, LossSubCategoryService lossSubCategoryService, ShiftService shiftService, ProductionLineMasterService productionLineMasterService, MachineMasterService machineMasterService, CurrentProductionRateService currentProductionRateService, ShiftController shiftController, LossAnalysisEntryTimeRuleService lossAnalysisEntryTimeRuleService) {
        this.lossAnalysisService = lossAnalysisService;
        this.SOPCategoryService = SOPCategoryService;
        this.lossCategoryService = lossCategoryService;
        this.lossSubCategoryService = lossSubCategoryService;
        this.shiftService = shiftService;
        this.productionLineMasterService = productionLineMasterService;
        this.machineMasterService = machineMasterService;
        this.currentProductionRateService = currentProductionRateService;
        this.shiftController = shiftController;
        this.lossAnalysisEntryTimeRuleService = lossAnalysisEntryTimeRuleService;
    }

    @PostMapping("/loss-analysis")
    private ResponseEntity<LossAnalysis> createLossAnalysis(@Valid @RequestBody LossAnalysis lossAnalysis) {
        Date today = new Date();
        ResponseEntity<Shift> shiftResponseEntity = shiftController.getCurrentShift();
        if (shiftResponseEntity == null) {
            throw new EntityNotFoundException("There is no running shift");
        }
        Shift currentShift = shiftResponseEntity.getBody();
        LossAnalysisEntryTimeRule lossAnalysisEntryTimeRule = new LossAnalysisEntryTimeRule();
        List<LossAnalysisEntryTimeRule> lossAnalysisEntryTimeRuleList = lossAnalysisEntryTimeRuleService.getAllLossAnalysisEntryTimeRule();
        if (lossAnalysisEntryTimeRuleList.isEmpty()) {
            lossAnalysisEntryTimeRule.setMinValue(10);
        } else {
            for (LossAnalysisEntryTimeRule lossAnalysisEntryTimeRuleObj : lossAnalysisEntryTimeRuleList) {
                lossAnalysisEntryTimeRule = lossAnalysisEntryTimeRuleObj;
            }
        }
        int addMin = Math.toIntExact(lossAnalysisEntryTimeRule.getMinValue());
        Date dNow = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(currentShift.getStartTime());
        cal.add(Calendar.MINUTE, addMin);
        dNow = cal.getTime();
        long currentShiftStartTime = dNow.getTime();

        LossAnalysis lossAnalysisObj = new LossAnalysis();
        Shift shift = shiftService.getShift(lossAnalysis.getShift().getId());
        List<LossAnalysis> lossAnalysisList = lossAnalysisService.getLossAnalysisByAssemblyLineByStage(lossAnalysis.getProductionLine(), lossAnalysis.getMachine());
        long startTime = 0;
        long endTime = 0;
        for (LossAnalysis oldLossAnalysis : lossAnalysisList) {
            if (oldLossAnalysis.getShift() == shift && oldLossAnalysis.getCreatedOn().getDate() == today.getDate()) {
                if (startTime == 0 || startTime > oldLossAnalysis.getFromTime().getTime()) {
                    startTime = oldLossAnalysis.getFromTime().getTime();
                }
                if (endTime == 0 || endTime < oldLossAnalysis.getToTime().getTime()) {
                    endTime = oldLossAnalysis.getToTime().getTime();
                }
            }
        }
        if (lossAnalysis.getFromTime().getTime() > lossAnalysis.getToTime().getTime()) {
            throw new EntityNotFoundException("From time is greater than to time.");
        } else if (shift.getId() == currentShift.getId()) {
            if (lossAnalysis.getFromTime().getTime() >= shift.getStartTime().getTime() && lossAnalysis.getToTime().getTime() <= shift.getEndTime().getTime()) {
                if (startTime != 0 && endTime != 0) {
                    if (lossAnalysis.getFromTime().getTime() < endTime) {
                        throw new EntityNotFoundException("Loss already present for this time range");
                    } else {
                        long lossTimeInMillies = Math.abs(lossAnalysis.getFromTime().getTime() - lossAnalysis.getToTime().getTime());
                        long lossTime = TimeUnit.MINUTES.convert(lossTimeInMillies, TimeUnit.MILLISECONDS);
                        lossAnalysis.setLossTime(lossTime);
                        lossAnalysis.setStatus(true);
                        //lossAnalysis.setCreatedOn(today);
                        lossAnalysisObj = lossAnalysisService.createLossAnalysis(lossAnalysis);
                    }
                } else {
                    long lossTimeInMillies = Math.abs(lossAnalysis.getFromTime().getTime() - lossAnalysis.getToTime().getTime());
                    long lossTime = TimeUnit.MINUTES.convert(lossTimeInMillies, TimeUnit.MILLISECONDS);
                    lossAnalysis.setLossTime(lossTime);
                    lossAnalysis.setStatus(true);
                    //lossAnalysis.setCreatedOn(today);
                    lossAnalysisObj = lossAnalysisService.createLossAnalysis(lossAnalysis);
                }
            } else {
                throw new EntityNotFoundException("Please enter time between current shift time range");
            }
        } else if (lossAnalysis.getFromTime().getTime() <= currentShiftStartTime) {
            if (lossAnalysis.getFromTime().getTime() >= shift.getStartTime().getTime() && lossAnalysis.getToTime().getTime() <= shift.getEndTime().getTime()) {
                if (startTime != 0 && endTime != 0) {
                    if (lossAnalysis.getFromTime().getTime() < endTime) {
                        throw new EntityNotFoundException("Loss already present for this time range");
                    } else {
                        long lossTimeInMillies = Math.abs(lossAnalysis.getFromTime().getTime() - lossAnalysis.getToTime().getTime());
                        long lossTime = TimeUnit.MINUTES.convert(lossTimeInMillies, TimeUnit.MILLISECONDS);
                        lossAnalysis.setLossTime(lossTime);
                        lossAnalysis.setStatus(true);
                        //lossAnalysis.setCreatedOn(today);
                        lossAnalysisObj = lossAnalysisService.createLossAnalysis(lossAnalysis);
                    }
                } else {
                    long lossTimeInMillies = Math.abs(lossAnalysis.getFromTime().getTime() - lossAnalysis.getToTime().getTime());
                    long lossTime = TimeUnit.MINUTES.convert(lossTimeInMillies, TimeUnit.MILLISECONDS);
                    lossAnalysis.setLossTime(lossTime);
                    lossAnalysis.setStatus(true);
                    //lossAnalysis.setCreatedOn(today);
                    lossAnalysisObj = lossAnalysisService.createLossAnalysis(lossAnalysis);
                }
            } else {
                throw new EntityNotFoundException("Please enter time between current shift time range");
            }
        } else {
            throw new EntityNotFoundException("Please enter loss for current shift.");
        }
        return new ResponseEntity<>(lossAnalysisObj, HttpStatus.CREATED);
    }

    @GetMapping("/loss-analysis")
    private ResponseEntity<LossAnalysis> getAllLossAnalysis() {
        List<LossAnalysis> lossAnalysisList = lossAnalysisService.getAllLossAnalysis();
        if (lossAnalysisList.isEmpty()) {
            throw new EntityNotFoundException("There is no Loss");
        }
        return new ResponseEntity(lossAnalysisList, HttpStatus.OK);
    }

    @GetMapping("/loss-analysis/{id}")
    private ResponseEntity<LossAnalysis> getLossAnalysisById(@PathVariable long id) {
        Optional<LossAnalysis> lossAnalysis = lossAnalysisService.getLossAnalysisById(id);
        return new ResponseEntity(lossAnalysis, HttpStatus.OK);
    }

    @GetMapping("/loss-analysis-by-productionLine-machine/{productionLineId}/{machineId}")
    private ResponseEntity<LossAnalysis> getLossAnalysisBySopTypeByDigitalSop(@PathVariable long productionLineId, @PathVariable long machineId) {
        Optional<ProductionLineMaster> productionLineMasterOptional = productionLineMasterService.getProductionLineMaster(productionLineId);
        Optional<MachineMaster> machineMaster = machineMasterService.getMachineMaster(machineId);
        List<LossAnalysis> lossAnalysisList = lossAnalysisService.getLossAnalysisByAssemblyLineByStage(productionLineMasterOptional.get(), machineMaster.get());
        if (lossAnalysisList.isEmpty()) {
            throw new EntityNotFoundException("There is no Loss Analysis.");
        }
        Collections.reverse(lossAnalysisList);
        return new ResponseEntity(lossAnalysisList, HttpStatus.OK);
    }

    @GetMapping("/loss-analysis-view/{productionLineId}/{machineId}/{lossCategoryId}/{lossSubCategoryId}")
    private ResponseEntity<LossAnalysis> getLossAnalysisView(@PathVariable long productionLineId, @PathVariable long machineId, @PathVariable long lossCategoryId, @PathVariable long lossSubCategoryId) {
        Date today = new Date();
        List<LossAnalysis> lossAnalysesList = new ArrayList<>();
        List<LossAnalysis> finalLossAnalysesList = new ArrayList<>();
        if (productionLineId == 0 && machineId == 0) {
            List<LossAnalysis> allLossAnalyses = lossAnalysisService.getAllLossAnalysis();
            if (lossCategoryId == 0 && lossSubCategoryId == 0) {
                for (LossAnalysis lossAnalysisObj : allLossAnalyses) {
                    lossAnalysesList.add(lossAnalysisObj);
                }
            } else if (lossCategoryId != 0 && lossSubCategoryId == 0) {
                for (LossAnalysis lossAnalysisObj : allLossAnalyses) {
                    if (lossAnalysisObj.getLossCategory().getId() == lossCategoryId) {
                        lossAnalysesList.add(lossAnalysisObj);
                    }
                }
            } else if (lossCategoryId != 0 && lossSubCategoryId != 0) {
                for (LossAnalysis lossAnalysisObj : allLossAnalyses) {
                    if (lossAnalysisObj.getLossCategory().getId() == lossCategoryId && lossAnalysisObj.getLossSubCategory().getId() == lossSubCategoryId) {
                        lossAnalysesList.add(lossAnalysisObj);
                    }
                }
            }

        } else if (productionLineId != 0 && machineId == 0) {
            Optional<ProductionLineMaster> productionLineMasterOptional = productionLineMasterService.getProductionLineMaster(productionLineId);
            List<LossAnalysis> allLossAnalyses = lossAnalysisService.getLossAnalysisByAssemblyLine(productionLineMasterOptional.get());
            if (lossCategoryId == 0 && lossSubCategoryId == 0) {
                for (LossAnalysis lossAnalysisObj : allLossAnalyses) {
                    lossAnalysesList.add(lossAnalysisObj);
                }
            } else if (lossCategoryId != 0 && lossSubCategoryId == 0) {
                for (LossAnalysis lossAnalysisObj : allLossAnalyses) {
                    if (lossAnalysisObj.getLossCategory().getId() == lossCategoryId) {
                        lossAnalysesList.add(lossAnalysisObj);
                    }
                }
            } else if (lossCategoryId != 0 && lossSubCategoryId != 0) {
                for (LossAnalysis lossAnalysisObj : allLossAnalyses) {
                    if (lossAnalysisObj.getLossCategory().getId() == lossCategoryId && lossAnalysisObj.getLossSubCategory().getId() == lossSubCategoryId) {
                        lossAnalysesList.add(lossAnalysisObj);
                    }
                }
            }

        } else if (productionLineId != 0 && machineId != 0) {
            Optional<ProductionLineMaster> productionLineMasterOptional = productionLineMasterService.getProductionLineMaster(productionLineId);
            Optional<MachineMaster> machineMaster = machineMasterService.getMachineMaster(machineId);
            List<LossAnalysis> lossAnalysis = lossAnalysisService.getLossAnalysisByAssemblyLineByStage(productionLineMasterOptional.get(), machineMaster.get());
            if (lossCategoryId == 0 && lossSubCategoryId == 0) {
                for (LossAnalysis lossAnalysisObj : lossAnalysis) {
                    lossAnalysesList.add(lossAnalysisObj);
                }
            } else if (lossCategoryId != 0 && lossSubCategoryId == 0) {
                for (LossAnalysis lossAnalysisObj : lossAnalysis) {
                    if (lossAnalysisObj.getLossCategory().getId() == lossCategoryId) {
                        lossAnalysesList.add(lossAnalysisObj);
                    }
                }
            } else if (lossCategoryId != 0 && lossSubCategoryId != 0) {
                for (LossAnalysis lossAnalysisObj : lossAnalysis) {
                    if (lossAnalysisObj.getLossCategory().getId() == lossCategoryId && lossAnalysisObj.getLossSubCategory().getId() == lossSubCategoryId) {
                        lossAnalysesList.add(lossAnalysisObj);
                    }
                }
            }
        }
        for (LossAnalysis lossAnalysisObj : lossAnalysesList) {
            if (lossAnalysisObj.getCreatedOn().equals(today)) {
                finalLossAnalysesList.add(lossAnalysisObj);
            }
        }
        if (lossAnalysesList.isEmpty()) {
            throw new EntityNotFoundException("There is no Loss Analysis");
        }
        return new ResponseEntity(lossAnalysesList, HttpStatus.OK);
    }

    @GetMapping("/loss-analysis-current-day")
    private ResponseEntity<LossAnalysis> getLossAnalysisCurrentDay() {
        Date toDay = new Date();
        List<LossAnalysis> lossAnalyses = new ArrayList<>();
        List<LossAnalysis> lossAnalysisList = lossAnalysisService.getAllLossAnalysis();
        for (LossAnalysis lossAnalysisObj : lossAnalysisList) {
            if (lossAnalysisObj.getCreatedOn().getDate() == toDay.getDate()) {
                lossAnalyses.add(lossAnalysisObj);
            }
        }
        if (lossAnalyses.isEmpty()) {
            throw new EntityNotFoundException("There is no loss for today");
        }
        Collections.reverse(lossAnalyses);
        return new ResponseEntity(lossAnalyses, HttpStatus.OK);
    }

    @PostMapping("/loss-analysis-by-shift-date")
    private ResponseEntity<LossAnalysis> getLossAnalysisCurrentMonth(@Valid @RequestBody LossAnalysis lossAnalysis) throws ParseException {
        long totalRunTime = 0;
        long totalLossTime = 0;
        long totalTime = 0;
        Shift shift = shiftService.getShift(lossAnalysis.getShift().getId());

        long totalTimeInMillies = Math.abs(shift.getStartTime().getTime() - shift.getEndTime().getTime());
        totalTime = TimeUnit.MINUTES.convert(totalTimeInMillies, TimeUnit.MILLISECONDS);

        List<LossCategory> lossCategoryList = lossCategoryService.getAllLossCategory();
        List<LossAnalysis> lossAnalysisList = lossAnalysisService.getLossAnalysisByShift(shift);

        LossAnalysisDto lossAnalysisDto = new LossAnalysisDto();
        List<LossCategoryDto> lossCategoryDtos = new ArrayList<>();
        for (LossCategory lossCategoryObj : lossCategoryList) {
            long totalCategoryLossTime = 0;
            LossCategoryDto lossCategoryDto = new LossCategoryDto();
            List<LossSubCategoryDto> lossSubCategoryDtos = new ArrayList<>();
            for (LossAnalysis lossAnalysisObj : lossAnalysisList) {
                if (lossAnalysisObj.getCreatedOn().getDate() == lossAnalysis.getCreatedOn().getDate()) {
                    if (lossAnalysisObj.getLossCategory().equals(lossCategoryObj)) {

                        long diffInMillies = Math.abs(shift.getStartTime().getTime() - shift.getEndTime().getTime());
                        totalTime = TimeUnit.MINUTES.convert(diffInMillies, TimeUnit.MILLISECONDS);

                        totalLossTime = totalLossTime + lossAnalysisObj.getLossTime();
                        totalCategoryLossTime = totalCategoryLossTime + lossAnalysisObj.getLossTime();
                        lossCategoryDto.setLossCategory(lossCategoryObj);
                        lossCategoryDto.setTotalCategoryLossTime(totalCategoryLossTime);
                        List<LossSubCategory> lossSubCategoryList = lossSubCategoryService.getLossSubCategoryByLosscategory(lossCategoryObj);
                        for (LossSubCategory lossSubCategoryObj : lossSubCategoryList) {
                            if (lossAnalysisObj.getLossSubCategory().equals(lossSubCategoryObj)) {
                                LossSubCategoryDto lossSubCategoryDto = new LossSubCategoryDto();
                                lossSubCategoryDto.setLossSubCategory(lossSubCategoryObj);
                                lossSubCategoryDto.setTotalSubCategoryLossTime(lossAnalysisObj.getLossTime());
                                lossSubCategoryDtos.add(lossSubCategoryDto);
                            }
                        }
                    }
                }
            }
            if (lossCategoryDto.getLossCategory() != null) {
                lossCategoryDto.setLossSubCategoryDtos(lossSubCategoryDtos);
                lossCategoryDtos.add(lossCategoryDto);
            }
        }
        totalRunTime = totalTime - totalLossTime;
        lossAnalysisDto.setTotalTime(totalTime);
        lossAnalysisDto.setTotalRunTime(totalRunTime);
        lossAnalysisDto.setTotalLossTime(totalLossTime);
        lossAnalysisDto.setLossCategoryDtos(lossCategoryDtos);

        return new ResponseEntity(lossAnalysisDto, HttpStatus.OK);
    }

    @PostMapping("/loss-analysis-by-shift-date-for-total-time")
    private ResponseEntity<LossAnalysis> getLossAnalysisCurrentMonthForReport1(@Valid @RequestBody LossAnalysis lossAnalysis) throws ParseException {
        Date toDay = new Date();
        if (lossAnalysis.getCreatedOn() == null) {
            lossAnalysis.setCreatedOn(toDay);
        }
        long totalRunTime = 0;
        long totalLossTime = 0;
        long totalTime = 0;
        Shift shift = shiftService.getShift(lossAnalysis.getShift().getId());
        long totalTimeInMillies = Math.abs(shift.getStartTime().getTime() - shift.getEndTime().getTime());
        totalTime = TimeUnit.MINUTES.convert(totalTimeInMillies, TimeUnit.MILLISECONDS);
        List<LossAnalysis> lossAnalysisList = lossAnalysisService.getLossAnalysisByShift(shift);
        for (LossAnalysis lossAnalysisObj : lossAnalysisList) {
            if (lossAnalysisObj.getCreatedOn().getDate() == lossAnalysis.getCreatedOn().getDate()) {
                totalLossTime = totalLossTime + lossAnalysisObj.getLossTime();
            }
        }
        totalRunTime = totalTime - totalLossTime;
        LossAnalysisDto lossAnalysisDto = new LossAnalysisDto();
        lossAnalysisDto.setTotalTime(totalTime);
        lossAnalysisDto.setTotalRunTime(totalRunTime);
        lossAnalysisDto.setTotalLossTime(totalLossTime);
        return new ResponseEntity(lossAnalysisDto, HttpStatus.OK);
    }

    @PostMapping("/loss-analysis-by-shift-date-for-category-loss-time")
    private ResponseEntity<LossAnalysis> getLossAnalysisCurrentMonthForCategoryLossTime(@Valid @RequestBody LossAnalysis lossAnalysis) throws ParseException {
        Date toDay = new Date();
        if (lossAnalysis.getCreatedOn() == null) {
            lossAnalysis.setCreatedOn(toDay);
        }
        /*Date lossdate = mySqlFormatDate(lossAnalysis.getCreatedOn());
        System.out.println("************************************"+lossdate);*/
        List<LossCategory> lossCategoryList = lossCategoryService.getAllLossCategory();
        if (lossCategoryList.size() <= 0 || lossCategoryList == null) {
            throw new EntityNotFoundException("There is No Loss Category.");
        }
        Shift shiftObj = shiftService.getShift(lossAnalysis.getShift().getId());

        List<LossAnalysis> lossAnalysisProductionList = new ArrayList<>();
        List<LossAnalysis> lossAnalysisList = lossAnalysisService.getLossAnalysisByShift(shiftObj);
        System.out.println("*****************************"+lossAnalysisList.size());

        for (LossAnalysis lossAnalysisObj: lossAnalysisList){
            if (lossAnalysisObj.getProductionLine().getId() == lossAnalysis.getProductionLine().getId()) {
                lossAnalysisProductionList.add(lossAnalysisObj);
            }
        }

        //List<LossAnalysis> lossAnalysisList = lossAnalysisService.getLossAnalysisByShiftByDateByProductionLine(lossAnalysis.getCreatedOn(), lossAnalysis.getShift().getId(), lossAnalysis.getProductionLine().getId());
        if (lossAnalysisProductionList.isEmpty()){
            throw new EntityNotFoundException("There is no Loss Entry.");
        }
        List<LossCategoryDto> lossCategoryDtos = new ArrayList<>();
        for (LossCategory lossCategoryObj : lossCategoryList) {
            long totalCategoryTime = 0;
            LossCategoryDto lossCategoryDto = new LossCategoryDto();
            for (LossAnalysis lossAnalysisObj : lossAnalysisProductionList) {
                if (lossAnalysisObj.getCreatedOn().getDate() == lossAnalysis.getCreatedOn().getDate()) {
                    if (lossAnalysisObj.getLossCategory().equals(lossCategoryObj)) {
                        totalCategoryTime = totalCategoryTime + lossAnalysisObj.getLossTime();
                    }
                }
            }
            if (totalCategoryTime > 0) {
                lossCategoryDto.setLossCategory(lossCategoryObj);
                lossCategoryDto.setTotalCategoryLossTime(totalCategoryTime);
                lossCategoryDtos.add(lossCategoryDto);
            }
        }
        if (lossCategoryDtos.isEmpty()){
            throw new EntityNotFoundException("There is no losses.");
        }
        return new ResponseEntity(lossCategoryDtos, HttpStatus.OK);
    }

    @PostMapping("/loss-analysis-by-shift-date-for-subcategory-loss-time")
    private ResponseEntity<LossAnalysis> getLossAnalysisCurrentMonthForSubCategoryLossTime(@Valid @RequestBody LossAnalysis lossAnalysis) throws ParseException {
        Shift shift = shiftService.getShift(lossAnalysis.getShift().getId());
        List<LossAnalysis> lossAnalysisList = lossAnalysisService.getLossAnalysisByShift(shift);
        Optional<LossCategory> lossCategoryOptional = lossCategoryService.getLossCategory(lossAnalysis.getLossCategory().getId());
        LossCategory lossCategory = lossCategoryOptional.get();
        List<LossSubCategory> lossSubCategoryList = lossSubCategoryService.getLossSubCategoryByLosscategory(lossCategory);
        List<LossSubCategoryDto> lossSubCategoryDtos = new ArrayList<>();
        for (LossSubCategory lossSubCategoryObj : lossSubCategoryList) {
            long totalSubCategoryTime = 0;
            LossSubCategoryDto lossSubCategoryDto = new LossSubCategoryDto();
            for (LossAnalysis lossAnalysisObj : lossAnalysisList) {
                if (lossAnalysisObj.getCreatedOn().getDate() == lossAnalysis.getCreatedOn().getDate()) {
                    if (lossAnalysisObj.getLossSubCategory().equals(lossSubCategoryObj)) {
                        totalSubCategoryTime = totalSubCategoryTime + lossAnalysisObj.getLossTime();
                        lossSubCategoryDto.setLossSubCategory(lossSubCategoryObj);
                    }
                }
            }
            if (lossSubCategoryDto.getLossSubCategory() != null) {
                lossSubCategoryDto.setTotalSubCategoryLossTime(totalSubCategoryTime);
                lossSubCategoryDtos.add(lossSubCategoryDto);
            }
        }
        return new ResponseEntity(lossSubCategoryDtos, HttpStatus.OK);
    }

    @PostMapping("/loss-analysis-by-shift-date-for-total-time-by-productionLine/{productionLineId}")
    private ResponseEntity<LossAnalysis> getLossAnalysisCurrentMonthForReport1(@Valid @RequestBody LossAnalysis lossAnalysis, @PathVariable long productionLineId) throws ParseException {
        Date toDay = new Date();
        if (lossAnalysis.getCreatedOn() == null) {
            lossAnalysis.setCreatedOn(toDay);
        }
        long totalRunTime = 0;
        long totalLossTime = 0;
        long totalTime = 0;
        Shift shift = shiftService.getShift(lossAnalysis.getShift().getId());
        long totalTimeInMillies = Math.abs(shift.getEndTime().getTime() - shift.getStartTime().getTime());
        totalTime = TimeUnit.MINUTES.convert(totalTimeInMillies, TimeUnit.MILLISECONDS);
        List<LossAnalysis> lossAnalysisList = lossAnalysisService.getLossAnalysisByShift(shift);
        for (LossAnalysis lossAnalysisObj : lossAnalysisList) {
            if (lossAnalysisObj.getProductionLine().getId() == productionLineId) {
                if (lossAnalysisObj.getCreatedOn().getDate() == lossAnalysis.getCreatedOn().getDate()) {
                    totalLossTime = totalLossTime + lossAnalysisObj.getLossTime();
                }
            }
        }

        long productionRate = 0;
        Optional<ProductionLineMaster> productionLineMasterOptional = productionLineMasterService.getProductionLineMaster(productionLineId);
        ProductionLineMaster productionLineMaster = productionLineMasterOptional.get();
        List<CurrentProductionRate> currentProductionRateList = currentProductionRateService.getCurrentProductionRateByProduction(productionLineMaster);
        for (CurrentProductionRate currentProductionRateObj : currentProductionRateList) {
            if (currentProductionRateObj.getCreatedOn().getDate() == toDay.getDate()) {
                productionRate = currentProductionRateObj.getProducationRate();
            }
        }

        totalRunTime = totalTime - totalLossTime;

        if (productionRate == 0) {
            productionRate = productionLineMaster.getDefaultProductionRate();
        }

        long totalProduction = totalRunTime / productionRate;
        LossAnalysisDto lossAnalysisDto = new LossAnalysisDto();
        lossAnalysisDto.setTotalTime(totalTime);
        lossAnalysisDto.setTotalRunTime(totalRunTime);
        lossAnalysisDto.setTotalLossTime(totalLossTime);
        lossAnalysisDto.setTotalProduction(totalProduction);
        return new ResponseEntity(lossAnalysisDto, HttpStatus.OK);
    }

    @PostMapping("/loss-analysis-by-shift-date-for-total-time-by-date-shift-productionLine")
    private ResponseEntity<LossAnalysis> getLossAnalysisByDateByShiftByProductionLine(@Valid @RequestBody LossAnalysis lossAnalysis) {
        List<LossAnalysis> lossAnalyses = lossAnalysisService.getLossAnalysisByShiftByDateByProductionLine(lossAnalysis.getCreatedOn(), lossAnalysis.getShift().getId(), lossAnalysis.getProductionLine().getId());
        if (lossAnalyses.isEmpty()) {
            throw new EntityNotFoundException("There is no loss entry.");
        }
        Collections.reverse(lossAnalyses);
        return new ResponseEntity(lossAnalyses, HttpStatus.OK);
    }

    public Date mySqlFormatDate(Date date1) {
        Date date = date1;
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        try {
            date = df.parse(df.format(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        System.out.println("*********************************date******"+date);
        return date;
    }
}