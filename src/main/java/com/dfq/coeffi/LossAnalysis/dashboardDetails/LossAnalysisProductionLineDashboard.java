package com.dfq.coeffi.LossAnalysis.dashboardDetails;

import com.dfq.coeffi.LossAnalysis.LossAnalysis;
import com.dfq.coeffi.LossAnalysis.LossAnalysisService;
import com.dfq.coeffi.LossAnalysis.LossCategory.LossCategory;
import com.dfq.coeffi.LossAnalysis.LossCategory.LossCategoryService;
import com.dfq.coeffi.LossAnalysis.machine.MachineMasterService;
import com.dfq.coeffi.LossAnalysis.oeeReport.*;
import com.dfq.coeffi.LossAnalysis.productionLine.ProductionLineMaster;
import com.dfq.coeffi.LossAnalysis.productionLine.ProductionLineMasterService;
import com.dfq.coeffi.controller.BaseController;
import com.dfq.coeffi.master.shift.Shift;
import com.dfq.coeffi.master.shift.ShiftService;
import com.dfq.coeffi.util.DateUtil;
import jxl.format.VerticalAlignment;
import jxl.write.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static jxl.format.Alignment.CENTRE;

@RestController
@Slf4j
public class LossAnalysisProductionLineDashboard extends BaseController {

    private final ShiftService shiftService;
    private final ProductionLineMasterService productionLineMasterService;
    private final MachineMasterService machineMasterService;
    private final LossAnalysisService lossAnalysisService;
    private final LossCategoryService lossCategoryService;

    @Autowired
    public LossAnalysisProductionLineDashboard(ShiftService shiftService, ProductionLineMasterService productionLineMasterService, MachineMasterService machineMasterService, LossAnalysisService lossAnalysisService, LossCategoryService lossCategoryService) {
        this.shiftService = shiftService;
        this.productionLineMasterService = productionLineMasterService;
        this.machineMasterService = machineMasterService;
        this.lossAnalysisService = lossAnalysisService;
        this.lossCategoryService = lossCategoryService;
    }

    private long topProductionLossTime = 0;

    @PostMapping("/loss-analysis-dashboard-production-loss-time")
    private ResponseEntity<LossAnalysisDashboadDto> getDetailsLossAnalysis(@Valid @RequestBody LossAnalysisDashboadRequiredDto lossAnalysisDashboadRequiredDto) {
        LossAnalysisDashboadDto lossAnalysisDashboadDto = getLossAnalysisDashboadDto(lossAnalysisDashboadRequiredDto);
        return new ResponseEntity(lossAnalysisDashboadDto, HttpStatus.OK);
    }

    private LossAnalysisDashboadDto getLossAnalysisDashboadDto(LossAnalysisDashboadRequiredDto lossAnalysisDashboadRequiredDto) {
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
        return lossAnalysisDashboadDto;
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
                    totalProductionLossDetailsObj.setProductionId(totalProductionLossDetails.getProductionId());
                    totalProductionLossDetailsObj.setProductionLineName(totalProductionLossDetails.getProductionLineName());
                    totalProductionLossDetailsObj.setTotalTimeInMin(totalProductionLossDetails.getTotalTimeInMin());
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

    public List<TotalProductionLossDetailsDto> getTotalProductionLossDetailsDto(List<TopProductionLossDetailsDto> totalProductionLossDetailsList, List<TotalCategoryLossDetailsDto> totalCategoryLossDetailDtos, long totalTime) {
        long totalTimeInMin = totalTime * 60;
        List<TotalProductionLossDetailsDto> totalProductionLossDetailsDtos = new ArrayList<>();
        for (TopProductionLossDetailsDto topProductionLossDetailsDto : totalProductionLossDetailsList) {
            if (topProductionLossDetailsDto.getTotalTimeInMin() > 0) {
                TotalProductionLossDetailsDto totalProductionLossDetailsDto = new TotalProductionLossDetailsDto();
                long operationTime = totalTimeInMin - topProductionLossDetailsDto.getTotalTimeInMin();
                totalProductionLossDetailsDto.setProductionLineId(topProductionLossDetailsDto.getProductionId());
                totalProductionLossDetailsDto.setProductionLineName(topProductionLossDetailsDto.getProductionLineName());
                totalProductionLossDetailsDto.setTotalLossTime(topProductionLossDetailsDto.getTotalTimeInMin());
                totalProductionLossDetailsDto.setCurrentTime(totalTimeInMin);
                totalProductionLossDetailsDto.setOpertationTime(operationTime);
                List<TotalCategoryLossDetailsDto> totalCategoryLossDetailsDtos = new ArrayList<>();
                for (TotalCategoryLossDetailsDto totalCategoryLossDetailsDto : totalCategoryLossDetailDtos) {
                    if (topProductionLossDetailsDto.getProductionId() == totalCategoryLossDetailsDto.getProductionLineId()) {
                        totalCategoryLossDetailsDtos.add(totalCategoryLossDetailsDto);
                    }
                }
                totalProductionLossDetailsDto.setTotalCategoryLossDetailsDtos(totalCategoryLossDetailsDtos);
                totalProductionLossDetailsDtos.add(totalProductionLossDetailsDto);
            }
        }
        return totalProductionLossDetailsDtos;
    }

    @PostMapping("/loss-analysis-dashboard-production-category-loss-time")
    public ResponseEntity<List<TotalCategoryLossDetailsDto>> getTopProductionTotalCategoryLossDetails(@RequestBody @Valid LossAnalysisDashboadRequiredDto lossAnalysisDashboadRequiredDto) {
        Date fromDate = lossAnalysisDashboadRequiredDto.getFromDate();
        Date toDate = lossAnalysisDashboadRequiredDto.getToDate();
        Shift shift = shiftService.getShift(lossAnalysisDashboadRequiredDto.getShiftId());
        List<LossCategory> lossCategories = lossCategoryService.getAllLossCategory();
        List<TotalCategoryLossDetailsDto> totalCategoryLossDetailsDtoList = new ArrayList<>();
        for (LossCategory lossCategory : lossCategories) {
            TotalCategoryLossDetailsDto totalCategoryLossDetailsDto = new TotalCategoryLossDetailsDto();
            long totalCategoryLossTime = 0;
            List<LossAnalysis> lossAnalysisCategoryWise = lossAnalysisService.getLossAnalysisByFromDateByToDateByShiftByProductionLineByLossCategory(fromDate, toDate, shift.getId(), lossAnalysisDashboadRequiredDto.getProductionLineId(), lossCategory.getId());
            for (LossAnalysis lossAnalysis : lossAnalysisCategoryWise) {
                totalCategoryLossTime = totalCategoryLossTime + lossAnalysis.getLossTime();
            }
            totalCategoryLossDetailsDto.setProductionLineId(lossAnalysisDashboadRequiredDto.getProductionLineId());
            totalCategoryLossDetailsDto.setCategoryId(lossCategory.getId());
            totalCategoryLossDetailsDto.setLossCategory(lossCategory.getLossCategory());
            totalCategoryLossDetailsDto.setTotalTimeInMin(totalCategoryLossTime);
            totalCategoryLossDetailsDtoList.add(totalCategoryLossDetailsDto);
        }
        totalCategoryLossDetailsDtoList.sort((TotalCategoryLossDetailsDto C1, TotalCategoryLossDetailsDto C2) -> Math.toIntExact(C2.getTotalTimeInMin() - C1.getTotalTimeInMin()));
        long cCount = 0;
        List<TotalCategoryLossDetailsDto> totalCategoryLossDetailsDtos = new ArrayList<>();
        for (TotalCategoryLossDetailsDto totalCategoryLossDetailsDtoObj : totalCategoryLossDetailsDtoList) {
            if (cCount == 5) {
                break;
            }
            totalCategoryLossDetailsDtos.add(totalCategoryLossDetailsDtoObj);
            cCount++;
        }
        return new ResponseEntity<>(totalCategoryLossDetailsDtos, HttpStatus.OK);
    }

    @PostMapping("/loss-analysis-dashboard-production-loss-time-download")
    private void lossAnalysisProductionLineReportDownload(HttpServletRequest request, HttpServletResponse response, @Valid @RequestBody LossAnalysisDashboadRequiredDto lossAnalysisDashboadRequiredDto) throws ServletException, IOException, WriteException {
        OutputStream out = null;
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment; filename= Production_Line_Loss_Details.xls");
        WritableWorkbook workbook = jxl.Workbook.createWorkbook(response.getOutputStream());
        try {
            writeToSheetProductionDashboad(workbook, lossAnalysisDashboadRequiredDto, 0);
            workbook.write();
            workbook.close();
        } catch (Exception e) {
            throw new ServletException("Something Went Wrong", e);
        } finally {
            if (out != null)
                out.close();
        }
    }

    private WritableWorkbook writeToSheetProductionDashboad(WritableWorkbook workbook, LossAnalysisDashboadRequiredDto lossAnalysisDashboadRequiredDto, int index) throws IOException, WriteException {

        WritableSheet s = workbook.createSheet("Loss Details", index);
        s.getSettings().setPrintGridLines(false);

        WritableFont headerFont = new WritableFont(WritableFont.TIMES, 12);
        headerFont.setBoldStyle(WritableFont.BOLD);
        WritableCellFormat headerFormat = new WritableCellFormat(headerFont);
        headerFormat.setAlignment(CENTRE);
        headerFormat.setVerticalAlignment(VerticalAlignment.getAlignment(HSSFCellStyle.VERTICAL_CENTER));
        headerFormat.setWrap(true);

        WritableFont headerFont1 = new WritableFont(WritableFont.TIMES, 10);
        headerFont1.setBoldStyle(WritableFont.BOLD);
        WritableCellFormat headerFormat1 = new WritableCellFormat(headerFont1);
        headerFormat1.setAlignment(CENTRE);
        headerFormat1.setVerticalAlignment(VerticalAlignment.getAlignment(HSSFCellStyle.VERTICAL_CENTER));
        headerFormat1.setWrap(true);

        WritableFont dataFont = new WritableFont(WritableFont.TIMES, 10);
        WritableCellFormat dataFormat = new WritableCellFormat(dataFont);
        dataFormat.setAlignment(CENTRE);
        dataFormat.setVerticalAlignment(VerticalAlignment.getAlignment(HSSFCellStyle.VERTICAL_CENTER));
        dataFormat.setWrap(true);

        Shift shift = shiftService.getShift(lossAnalysisDashboadRequiredDto.getShiftId());
        long totalTime = getTotalTime(lossAnalysisDashboadRequiredDto) * 60;
        List<ProductionLineMaster> productionLineMasterList = productionLineMasterService.getAllProductionLineMaster();
        List<LossCategory> lossCategoryList = lossCategoryService.getAllLossCategory();

        s.mergeCells(1, 1, 1, 3);
        if (productionLineMasterList.size() > 7) {
            s.mergeCells(2, 2, productionLineMasterList.size() + 1, 2);
            s.mergeCells(8, 1, productionLineMasterList.size() + 1, 1);
        } else {
            s.mergeCells(2, 2, 7, 2);
            for (int i = 2; i <= 7; i++) {
                s.setColumnView(i, 15);
            }
        }

        s.addCell(new Label(1, 1, "Loss Category", headerFormat));
        s.addCell(new Label(2, 1, "From Date", headerFormat));
        s.addCell(new Label(3, 1, "" + DateUtil.convertToDateString(lossAnalysisDashboadRequiredDto.getFromDate()), headerFormat1));
        s.addCell(new Label(4, 1, "To Date", headerFormat));
        s.addCell(new Label(5, 1, "" + DateUtil.convertToDateString(lossAnalysisDashboadRequiredDto.getToDate()), headerFormat1));
        s.addCell(new Label(6, 1, "Shift", headerFormat));
        s.addCell(new Label(7, 1, "" + shift.getName(), headerFormat1));
        s.addCell(new Label(2, 2, "Production Line", headerFormat));

        s.setColumnView(0, 3);
        s.setColumnView(1, 30);

        LossAnalysisDashboadDto lossAnalysisDashboadDto = getLossAnalysisDashboadDto(lossAnalysisDashboadRequiredDto);
        List<TotalProductionLossDetailsDto> totalProductionLossDetailsDtos = lossAnalysisDashboadDto.getTotalProductionLossDetailsDtos();

        int column = 2;
        for (ProductionLineMaster productionLineMaster : productionLineMasterList) {
            long totalLossTime = 0;
            s.setColumnView(column, 15);
            s.addCell(new Label(column, 3, "" + productionLineMaster.getLineName(), headerFormat));
            int row = 4;
            for (LossCategory lossCategory : lossCategoryList) {
                s.addCell(new Label(1, row, "" + lossCategory.getLossCategory(), headerFormat1));

                for (TotalProductionLossDetailsDto totalProductionLossDetailsDto : totalProductionLossDetailsDtos) {
                    if (totalProductionLossDetailsDto.getProductionLineId() == productionLineMaster.getId()) {
                        List<TotalCategoryLossDetailsDto> totalCategoryLossDetailsDtos = totalProductionLossDetailsDto.getTotalCategoryLossDetailsDtos();
                        for (TotalCategoryLossDetailsDto totalCategoryLossDetailsDto : totalCategoryLossDetailsDtos) {
                            if (totalCategoryLossDetailsDto.getCategoryId() == lossCategory.getId()) {
                                totalLossTime = totalLossTime +totalCategoryLossDetailsDto.getTotalTimeInMin();
                                s.addCell(new Label(column, row, "" + totalCategoryLossDetailsDto.getTotalTimeInMin(), dataFormat));
                            }
                        }
                    }
                }
                row = row + 1;
            }
            s.addCell(new Label(1, row, "Total Loss Time", headerFormat));
            s.addCell(new Label(column, row, "" + totalLossTime, headerFormat1));
            s.addCell(new Label(1, row + 1, "Total Time", headerFormat));
            s.addCell(new Label(column, row + 1, "" + totalTime, headerFormat1));
            s.addCell(new Label(1, row + 2, "Total Run Time", headerFormat));
            s.addCell(new Label(column, row + 2, "" + (totalTime - totalLossTime), headerFormat1));

            column = column + 1;
        }
        return workbook;
    }
}