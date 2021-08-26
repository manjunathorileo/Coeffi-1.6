package com.dfq.coeffi.LossAnalysis.dashboardDetails;

import com.dfq.coeffi.LossAnalysis.LossAnalysis;
import com.dfq.coeffi.LossAnalysis.LossAnalysisService;
import com.dfq.coeffi.LossAnalysis.LossCategory.LossCategory;
import com.dfq.coeffi.LossAnalysis.LossCategory.LossCategoryService;
import com.dfq.coeffi.LossAnalysis.machine.MachineMasterService;
import com.dfq.coeffi.LossAnalysis.productionLine.ProductionLineMaster;
import com.dfq.coeffi.LossAnalysis.productionLine.ProductionLineMasterService;
import com.dfq.coeffi.controller.BaseController;
import com.dfq.coeffi.master.shift.Shift;
import com.dfq.coeffi.master.shift.ShiftService;
import com.dfq.coeffi.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;

@RestController
@Slf4j
public class LossAnalysisDashboard extends BaseController {

    private final ShiftService shiftService;
    private final ProductionLineMasterService productionLineMasterService;
    private final MachineMasterService machineMasterService;
    private final LossAnalysisService lossAnalysisService;
    private final LossCategoryService lossCategoryService;

    @Autowired
    public LossAnalysisDashboard(ShiftService shiftService, ProductionLineMasterService productionLineMasterService, MachineMasterService machineMasterService, LossAnalysisService lossAnalysisService, LossCategoryService lossCategoryService) {
        this.shiftService = shiftService;
        this.productionLineMasterService = productionLineMasterService;
        this.machineMasterService = machineMasterService;
        this.lossAnalysisService = lossAnalysisService;
        this.lossCategoryService = lossCategoryService;
    }

    private long topProductionLossTime = 0;

    @PostMapping("/loss-analysis-dashboard-main-time")
    private ResponseEntity<LossAnalysisDashboadDto> getDetailsLossAnalysis(@Valid @RequestBody LossAnalysisDashboadRequiredDto lossAnalysisDashboadRequiredDto) {
        LossAnalysisDashboadDto lossAnalysisDashboadDto = new LossAnalysisDashboadDto();
        Date fromDate = lossAnalysisDashboadRequiredDto.getFromDate();
        Date toDate = lossAnalysisDashboadRequiredDto.getToDate();
        Shift shift = shiftService.getShift(lossAnalysisDashboadRequiredDto.getShiftId());
        List<ProductionLineMaster> productionLineMasters = productionLineMasterService.getAllProductionLineMaster();
        List<LossCategory> lossCategories = lossCategoryService.getAllLossCategory();
        long totalTime = getTotalTime(lossAnalysisDashboadRequiredDto);
        long totalProductionLossTime = 0;
        List<TopProductionLossDetailsDto> totalProductionLossDetailsList = new ArrayList<>();
        List<TotalCategoryLossDetailsDto> totalCategoryLossDetailsDtoList = new ArrayList<>();
        for (ProductionLineMaster productionLineMaster : productionLineMasters) {
            TopProductionLossDetailsDto totalProductionLossDetails = new TopProductionLossDetailsDto();
            long productionLineLossTime = 0;
            List<LossAnalysis> lossAnalysesProductioWise = lossAnalysisService.getLossAnalysisByShiftByFromDateByToDateByProductionLine(fromDate, toDate, shift.getId(), productionLineMaster.getId());
            for (LossAnalysis lossAnalysis : lossAnalysesProductioWise) {
                productionLineLossTime = productionLineLossTime + lossAnalysis.getLossTime();
                totalProductionLossTime = totalProductionLossTime + lossAnalysis.getLossTime();
            }
            totalProductionLossDetails.setProductionId(productionLineMaster.getId());
            totalProductionLossDetails.setProductionLineName(productionLineMaster.getLineName());
            totalProductionLossDetails.setTotalTimeInMin(productionLineLossTime);
            totalProductionLossDetailsList.add(totalProductionLossDetails);

            for (LossCategory lossCategory : lossCategories) {
                TotalCategoryLossDetailsDto totalCategoryLossDetailsDto = new TotalCategoryLossDetailsDto();
                long totalCategoryLossTime = 0;
                List<LossAnalysis> lossAnalysisCategoryWise = lossAnalysisService.getLossAnalysisByFromDateByToDateByShiftByProductionLineByLossCategory(fromDate, toDate, shift.getId(), productionLineMaster.getId(), lossCategory.getId());
                for (LossAnalysis lossAnalysis : lossAnalysisCategoryWise) {
                    totalCategoryLossTime = totalCategoryLossTime + lossAnalysis.getLossTime();
                }
                totalCategoryLossDetailsDto.setProductionLineId(productionLineMaster.getId());
                totalCategoryLossDetailsDto.setCategoryId(lossCategory.getId());
                totalCategoryLossDetailsDto.setLossCategory(lossCategory.getLossCategory());
                totalCategoryLossDetailsDto.setTotalTimeInMin(totalCategoryLossTime);
                totalCategoryLossDetailsDtoList.add(totalCategoryLossDetailsDto);
            }
        }
        totalProductionLossDetailsList.sort((TopProductionLossDetailsDto P1, TopProductionLossDetailsDto P2) -> Math.toIntExact(P2.getTotalTimeInMin() - P1.getTotalTimeInMin()));
        totalCategoryLossDetailsDtoList.sort((TotalCategoryLossDetailsDto C1, TotalCategoryLossDetailsDto C2) -> Math.toIntExact(C2.getTotalTimeInMin() - C1.getTotalTimeInMin()));

        List<TopProductionLossDetailsDto> donutReportProductionDetails = getDonutReportProductionDetails(totalProductionLossDetailsList, totalCategoryLossDetailsDtoList);
        List<TotalProductionLossDetailsDto> totalProductionLossDetailsDtos = getTotalProductionLossDetailsDto(totalProductionLossDetailsList, totalCategoryLossDetailsDtoList, totalTime);

        lossAnalysisDashboadDto.setTotalTime(totalTime);
        lossAnalysisDashboadDto.setTotalProductionLossTime(totalProductionLossTime);
        lossAnalysisDashboadDto.setTopProductionLossTime(topProductionLossTime);
        lossAnalysisDashboadDto.setTopProductionLossDetailDtos(donutReportProductionDetails);
        lossAnalysisDashboadDto.setTotalProductionLossDetailsDtos(totalProductionLossDetailsDtos);
        return new ResponseEntity(lossAnalysisDashboadDto, HttpStatus.OK);
    }

    public long getTotalTime(LossAnalysisDashboadRequiredDto lossAnalysisDashboadRequiredDto) {
        Date fromDate = lossAnalysisDashboadRequiredDto.getFromDate();
        Date toDate = lossAnalysisDashboadRequiredDto.getToDate();
        long dayDiff = toDate.getTime() - fromDate.getTime();
        long daysBetween = (dayDiff / (1000 * 60 * 60 * 24)) + 1;
        Shift shift = shiftService.getShift(lossAnalysisDashboadRequiredDto.getShiftId());
        long shiftHr = DateUtil.getRunningHour(shift.getEndTime()) - DateUtil.getRunningHour(shift.getStartTime());
        long totalTime = shiftHr * daysBetween;
        return totalTime;
    }

    public List<TopProductionLossDetailsDto> getDonutReportProductionDetails(List<TopProductionLossDetailsDto> totalProductionLossDetailsList, List<TotalCategoryLossDetailsDto> totalCategoryLossDetailDtos) {
        int pCount = 0;
        topProductionLossTime = 0;
        long otherProductionLossTime = 0;
        List<TopProductionLossDetailsDto> topTotalProductionLossDetails = new ArrayList<>();
        for (TopProductionLossDetailsDto totalProductionLossDetails : totalProductionLossDetailsList) {
            TopProductionLossDetailsDto totalProductionLossDetailsObj = new TopProductionLossDetailsDto();
            if (totalProductionLossDetails.getTotalTimeInMin() > 0) {
                if (pCount == 5) {
                    otherProductionLossTime = otherProductionLossTime + totalProductionLossDetails.getTotalTimeInMin();
                } else {
                    List<TotalCategoryLossDetailsDto> totalCategoryLossDetailsDtoList = getTopProductionTotalCategoryLossDetails(totalProductionLossDetails.getProductionId(), totalCategoryLossDetailDtos);
                    totalProductionLossDetailsObj.setProductionId(totalProductionLossDetails.getProductionId());
                    totalProductionLossDetailsObj.setProductionLineName(totalProductionLossDetails.getProductionLineName());
                    totalProductionLossDetailsObj.setTotalTimeInMin(totalProductionLossDetails.getTotalTimeInMin());
                    totalProductionLossDetailsObj.setTotalCategoryLossDetailsDtoList(totalCategoryLossDetailsDtoList);
                    topTotalProductionLossDetails.add(totalProductionLossDetailsObj);
                    topProductionLossTime = topProductionLossTime + totalProductionLossDetails.getTotalTimeInMin();
                    pCount++;
                }
            }
        }
        TopProductionLossDetailsDto otherProductionLossDetails = new TopProductionLossDetailsDto();
        List<TotalCategoryLossDetailsDto> totalCategoryLossDetailsDtoList = new ArrayList<>();
        otherProductionLossDetails.setProductionId(0);
        otherProductionLossDetails.setProductionLineName("Other Production Line");
        otherProductionLossDetails.setTotalTimeInMin(otherProductionLossTime);
        otherProductionLossDetails.setTotalCategoryLossDetailsDtoList(totalCategoryLossDetailsDtoList);
        topTotalProductionLossDetails.add(otherProductionLossDetails);
        return topTotalProductionLossDetails;
    }

    public List<TotalCategoryLossDetailsDto> getTopProductionTotalCategoryLossDetails(long productionLineId, List<TotalCategoryLossDetailsDto> totalCategoryLossDetailDtos) {
        long cCount = 0;
        List<TotalCategoryLossDetailsDto> totalCategoryLossDetailsDtoList = new ArrayList<>();
        for (TotalCategoryLossDetailsDto totalCategoryLossDetailsDtoObj : totalCategoryLossDetailDtos) {
            if (cCount == 5) {
                break;
            }
            if (productionLineId == totalCategoryLossDetailsDtoObj.getProductionLineId() && totalCategoryLossDetailsDtoObj.getTotalTimeInMin() > 0) {
                totalCategoryLossDetailsDtoList.add(totalCategoryLossDetailsDtoObj);
                cCount++;
            }
        }
        return totalCategoryLossDetailsDtoList;
    }

    public List<TotalProductionLossDetailsDto> getTotalProductionLossDetailsDto(List<TopProductionLossDetailsDto> totalProductionLossDetailsList, List<TotalCategoryLossDetailsDto> totalCategoryLossDetailDtos, long totalTime){
        long totalTimeInMin = totalTime * 60;
        List<TotalProductionLossDetailsDto> totalProductionLossDetailsDtos = new ArrayList<>();
        for (TopProductionLossDetailsDto topProductionLossDetailsDto:totalProductionLossDetailsList) {
            if (topProductionLossDetailsDto.getTotalTimeInMin() > 0) {
                TotalProductionLossDetailsDto totalProductionLossDetailsDto = new TotalProductionLossDetailsDto();
                long operationTime = totalTimeInMin - topProductionLossDetailsDto.getTotalTimeInMin();
                totalProductionLossDetailsDto.setProductionLineId(topProductionLossDetailsDto.getProductionId());
                totalProductionLossDetailsDto.setProductionLineName(topProductionLossDetailsDto.getProductionLineName());
                totalProductionLossDetailsDto.setTotalLossTime(topProductionLossDetailsDto.getTotalTimeInMin());
                totalProductionLossDetailsDto.setCurrentTime(totalTimeInMin);
                totalProductionLossDetailsDto.setOpertationTime(operationTime);
                long cCount = 0;
                List<TotalCategoryLossDetailsDto> totalCategoryLossDetailsDtos = new ArrayList<>();
                for (TotalCategoryLossDetailsDto totalCategoryLossDetailsDto : totalCategoryLossDetailDtos) {
                    if (cCount == 5) {
                        break;
                    }
                    if (topProductionLossDetailsDto.getProductionId() == totalCategoryLossDetailsDto.getProductionLineId()) {
                        totalCategoryLossDetailsDtos.add(totalCategoryLossDetailsDto);
                        cCount++;
                    }
                }
                totalProductionLossDetailsDto.setTotalCategoryLossDetailsDtos(totalCategoryLossDetailsDtos);
                totalProductionLossDetailsDtos.add(totalProductionLossDetailsDto);
            }
        }
        return totalProductionLossDetailsDtos;
    }
}