package com.dfq.coeffi.LossAnalysis.oeeReport;

import com.dfq.coeffi.LossAnalysis.LossAnalysis;
import com.dfq.coeffi.LossAnalysis.LossAnalysisService;
import com.dfq.coeffi.LossAnalysis.LossCategory.LossCategory;
import com.dfq.coeffi.LossAnalysis.LossCategory.LossCategoryService;
import com.dfq.coeffi.LossAnalysis.productionLine.ProductionLineMaster;
import com.dfq.coeffi.LossAnalysis.productionLine.ProductionLineMasterService;
import com.dfq.coeffi.LossAnalysis.productionTrack.ProductionTrack;
import com.dfq.coeffi.LossAnalysis.productionTrack.ProductionTrackService;
import com.dfq.coeffi.SOPDetails.Event.eventCompletion.EventCompletionReportDto;
import com.dfq.coeffi.controller.BaseController;
import com.dfq.coeffi.master.shift.Shift;
import com.dfq.coeffi.master.shift.ShiftService;
import com.dfq.coeffi.util.DateUtil;
import jxl.format.Colour;
import jxl.format.VerticalAlignment;
import jxl.write.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.EntityNotFoundException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static jxl.format.Alignment.CENTRE;

@RestController
@Slf4j
public class OeeReportResource extends BaseController {

    private final ProductionLineMasterService productionLineMasterService;
    private final ShiftService shiftService;
    private final ProductionTrackService productionTrackService;
    private final LossCategoryService lossCategoryService;
    private final LossAnalysisService lossAnalysisService;

    @Autowired
    public OeeReportResource(ProductionLineMasterService productionLineMasterService, ShiftService shiftService, ProductionTrackService productionTrackService, LossCategoryService lossCategoryService, LossAnalysisService lossAnalysisService) {
        this.productionLineMasterService = productionLineMasterService;
        this.shiftService = shiftService;
        this.productionTrackService = productionTrackService;
        this.lossCategoryService = lossCategoryService;
        this.lossAnalysisService = lossAnalysisService;
    }

    @GetMapping("oee-report/{productionLineId}/{month}/{year}")
    private ResponseEntity<OeeProductionLineDto> getOeeReport(@PathVariable long productionLineId, @PathVariable int month, @PathVariable int year) {
        OeeProductionLineDto oeeProductionLineDto = getOeeReportData(productionLineId, month, year);
        return new ResponseEntity(oeeProductionLineDto, HttpStatus.OK);
    }

    public OeeProductionLineDto getOeeReportData(long productionLineId, int month, int year) {
        DecimalFormat df = new DecimalFormat("###.##");
        Date lastDateOfMonth = DateUtil.getMonthEndDate(month, year);
        long totalDay = Long.parseLong(DateUtil.getDayOfDate(lastDateOfMonth));
        String monthName = DateUtil.getMonthName(lastDateOfMonth);

        Optional<ProductionLineMaster> productionLineMasterOptional = productionLineMasterService.getProductionLineMaster(productionLineId);
        if (!productionLineMasterOptional.isPresent()) {
            throw new EntityNotFoundException("There no Such Production Line!!!!");
        }
        ProductionLineMaster productionLineMaster = productionLineMasterOptional.get();
        List<Shift> shifts = shiftService.findByStatus(true);
        if (shifts.isEmpty()) {
            throw new EntityNotFoundException("There is no shift.");
        }
        List<LossCategory> lossCategorieList = lossCategoryService.getAllLossCategory();

        OeeProductionLineDto oeeProductionLineDto = new OeeProductionLineDto();
        List<OeeProductionDateDto> oeeProductionDateDtos = new ArrayList<>();
        for (int i = 1; i <= totalDay; i++) {
            OeeProductionDateDto oeeProductionDateDto = new OeeProductionDateDto();
            List<OeeProductionShiftDto> oeeProductionShiftDtos = new ArrayList<>();
            for (Shift shift : shifts) {
                long totalTime = 0, totalLossTime = 0, totalOperationTime = 0;
                Double availability = 0.0, performance = 0.0, quality = 0.0, overallEquipmentEfficiency = 0.0;
                long totalGoodProduction = 0, nonQualityProduction = 0, totalProduction = 0;

                OeeProductionShiftDto oeeProductionShiftDto = new OeeProductionShiftDto();
                List<OeeProductionDetailsDto> oeeProductionDetailsDtos = new ArrayList<>();
                List<OeeProductionLossCategoryDetailsDto> oeeProductionLossCategoryDetailsDtos = new ArrayList<>();
                Date today = DateUtil.getDate(i, month, year);
                List<ProductionTrack> productionTrackList = productionTrackService.getProductionTrackByDateByShiftByProduction(today, shift.getId(), productionLineId);
                for (ProductionTrack productionTrack : productionTrackList) {
                    totalGoodProduction = productionTrack.getGoodProduction();
                    nonQualityProduction = productionTrack.getNonQualityProduction();
                    totalProduction = productionTrack.getCurrentTotalItemProductionNo();

                    OeeProductionDetailsDto oeeProductionDetailsDto = new OeeProductionDetailsDto();
                    oeeProductionDetailsDto.setDefaultProductionRate(productionTrack.getDefaultProductionRate());
                    oeeProductionDetailsDto.setActualProductionRate(productionTrack.getCurrentProductionRate());
                    oeeProductionDetailsDto.setGoodProduction(productionTrack.getGoodProduction());
                    oeeProductionDetailsDto.setNonQualityProduction(productionTrack.getNonQualityProduction());
                    oeeProductionDetailsDtos.add(oeeProductionDetailsDto);
                }
                oeeProductionShiftDto.setOeeProductionDetailsDtos(oeeProductionDetailsDtos);
                for (LossCategory lossCategory : lossCategorieList) {
                    long totalCategoryLossTime = 0;
                    List<LossAnalysis> lossAnalysisList = lossAnalysisService.getLossAnalysisByFromDateByToDateByShiftByProductionLineByLossCategory(today, today, shift.getId(), productionLineId, lossCategory.getId());
                    for (LossAnalysis lossAnalysis : lossAnalysisList) {
                        totalCategoryLossTime = totalCategoryLossTime + lossAnalysis.getLossTime();
                    }
                    OeeProductionLossCategoryDetailsDto oeeProductionLossCategoryDetailsDto = new OeeProductionLossCategoryDetailsDto();
                    oeeProductionLossCategoryDetailsDto.setLossCategoryId(lossCategory.getId());
                    oeeProductionLossCategoryDetailsDto.setLossCategoryName(lossCategory.getLossCategory());
                    oeeProductionLossCategoryDetailsDto.setTotalLossTime(totalCategoryLossTime);
                    oeeProductionLossCategoryDetailsDtos.add(oeeProductionLossCategoryDetailsDto);
                    totalLossTime = totalLossTime + totalCategoryLossTime;
                }
                long totalTimeInMillies = Math.abs(shift.getStartTime().getTime() - shift.getEndTime().getTime());
                totalTime = TimeUnit.MINUTES.convert(totalTimeInMillies, TimeUnit.MILLISECONDS);
                totalOperationTime = totalTime - totalLossTime;
                availability = (Double.valueOf(totalOperationTime) / Double.valueOf(totalTime)) * 100;
                if (totalProduction > 0) {
                    performance = (Double.valueOf(totalGoodProduction + nonQualityProduction) / Double.valueOf(totalProduction)) * 100;
                } else {
                    performance = Double.valueOf(0);
                }
                if ((totalGoodProduction + nonQualityProduction) > 0) {
                    quality = (Double.valueOf(totalGoodProduction) / Double.valueOf(totalGoodProduction + nonQualityProduction)) * 100;
                } else
                    quality = Double.valueOf(0);
                overallEquipmentEfficiency = availability * performance * quality;

                oeeProductionShiftDto.setShift(shift);
                oeeProductionShiftDto.setOeeProductionDetailsDtos(oeeProductionDetailsDtos);
                oeeProductionShiftDto.setTotalTime(totalTime);
                oeeProductionShiftDto.setTotalLossTime(totalLossTime);
                oeeProductionShiftDto.setTotalOperationTime(totalOperationTime);
                oeeProductionShiftDto.setOeeProductionLossCategoryDetailsDtos(oeeProductionLossCategoryDetailsDtos);
                oeeProductionShiftDto.setAvailability(Double.valueOf(df.format(availability)));
                oeeProductionShiftDto.setPerformance(Double.valueOf(df.format(performance)));
                oeeProductionShiftDto.setQuality(Double.valueOf(df.format(quality)));
                oeeProductionShiftDto.setOverallEquipmentEfficiency(Double.valueOf(df.format(overallEquipmentEfficiency)));
                oeeProductionShiftDtos.add(oeeProductionShiftDto);
            }
            oeeProductionDateDto.setDate(i);
            oeeProductionDateDto.setMonthName(monthName);
            oeeProductionDateDto.setYear(year);
            oeeProductionDateDto.setOeeProductionShiftDtos(oeeProductionShiftDtos);
            oeeProductionDateDtos.add(oeeProductionDateDto);
        }
        oeeProductionLineDto.setProductionLineName(productionLineMaster.getLineName());
        oeeProductionLineDto.setTotalNoOfDays(totalDay);
        oeeProductionLineDto.setTotalShifts(shifts.size());
        oeeProductionLineDto.setOeeProductionDateDtos(oeeProductionDateDtos);
        return oeeProductionLineDto;
    }

    @GetMapping("/oee-report-download/{productionLineId}/{month}/{year}")
    public void jobSubCategoryCycleTimeTrackExcelReport(HttpServletRequest request, HttpServletResponse response, @PathVariable long productionLineId, @PathVariable int month, @PathVariable int year) throws ServletException, IOException, WriteException {
        OutputStream out = null;
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment; filename= OEE_Report.xls");
        WritableWorkbook workbook = jxl.Workbook.createWorkbook(response.getOutputStream());
        OeeProductionLineDto oeeProductionLineDto = getOeeReportData(productionLineId, month, year);
        try {
            writeToSheetEventCompletion(workbook, oeeProductionLineDto, 0);
            workbook.write();
            workbook.close();
        } catch (Exception e) {
            throw new ServletException("Something Went Wrong", e);
        } finally {
            if (out != null)
                out.close();
        }
    }

    private WritableWorkbook writeToSheetEventCompletion(WritableWorkbook workbook, OeeProductionLineDto oeeProductionLineDtoObj, int index) throws IOException, WriteException {

        WritableSheet s = workbook.createSheet("Execution Event", index);
        s.getSettings().setPrintGridLines(false);
        WritableFont headerFont = new WritableFont(WritableFont.TIMES, 12);
        headerFont.setBoldStyle(WritableFont.BOLD);
        WritableCellFormat headerFormat = new WritableCellFormat(headerFont);
        headerFormat.setAlignment(CENTRE);
        headerFormat.setVerticalAlignment(VerticalAlignment.getAlignment(HSSFCellStyle.VERTICAL_CENTER));
        headerFormat.setWrap(true);
        //headerFormat.setBackground(Colour.BLUE);

        WritableFont headerFont1 = new WritableFont(WritableFont.TIMES, 10);
        headerFont1.setBoldStyle(WritableFont.BOLD);
        WritableCellFormat headerFormat1 = new WritableCellFormat(headerFont1);
        headerFormat1.setAlignment(CENTRE);
        headerFormat1.setVerticalAlignment(VerticalAlignment.getAlignment(HSSFCellStyle.VERTICAL_CENTER));
        headerFormat1.setWrap(true);
        //headerFormat1.setBackground(Colour.BLUE);

        WritableFont dataFont = new WritableFont(WritableFont.TIMES, 10);
        WritableCellFormat dataFormat = new WritableCellFormat(dataFont);
        dataFormat.setAlignment(CENTRE);
        dataFormat.setVerticalAlignment(VerticalAlignment.getAlignment(HSSFCellStyle.VERTICAL_CENTER));
        dataFormat.setWrap(true);
        //dataFormat.setBackground(Colour.GRAY_25);



        s.addCell(new Label(1, 1, "OEE REPORT", headerFormat));
        s.addCell(new Label(2, 2, "Date", headerFormat1));
        s.addCell(new Label(2, 3, "Shift", headerFormat1));
        s.addCell(new Label(1, 4, "Default Production Rate", headerFormat1));
        s.addCell(new Label(1, 5, "Actual Production Rate", headerFormat1));
        s.addCell(new Label(1, 6, "Good Production", headerFormat1));
        s.addCell(new Label(1, 7, "Non Quality Production", headerFormat1));
        s.addCell(new Label(2, 4, "Items/Hour", headerFormat1));
        s.addCell(new Label(2, 5, "Items/Hour", headerFormat1));
        s.addCell(new Label(2, 6, "Items", headerFormat1));
        s.addCell(new Label(2, 7, "Items", headerFormat1));

        s.addCell(new Label(1, 9, "TOTAL TIME", headerFormat1));
        s.addCell(new Label(2, 9, "min", headerFormat1));

        s.setColumnView(0, 3);
        s.setColumnView(1, 35);
        s.setColumnView(2, 10);

        s.mergeCells(3, 1, Math.toIntExact(2 + oeeProductionLineDtoObj.getTotalNoOfDays() * oeeProductionLineDtoObj.getTotalShifts()), 1);
        s.mergeCells(1, 1, 1, 3);

        OeeProductionLineDto oeeProductionLineDto = oeeProductionLineDtoObj;
        s.addCell(new Label(3, 1, "" + oeeProductionLineDto.getProductionLineName(), headerFormat1));

        int row = 2;
        int column = 3;
        List<OeeProductionDateDto> oeeProductionDateDtos = oeeProductionLineDto.getOeeProductionDateDtos();
        for (OeeProductionDateDto oeeProductionDateDto : oeeProductionDateDtos) {
            int oldColumn = column;
            int oldRow = row;
            s.addCell(new Label(column, row, "" + oeeProductionDateDto.getDate() + "-" + oeeProductionDateDto.getMonthName(), headerFormat1));

            List<OeeProductionShiftDto> oeeProductionShiftDtos = oeeProductionDateDto.getOeeProductionShiftDtos();
            for (OeeProductionShiftDto oeeProductionShiftDto : oeeProductionShiftDtos) {
                s.addCell(new Label(column, row + 1, "" + oeeProductionShiftDto.getShift().getName(), headerFormat1));

                List<OeeProductionDetailsDto> oeeProductionDetailsDtos = oeeProductionShiftDto.getOeeProductionDetailsDtos();
                for (OeeProductionDetailsDto oeeProductionDetailsDto : oeeProductionDetailsDtos) {
                    s.addCell(new Label(column, row + 2, "" + oeeProductionDetailsDto.getDefaultProductionRate(), dataFormat));
                    s.addCell(new Label(column, row + 3, "" + oeeProductionDetailsDto.getActualProductionRate(), dataFormat));
                    s.addCell(new Label(column, row + 4, "" + oeeProductionDetailsDto.getGoodProduction(), dataFormat));
                    s.addCell(new Label(column, row + 5, "" + oeeProductionDetailsDto.getNonQualityProduction(), dataFormat));
                }

                s.addCell(new Label(column, row + 7, "" + oeeProductionShiftDto.getTotalTime(), dataFormat));

                oldRow = row + 8;
                List<OeeProductionLossCategoryDetailsDto> oeeProductionLossCategoryDetailsDtos = oeeProductionShiftDto.getOeeProductionLossCategoryDetailsDtos();
                List<LossCategory> lossCategories = lossCategoryService.getAllLossCategory();
                for (LossCategory lossCategory : lossCategories) {
                    s.addCell(new Label(1, oldRow, "" + lossCategory.getLossCategory(), headerFormat1));
                    s.addCell(new Label(2, oldRow, "min", headerFormat1));
                    for (OeeProductionLossCategoryDetailsDto oeeProductionLossCategoryDetailsDto : oeeProductionLossCategoryDetailsDtos) {
                        if (oeeProductionLossCategoryDetailsDto.getLossCategoryId() == lossCategory.getId()) {
                            s.addCell(new Label(column, oldRow, "" + oeeProductionLossCategoryDetailsDto.getTotalLossTime(), dataFormat));
                        }
                    }
                    oldRow = oldRow + 1;
                }

                s.addCell(new Label(column, oldRow, "" + oeeProductionShiftDto.getTotalLossTime(), dataFormat));
                s.addCell(new Label(column, oldRow + 1, "" + oeeProductionShiftDto.getTotalOperationTime(), dataFormat));

                s.addCell(new Label(column, oldRow + 3, "" + oeeProductionShiftDto.getShift().getName(), headerFormat1));
                s.addCell(new Label(1, oldRow + 4, "Availability", headerFormat1));
                s.addCell(new Label(1, oldRow + 5, "Performance", headerFormat1));
                s.addCell(new Label(1, oldRow + 6, "Quality", headerFormat1));
                s.addCell(new Label(1, oldRow + 7, "Overall Equipment Efficiency (OEE)", headerFormat1));
                s.addCell(new Label(2, oldRow + 4, "%", headerFormat1));
                s.addCell(new Label(2, oldRow + 5, "%", headerFormat1));
                s.addCell(new Label(2, oldRow + 6, "%", headerFormat1));
                s.addCell(new Label(2, oldRow + 7, "%", headerFormat1));
                s.addCell(new Label(column, oldRow + 4, "" + oeeProductionShiftDto.getAvailability(), dataFormat));
                s.addCell(new Label(column, oldRow + 5, "" + oeeProductionShiftDto.getPerformance(), dataFormat));
                s.addCell(new Label(column, oldRow + 6, "" + oeeProductionShiftDto.getQuality(), dataFormat));
                s.addCell(new Label(column, oldRow + 7, "" + oeeProductionShiftDto.getOverallEquipmentEfficiency(), dataFormat));

                column = column + 1;
            }
            s.mergeCells(oldColumn, row, column - 1, row);
        }
        return workbook;
    }
}
