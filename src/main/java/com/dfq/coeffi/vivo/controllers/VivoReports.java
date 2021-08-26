package com.dfq.coeffi.vivo.controllers;

import com.dfq.coeffi.controller.BaseController;
import com.dfq.coeffi.vivo.entity.PaymentRules;
import com.dfq.coeffi.vivo.entity.VivoDateDto;
import com.dfq.coeffi.vivo.entity.VivoInfo;
import com.dfq.coeffi.vivo.service.PaymentRulesService;
import com.dfq.coeffi.vivo.service.VivoInfoService;
import jxl.format.Colour;
import jxl.write.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.EntityNotFoundException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static jxl.format.Alignment.*;

@RestController
public class VivoReports extends BaseController {

    //___________ARUN_____________
    @Autowired
    VivoInfoService vivoInfoService;
    @Autowired
    PaymentRulesService paymentRulesService;

    /**
     * view all
     *
     * @param dateDto
     * @return
     */
    @PostMapping("vivo-total-view")
    public List<VivoInfo> viewVivoInfo(@RequestBody VivoDateDto dateDto) {
        List<VivoInfo> vivoInfoList = vivoInfoService.filterByDates(dateDto.startDate, dateDto.endDate);
        return vivoInfoList;
    }

    /**
     * view by vehicleType
     *
     * @param dateDto
     * @return
     */
    @PostMapping("vivo-Reports/view-vehicleType")
    public List<VivoInfo> viewByVehicleType(@RequestBody VivoDateDto dateDto) {
        List<VivoInfo> vivoInfoList = vivoInfoService.filterByDates(dateDto.startDate, dateDto.endDate);
        List<VivoInfo> vivoInfosByVehicleType = new ArrayList<>();
        for (VivoInfo v : vivoInfoList) {
            if (v.getVehicleType() != null) {
                if (v.getVehicleType().getTypeOfVehicle().equalsIgnoreCase(dateDto.vehicleType)) {
                    vivoInfosByVehicleType.add(v);
                }
            }
        }
        return vivoInfosByVehicleType;
    }

    /**
     * View By visitType
     *
     * @param dateDto
     * @return
     */
    @PostMapping("vivo-Reports/view-visitType")
    public List<VivoInfo> viewByVisitType(@RequestBody VivoDateDto dateDto) {
        List<VivoInfo> vivoInfoList = vivoInfoService.filterByDates(dateDto.startDate, dateDto.endDate);
        List<VivoInfo> vivoInfosByVehicleType = new ArrayList<>();
        for (VivoInfo v : vivoInfoList) {
            if (v.getVivoPass().getPurpose().equalsIgnoreCase(dateDto.visitType)) {
                vivoInfosByVehicleType.add(v);
            }
        }
        return vivoInfosByVehicleType;
    }

    /**
     * View By Checked Within Time
     *
     * @param dateDto
     * @return
     */
    @PostMapping("vivo-Reports/view-within-time")
    public List<VivoInfo> viewByCheckedWithinTime(@RequestBody VivoDateDto dateDto) {
        List<VivoInfo> vivoInfoList = vivoInfoService.filterByDates(dateDto.startDate, dateDto.endDate);
        List<VivoInfo> vivoInfosWithinTime = new ArrayList<>();
        for (VivoInfo v : vivoInfoList) {
            if (v.getExtraTime() == "0" || v.getExitTime() == null) {
                vivoInfosWithinTime.add(v);
            }
        }
        return vivoInfosWithinTime;
    }

    /**
     * View By Checked Extra Time
     *
     * @param dateDto
     * @return
     */

    @PostMapping("vivo-Reports/view-extra-time")
    public List<VivoInfo> viewByCheckedExtraTime(@RequestBody VivoDateDto dateDto) {
        List<VivoInfo> vivoInfoList = vivoInfoService.filterByDates(dateDto.startDate, dateDto.endDate);
        List<VivoInfo> vivoInfosExtraTime = new ArrayList<>();
        for (VivoInfo v : vivoInfoList) {
            if (v.getExtraTime() != "0") {
                vivoInfosExtraTime.add(v);
            }
        }
        return vivoInfosExtraTime;
    }

    /**
     * view By Payment
     *
     * @param dateDto
     * @return
     */
    @PostMapping("vivo-Reports/view-payment")
    public List<VivoInfo> viewByPayment(@RequestBody VivoDateDto dateDto) {
        List<VivoInfo> vivoInfoList = vivoInfoService.filterByDates(dateDto.startDate, dateDto.endDate);
        List<VivoInfo> vivoInfosExtraTime = new ArrayList<>();
        for (VivoInfo v : vivoInfoList) {
            if (v.getPayableAmount() > 0) {
                vivoInfosExtraTime.add(v);
            }
        }
        return vivoInfosExtraTime;
    }

    @PostMapping("vivo-Reports/view-bay-wise")
    public List<VivoInfo> viewByBayNumber(@RequestBody VivoDateDto dateDto) {
        List<VivoInfo> vivoInfoList = vivoInfoService.filterByDates(dateDto.startDate, dateDto.endDate);
        List<VivoInfo> vivoInfoList1 = new ArrayList<>();
        for (VivoInfo vivoInfo : vivoInfoList) {
            if (vivoInfo.getBayNumber() != null) {
                if (vivoInfo.getBayNumber().equalsIgnoreCase(dateDto.bayNumber)) {
                    vivoInfoList1.add(vivoInfo);
                }
            } else {
                System.out.println("No vehicle for bay");
            }
        }
        return vivoInfoList1;
    }


    /**
     * Download All
     *
     * @param dateDto
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    @PostMapping("vivo-excel-download")
    private void createTotalvivoDetails(@RequestBody VivoDateDto dateDto, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<VivoInfo> vivoInfoList = viewVivoInfo(dateDto);
        OutputStream out = null;
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment; filename= Vivo.xlsx");
        WritableWorkbook workbook = jxl.Workbook.createWorkbook(response.getOutputStream());
        try {
            writeToSheet(workbook, response, vivoInfoList, 0);
            workbook.write();
            workbook.close();
        } catch (Exception e) {
            throw new ServletException("Exception in excel download", e);
        } finally {
            if (out != null)
                out.close();
        }
    }

    /**
     * @param dateDto
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */

    @PostMapping("vivo-download/bay-wise")
    private void bayWiseReport(@RequestBody VivoDateDto dateDto, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<VivoInfo> vivoInfoList = viewByBayNumber(dateDto);
        OutputStream out = null;
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment; filename= Vivo.xlsx");
        WritableWorkbook workbook = jxl.Workbook.createWorkbook(response.getOutputStream());
        try {
            writeToSheet(workbook, response, vivoInfoList, 0);
            workbook.write();
            workbook.close();
        } catch (Exception e) {
            throw new ServletException("Exception in excel download", e);
        } finally {
            if (out != null)
                out.close();
        }
    }

    /**
     * Download By visitType/Purpose
     *
     * @param dateDto
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    @PostMapping("vivo-excel-download/visitType")
    private void createVivoDetailsByVisitType(@RequestBody VivoDateDto dateDto, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<VivoInfo> vivoInfoList = viewByVisitType(dateDto);
        OutputStream out = null;
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment; filename= Vivo.xlsx");
        WritableWorkbook workbook = jxl.Workbook.createWorkbook(response.getOutputStream());
        try {
            writeToSheet(workbook, response, vivoInfoList, 0);
            workbook.write();
            workbook.close();
        } catch (Exception e) {
            throw new ServletException("Exception in excel download", e);
        } finally {
            if (out != null)
                out.close();
        }
    }

    /**
     * Download By vehicle-type
     *
     * @param dateDto
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    @PostMapping("vivo-excel-download/vehicleType")
    private void createCustomersDetails(@RequestBody VivoDateDto dateDto, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        List<VivoInfo> vivoInfoList = viewByVehicleType(dateDto);
        OutputStream out = null;
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment; filename= Vivo.xlsx");
        WritableWorkbook workbook = jxl.Workbook.createWorkbook(response.getOutputStream());
        try {
            writeToSheet(workbook, response, vivoInfoList, 0);
            workbook.write();
            workbook.close();
        } catch (Exception e) {
            throw new ServletException("Exception in excel download", e);
        } finally {
            if (out != null)
                out.close();
        }
    }

    /**
     * Download By within Time
     *
     * @param dateDto
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    @PostMapping("vivo-excel-download/within-time")
    private void infoWithinTimeDetails(@RequestBody VivoDateDto dateDto, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        List<VivoInfo> vivoInfoList = viewByCheckedWithinTime(dateDto);
        OutputStream out = null;
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment; filename= Vivo.xlsx");
        WritableWorkbook workbook = jxl.Workbook.createWorkbook(response.getOutputStream());
        try {
            writeToSheet(workbook, response, vivoInfoList, 0);
            workbook.write();
            workbook.close();
        } catch (Exception e) {
            throw new ServletException("Exception in excel download", e);
        } finally {
            if (out != null)
                out.close();
        }
    }

    /**
     * Download By extra Time
     *
     * @param dateDto
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    @PostMapping("vivo-excel-download/extra-time")
    private void infoExtraTimeDetails(@RequestBody VivoDateDto dateDto, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        List<VivoInfo> vivoInfoList = viewByCheckedExtraTime(dateDto);
        OutputStream out = null;
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment; filename= Vivo.xlsx");
        WritableWorkbook workbook = jxl.Workbook.createWorkbook(response.getOutputStream());
        try {
            writeToSheet(workbook, response, vivoInfoList, 0);
            workbook.write();
            workbook.close();
        } catch (Exception e) {
            throw new ServletException("Exception in excel download", e);
        } finally {
            if (out != null)
                out.close();
        }
    }

    /**
     * Download By Payment
     *
     * @param dateDto
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    @PostMapping("vivo-excel-download/payment")
    private void vivoInfoPaymentDetails(@RequestBody VivoDateDto dateDto, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        List<VivoInfo> vivoInfoList = viewByPayment(dateDto);
        OutputStream out = null;
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment; filename= Vivo.xlsx");
        WritableWorkbook workbook = jxl.Workbook.createWorkbook(response.getOutputStream());
        try {
            writeToSheet(workbook, response, vivoInfoList, 0);
            workbook.write();
            workbook.close();
        } catch (Exception e) {
            throw new ServletException("Exception in excel download", e);
        } finally {
            if (out != null)
                out.close();
        }
    }

    private WritableWorkbook writeToSheet(WritableWorkbook workbook, HttpServletResponse response, List<VivoInfo> vivoInfos, int index) throws IOException, WriteException {
        WritableSheet s = workbook.createSheet("Data Input", index);
        s.getSettings().setPrintGridLines(false);
        WritableFont headerFont = new WritableFont(WritableFont.TIMES, 10);
        headerFont.setBoldStyle(WritableFont.BOLD);
        WritableCellFormat headerFormat = new WritableCellFormat(headerFont);
        headerFormat.setAlignment(CENTRE);
        WritableFont headerFontLeft = new WritableFont(WritableFont.TIMES, 7);
        headerFontLeft.setBoldStyle(WritableFont.BOLD);
        WritableCellFormat headerFormatLeft = new WritableCellFormat(headerFontLeft);
        headerFormatLeft.setAlignment(LEFT);
        WritableFont headerFontRight = new WritableFont(WritableFont.TIMES, 7);
        headerFontRight.setBoldStyle(WritableFont.BOLD);
        WritableCellFormat headerFormatRight = new WritableCellFormat(headerFontRight);
        headerFormatRight.setAlignment(RIGHT);
        WritableFont cellFont = new WritableFont(WritableFont.TIMES);
        cellFont.setBoldStyle(WritableFont.BOLD);
        WritableCellFormat cellFormat = new WritableCellFormat(cellFont);
        cellFormat.setAlignment(CENTRE);
        cellFormat.setBackground(jxl.format.Colour.GRAY_25);
        WritableCellFormat cellFormatDate = new WritableCellFormat(cellFont);
        cellFormatDate.setAlignment(CENTRE);
        cellFormatDate.setBackground(jxl.format.Colour.ICE_BLUE);
        WritableFont cellFontRight = new WritableFont(WritableFont.TIMES);
        cellFontRight.setBoldStyle(WritableFont.BOLD);
        WritableCellFormat cellFormatRight = new WritableCellFormat(cellFontRight);
        cellFormatRight.setAlignment(RIGHT);
        WritableFont cellFontLeft = new WritableFont(WritableFont.TIMES);
        cellFontLeft.setBoldStyle(WritableFont.BOLD);
        WritableCellFormat cellFormatLeft = new WritableCellFormat(cellFontLeft);
        cellFormatLeft.setAlignment(LEFT);
        WritableFont cellFontSimpleRight = new WritableFont(WritableFont.TIMES);
        WritableCellFormat cellFormatSimpleRight = new WritableCellFormat(cellFontSimpleRight);
        cellFormatSimpleRight.setAlignment(RIGHT);

        WritableFont cf = new WritableFont(WritableFont.ARIAL, 7);
        WritableCellFormat cLeft = new WritableCellFormat(cf);
        cLeft.setAlignment(LEFT);
        cLeft.setBackground(Colour.ICE_BLUE);

        s.setColumnView(0, 10);
        s.setColumnView(1, 20);
        s.setColumnView(2, 20);
        s.setColumnView(3, 20);
        s.setColumnView(4, 20);
        s.setColumnView(5, 20);
        s.setColumnView(6, 10);
        s.setColumnView(7, 15);
        s.setColumnView(8, 15);
        s.addCell(new Label(0, 0, "DateOfVisit", cellFormat));
        s.addCell(new Label(1, 0, "VehicleType", cellFormat));
        s.addCell(new Label(2, 0, "VehicleNumber", cellFormat));
        s.addCell(new Label(3, 0, "EntryTime", cellFormat));
        s.addCell(new Label(4, 0, "ExitTime", cellFormat));
        s.addCell(new Label(5, 0, "TotalTime", cellFormat));
        s.addCell(new Label(6, 0, "Purpose", cellFormat));
        List<PaymentRules> paymentRules = paymentRulesService.getAll();
        Collections.reverse(paymentRules);
        if (paymentRules.get(0) != null && paymentRules.get(0).isApplicable()) {
            s.addCell(new Label(7, 0, "Payment", cellFormat));
        }
        int rownum = 1;
        for (VivoInfo vivoInfo : vivoInfos) {
            s.addCell(new Label(0, rownum, "" + vivoInfo.getMarkedOn(),cLeft));
            s.addCell(new Label(1, rownum, "" + vivoInfo.getVehicleType().getTypeOfVehicle(),cLeft));
            s.addCell(new Label(2, rownum, "" + vivoInfo.getVehicleNumber(),cLeft));
            if (vivoInfo.getCheckedIn()!=null) {
                s.addCell(new Label(3, rownum, "" + vivoInfo.getCheckedIn(),cLeft));
            }
            if (vivoInfo.getCheckedOut()!=null) {
                s.addCell(new Label(4, rownum, "" + vivoInfo.getCheckedOut(),cLeft));
            }
            if (vivoInfo.getWorkedHours()!=null) {
                s.addCell(new Label(5, rownum, "" + vivoInfo.getWorkedHours(),cLeft));
            }
            if (vivoInfo.getPurpose()!=null) {
                s.addCell(new Label(6, rownum, "" + vivoInfo.getPurpose(),cLeft));
            }
            if (paymentRules.get(0) != null && paymentRules.get(0).isApplicable()) {
                s.addCell(new Label(7, rownum, "" + vivoInfo.getPayableAmount(),cLeft));
            }

            rownum++;
        }
        return workbook;
    }

    //___________ARUN_____________


//    @Autowired
//    VivoInfoService vivoInfoService;
//    @Autowired
//    PaymentRulesService paymentRulesService;
//
//    /**
//     * view all
//     *
//     * @param dateDto
//     * @return
//     */
//    @PostMapping("vivo-total-view")
//    public List<VivoInfo> viewVivoInfo(@RequestBody VivoDateDto dateDto) {
//        List<VivoInfo> vivoInfoList = vivoInfoService.filterByDates(dateDto.startDate, dateDto.endDate);
//        return vivoInfoList;
//    }
//
//    /**
//     * view by vehicleType
//     *
//     * @param dateDto
//     * @return
//     */
//    @PostMapping("vivo-Reports/view-vehicleType")
//    public List<VivoInfo> viewByVehicleType(@RequestBody VivoDateDto dateDto) {
//        List<VivoInfo> vivoInfoList = vivoInfoService.filterByDates(dateDto.startDate, dateDto.endDate);
//        List<VivoInfo> vivoInfosByVehicleType = new ArrayList<>();
//        for (VivoInfo v : vivoInfoList) {
//            if (v.getVehicleType() != null) {
//                if (v.getVehicleType().getTypeOfVehicle().equalsIgnoreCase(dateDto.vehicleType)) {
//                    vivoInfosByVehicleType.add(v);
//                }
//            }
//        }
//        return vivoInfosByVehicleType;
//    }
//
//    /**
//     * View By visitType
//     *
//     * @param dateDto
//     * @return
//     */
//    @PostMapping("vivo-Reports/view-visitType")
//    public List<VivoInfo> viewByVisitType(@RequestBody VivoDateDto dateDto) {
//        List<VivoInfo> vivoInfoList = vivoInfoService.filterByDates(dateDto.startDate, dateDto.endDate);
//        List<VivoInfo> vivoInfosByVehicleType = new ArrayList<>();
//        for (VivoInfo v : vivoInfoList) {
//            if (v.getVivoPass().getPurpose().equalsIgnoreCase(dateDto.visitType)) {
//                vivoInfosByVehicleType.add(v);
//            }
//        }
//        return vivoInfosByVehicleType;
//    }
//
//    /**
//     * View By Checked Within Time
//     *
//     * @param dateDto
//     * @return
//     */
//    @PostMapping("vivo-Reports/view-within-time")
//    public List<VivoInfo> viewByCheckedWithinTime(@RequestBody VivoDateDto dateDto) {
//        List<VivoInfo> vivoInfoList = vivoInfoService.filterByDates(dateDto.startDate, dateDto.endDate);
//        List<VivoInfo> vivoInfosWithinTime = new ArrayList<>();
//        for (VivoInfo v : vivoInfoList) {
//            if (v.getExtraTime() == "0" || v.getExitTime() == null) {
//                vivoInfosWithinTime.add(v);
//            }
//        }
//        return vivoInfosWithinTime;
//    }
//
//    /**
//     * View By Checked Extra Time
//     *
//     * @param dateDto
//     * @return
//     */
//
//    @PostMapping("vivo-Reports/view-extra-time")
//    public List<VivoInfo> viewByCheckedExtraTime(@RequestBody VivoDateDto dateDto) {
//        List<VivoInfo> vivoInfoList = vivoInfoService.filterByDates(dateDto.startDate, dateDto.endDate);
//        List<VivoInfo> vivoInfosExtraTime = new ArrayList<>();
//        for (VivoInfo v : vivoInfoList) {
//            if (v.getExtraTime() != "0") {
//                vivoInfosExtraTime.add(v);
//            }
//        }
//        return vivoInfosExtraTime;
//    }
//
//    /**
//     * view By Payment
//     *
//     * @param dateDto
//     * @return
//     */
//    @PostMapping("vivo-Reports/view-payment")
//    public List<VivoInfo> viewByPayment(@RequestBody VivoDateDto dateDto) {
//        List<VivoInfo> vivoInfoList = vivoInfoService.filterByDates(dateDto.startDate, dateDto.endDate);
//        List<VivoInfo> vivoInfosExtraTime = new ArrayList<>();
//        for (VivoInfo v : vivoInfoList) {
//            if (v.getPayableAmount() > 0) {
//                vivoInfosExtraTime.add(v);
//            }
//        }
//        return vivoInfosExtraTime;
//    }
//
//    @PostMapping("vivo-Reports/view-bay-wise")
//    public List<VivoInfo> viewByBayNumber(@RequestBody VivoDateDto dateDto) {
//        List<VivoInfo> vivoInfoList = vivoInfoService.filterByDates(dateDto.startDate, dateDto.endDate);
//        List<VivoInfo> vivoInfoList1 = new ArrayList<>();
//        for (VivoInfo vivoInfo : vivoInfoList) {
//            if (vivoInfo.getBayArchiveNumber() != null) {
//                if (vivoInfo.getBayArchiveNumber().equalsIgnoreCase(dateDto.bayNumber)) {
//                    vivoInfo.setBayNumber(vivoInfo.getBayArchiveNumber());
//                    if (vivoInfo.getSlotArchiveNumber() != null) {
//                        vivoInfo.setSlotNumber(vivoInfo.getSlotArchiveNumber());
//                    }
//                    vivoInfoList1.add(vivoInfo);
//                }
//            } else if (vivoInfo.getBayNumber() != null) {
//                if (vivoInfo.getBayNumber().equalsIgnoreCase(dateDto.bayNumber)) {
//                    vivoInfoList1.add(vivoInfo);
//                }
//            }
//        }
//        return vivoInfoList1;
//    }
//
//
//    /**
//     * Download All
//     *
//     * @param dateDto
//     * @param request
//     * @param response
//     * @throws ServletException
//     * @throws IOException
//     */
//    @PostMapping("vivo-excel-download")
//    private void createTotalvivoDetails(@RequestBody VivoDateDto dateDto, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//        List<VivoInfo> vivoInfoList = viewVivoInfo(dateDto);
//        OutputStream out = null;
//        response.setContentType("application/vnd.ms-excel");
//        response.setHeader("Content-Disposition", "attachment; filename= Vivo.xlsx");
//        WritableWorkbook workbook = jxl.Workbook.createWorkbook(response.getOutputStream());
//        try {
//            writeToSheet(workbook, response, vivoInfoList, 0);
//            workbook.write();
//            workbook.close();
//        } catch (Exception e) {
//            throw new ServletException("Exception in excel download", e);
//        } finally {
//            if (out != null)
//                out.close();
//        }
//    }
//
//    /**
//     * @param dateDto
//     * @param request
//     * @param response
//     * @throws ServletException
//     * @throws IOException
//     */
//
//    @PostMapping("vivo-download/bay-wise")
//    private void bayWiseReport(@RequestBody VivoDateDto dateDto, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//        List<VivoInfo> vivoInfoList = viewByBayNumber(dateDto);
//        OutputStream out = null;
//        response.setContentType("application/vnd.ms-excel");
//        response.setHeader("Content-Disposition", "attachment; filename= Vivo.xlsx");
//        WritableWorkbook workbook = jxl.Workbook.createWorkbook(response.getOutputStream());
//        try {
//            writeToSheet(workbook, response, vivoInfoList, 0);
//            workbook.write();
//            workbook.close();
//        } catch (Exception e) {
//            throw new ServletException("Exception in excel download", e);
//        } finally {
//            if (out != null)
//                out.close();
//        }
//    }
//
//    /**
//     * Download By visitType/Purpose
//     *
//     * @param dateDto
//     * @param request
//     * @param response
//     * @throws ServletException
//     * @throws IOException
//     */
//    @PostMapping("vivo-excel-download/visitType")
//    private void createVivoDetailsByVisitType(@RequestBody VivoDateDto dateDto, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//        List<VivoInfo> vivoInfoList = viewByVisitType(dateDto);
//        OutputStream out = null;
//        response.setContentType("application/vnd.ms-excel");
//        response.setHeader("Content-Disposition", "attachment; filename= Vivo.xlsx");
//        WritableWorkbook workbook = jxl.Workbook.createWorkbook(response.getOutputStream());
//        try {
//            writeToSheet(workbook, response, vivoInfoList, 0);
//            workbook.write();
//            workbook.close();
//        } catch (Exception e) {
//            throw new ServletException("Exception in excel download", e);
//        } finally {
//            if (out != null)
//                out.close();
//        }
//    }
//
//    /**
//     * Download By vehicle-type
//     *
//     * @param dateDto
//     * @param request
//     * @param response
//     * @throws ServletException
//     * @throws IOException
//     */
//    @PostMapping("vivo-excel-download/vehicleType")
//    private void createCustomersDetails(@RequestBody VivoDateDto dateDto, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//
//        List<VivoInfo> vivoInfoList = viewByVehicleType(dateDto);
//        OutputStream out = null;
//        response.setContentType("application/vnd.ms-excel");
//        response.setHeader("Content-Disposition", "attachment; filename= Vivo.xlsx");
//        WritableWorkbook workbook = jxl.Workbook.createWorkbook(response.getOutputStream());
//        try {
//            writeToSheet(workbook, response, vivoInfoList, 0);
//            workbook.write();
//            workbook.close();
//        } catch (Exception e) {
//            throw new ServletException("Exception in excel download", e);
//        } finally {
//            if (out != null)
//                out.close();
//        }
//    }
//
//    /**
//     * Download By within Time
//     *
//     * @param dateDto
//     * @param request
//     * @param response
//     * @throws ServletException
//     * @throws IOException
//     */
//    @PostMapping("vivo-excel-download/within-time")
//    private void infoWithinTimeDetails(@RequestBody VivoDateDto dateDto, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//
//        List<VivoInfo> vivoInfoList = viewByCheckedWithinTime(dateDto);
//        OutputStream out = null;
//        response.setContentType("application/vnd.ms-excel");
//        response.setHeader("Content-Disposition", "attachment; filename= Vivo.xlsx");
//        WritableWorkbook workbook = jxl.Workbook.createWorkbook(response.getOutputStream());
//        try {
//            writeToSheet(workbook, response, vivoInfoList, 0);
//            workbook.write();
//            workbook.close();
//        } catch (Exception e) {
//            throw new ServletException("Exception in excel download", e);
//        } finally {
//            if (out != null)
//                out.close();
//        }
//    }
//
//    /**
//     * Download By extra Time
//     *
//     * @param dateDto
//     * @param request
//     * @param response
//     * @throws ServletException
//     * @throws IOException
//     */
//    @PostMapping("vivo-excel-download/extra-time")
//    private void infoExtraTimeDetails(@RequestBody VivoDateDto dateDto, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//
//        List<VivoInfo> vivoInfoList = viewByCheckedExtraTime(dateDto);
//        OutputStream out = null;
//        response.setContentType("application/vnd.ms-excel");
//        response.setHeader("Content-Disposition", "attachment; filename= Vivo.xlsx");
//        WritableWorkbook workbook = jxl.Workbook.createWorkbook(response.getOutputStream());
//        try {
//            writeToSheet(workbook, response, vivoInfoList, 0);
//            workbook.write();
//            workbook.close();
//        } catch (Exception e) {
//            throw new ServletException("Exception in excel download", e);
//        } finally {
//            if (out != null)
//                out.close();
//        }
//    }
//
//    /**
//     * Download By Payment
//     *
//     * @param dateDto
//     * @param request
//     * @param response
//     * @throws ServletException
//     * @throws IOException
//     */
//    @PostMapping("vivo-excel-download/payment")
//    private void vivoInfoPaymentDetails(@RequestBody VivoDateDto dateDto, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//
//        List<VivoInfo> vivoInfoList = viewByPayment(dateDto);
//        OutputStream out = null;
//        response.setContentType("application/vnd.ms-excel");
//        response.setHeader("Content-Disposition", "attachment; filename= Vivo.xlsx");
//        WritableWorkbook workbook = jxl.Workbook.createWorkbook(response.getOutputStream());
//        try {
//            writeToSheet(workbook, response, vivoInfoList, 0);
//            workbook.write();
//            workbook.close();
//        } catch (Exception e) {
//            throw new ServletException("Exception in excel download", e);
//        } finally {
//            if (out != null)
//                out.close();
//        }
//    }
//
//    private WritableWorkbook writeToSheet(WritableWorkbook workbook, HttpServletResponse response, List<VivoInfo> vivoInfos, int index) throws IOException, WriteException {
//        WritableSheet s = workbook.createSheet("Data Input", index);
//        s.getSettings().setPrintGridLines(false);
//
//        WritableFont headerFont = new WritableFont(WritableFont.TIMES, 14);
//        headerFont.setBoldStyle(WritableFont.BOLD);
//        WritableCellFormat headerFormat = new WritableCellFormat(headerFont);
//        headerFormat.setAlignment(CENTRE);
//        headerFormat.setBackground(Colour.GRAY_25);
//
//        s.setColumnView(0, 10);
//        s.setColumnView(1, 20);
//        s.setColumnView(2, 20);
//        s.setColumnView(3, 20);
//        s.setColumnView(4, 20);
//        s.setColumnView(5, 20);
//        s.setColumnView(6, 10);
//        s.setColumnView(7, 15);
//        s.setColumnView(8, 15);
//        s.addCell(new Label(0, 0, "DateOfVisit", headerFormat));
//        s.addCell(new Label(1, 0, "VehicleType", headerFormat));
//        s.addCell(new Label(2, 0, "VehicleNumber", headerFormat));
//        s.addCell(new Label(3, 0, "EntryTime", headerFormat));
//        s.addCell(new Label(4, 0, "ExitTime", headerFormat));
//        s.addCell(new Label(5, 0, "TotalTime", headerFormat));
//        s.addCell(new Label(6, 0, "Purpose", headerFormat));
//        List<PaymentRules> paymentRules = paymentRulesService.getAll();
//        Collections.reverse(paymentRules);
//        if (paymentRules.get(0) != null && paymentRules.get(0).isApplicable()) {
//            s.addCell(new Label(7, 0, "Payment", headerFormat));
//        }
//        int rownum = 1;
//        for (VivoInfo vivoInfo : vivoInfos) {
//            s.addCell(new Label(0, rownum, "" + vivoInfo.getMarkedOn()));
//            s.addCell(new Label(1, rownum, "" + vivoInfo.getVehicleType().getTypeOfVehicle()));
//            s.addCell(new Label(2, rownum, "" + vivoInfo.getVehicleNumber()));
//            if (vivoInfo.getCheckedIn() != null) {
//                s.addCell(new Label(3, rownum, "" + vivoInfo.getCheckedIn()));
//            }
//            if (vivoInfo.getCheckedOut() != null) {
//                s.addCell(new Label(4, rownum, "" + vivoInfo.getCheckedOut()));
//            }
//            if (vivoInfo.getWorkedHours() != null) {
//                s.addCell(new Label(5, rownum, "" + vivoInfo.getWorkedHours()));
//            }
//            if (vivoInfo.getPurpose() != null) {
//                s.addCell(new Label(6, rownum, "" + vivoInfo.getPurpose()));
//            }
//            if (paymentRules.get(0) != null && paymentRules.get(0).isApplicable()) {
//                s.addCell(new Label(7, rownum, "" + vivoInfo.getPayableAmount()));
//            }
//
//            rownum++;
//        }
//        return workbook;
//    }


}
