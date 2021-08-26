package com.dfq.coeffi.StoreManagement.Controller;

import com.dfq.coeffi.StoreManagement.Entity.IqcInspector;
import com.dfq.coeffi.StoreManagement.Entity.IqcInspectorItems;
import com.dfq.coeffi.StoreManagement.Entity.MaterialsEnum;
import com.dfq.coeffi.StoreManagement.Entity.ProductionItemQualityCheck;
import com.dfq.coeffi.StoreManagement.Repository.IqcInspectorItemsRepository;
import com.dfq.coeffi.StoreManagement.Repository.IqcInspectorRepository;
import com.dfq.coeffi.controller.BaseController;
import jxl.write.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.EntityNotFoundException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static jxl.format.Alignment.*;

@RestController
public class IqcInspectorDashboardController extends BaseController  {
    @Autowired
    IqcInspectorRepository iqcInspectorRepository;

    @Autowired
    IqcInspectorItemsRepository  iqcInspectorItemsRepository;

    @GetMapping("iqc-total-count")
    public ResponseEntity<Long> getTotalCount(){
        List<IqcInspector> iqcInspectorList=iqcInspectorRepository.findAll();
        long count=0;
        count=iqcInspectorList.size();

        return new ResponseEntity<>(count, HttpStatus.OK);
    }

    @GetMapping("iqc-approve-count")
    public ResponseEntity<Long> getApproveCount(){
        List<IqcInspector> iqcInspectorList=iqcInspectorRepository.findAll();
        List<IqcInspector> iqcInspectorList1=new ArrayList<>();
        long count=0;
        for (IqcInspector i:iqcInspectorList) {
            if(i.getItemStatus().equals(MaterialsEnum.Approved)){
                iqcInspectorList1.add(i);
            }

        }
        count=iqcInspectorList1.size();

        return new ResponseEntity<>(count, HttpStatus.OK);
    }

    @GetMapping("iqc-reject-count")
    public ResponseEntity<Long> getRejectCount(){
        List<IqcInspector> iqcInspectorList=iqcInspectorRepository.findAll();
        List<IqcInspector> iqcInspectorList1=new ArrayList<>();
        long count=0;
        for (IqcInspector i:iqcInspectorList) {
            if(i.getItemStatus().equals(MaterialsEnum.Rejected)){
                iqcInspectorList1.add(i);
            }

        }
        count=iqcInspectorList1.size();

        return new ResponseEntity<>(count, HttpStatus.OK);
    }

    @GetMapping("iqc-total-materials-report")
    public ResponseEntity<List<IqcInspector>> getTotalRequestReportIqc(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, ParseException {
        Date date=new Date();
        List<IqcInspector> employeeRequestList= getTotalCount1();
        OutputStream out = null;
        String fileName = "Total_Report";
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName + ".xls");
        WritableWorkbook workbook = jxl.Workbook.createWorkbook(response.getOutputStream());
        try {
            if (employeeRequestList  != null) {
                totalRequestFqc1(workbook, employeeRequestList, response, 0) ;
            }
            if (employeeRequestList.isEmpty())
                throw new EntityNotFoundException("entries");
            workbook.write();
            workbook.close();
        } catch (Exception e) {
            throw new ServletException("Exception in excel download", e);
        } finally {
            if (out != null)
                out.close();
        }
        return new ResponseEntity(employeeRequestList, HttpStatus.OK);
    }
    private WritableWorkbook totalRequestFqc1(WritableWorkbook workbook, List<IqcInspector> employeeRequestList, HttpServletResponse response, int index) throws IOException, WriteException {
        WritableSheet s = workbook.createSheet("Total-Materials-Report", index);
        s.getSettings().setPrintGridLines(false);
        WritableFont headerFont = new WritableFont(WritableFont.TIMES, 14);
        headerFont.setBoldStyle(WritableFont.BOLD);
        WritableCellFormat headerFormat = new WritableCellFormat(headerFont);
        headerFormat.setAlignment(CENTRE);
        WritableFont headerFontLeft = new WritableFont(WritableFont.TIMES, 14);
        headerFontLeft.setBoldStyle(WritableFont.BOLD);
        WritableCellFormat headerFormatLeft = new WritableCellFormat(headerFontLeft);
        headerFormatLeft.setAlignment(LEFT);
        WritableFont headerFontRight = new WritableFont(WritableFont.TIMES, 14);
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
        s.setColumnView(0, 5);
        s.setColumnView(1, 20);
        s.setColumnView(2, 15);
        s.setColumnView(3, 10);
        s.setColumnView(4, 10);
        s.setColumnView(5, 10);
        s.setColumnView(6, 10);
        s.setColumnView(7, 10);
        s.setColumnView(8, 10);
        s.setColumnView(9, 10);


        int rowNum = 1;
        for (IqcInspector   employeeRequest : employeeRequestList) {
            s.addCell(new Label(0, 0, "#", cellFormat));
            s.addCell(new Label(0, rowNum, "" + Integer.valueOf(rowNum)));
            s.addCell(new Label(1, 0, "Po", cellFormat));
            s.addCell(new Label(1, rowNum, "" + employeeRequest.getPo()));
            s.addCell(new Label(2, 0, "Grn", cellFormat));
            s.addCell(new Label(2, rowNum, "" + employeeRequest.getGrn()));
            s.addCell(new Label(3, 0, "Supplier Name", cellFormat));
            s.addCell(new Label(3, rowNum, "" + employeeRequest.getSupplierName()));
            s.addCell(new Label(4, 0, "Received Date", cellFormat));
            s.addCell(new Label(4, rowNum, "" + employeeRequest.getReceivedDate()));
            s.addCell(new Label(5, 0, "Production Employee", cellFormat));

        }
        return workbook;
    }


    public List<IqcInspector> getTotalCount1(){
        List<IqcInspector> iqcInspectorList=iqcInspectorRepository.findAll() ;
        return iqcInspectorList;
    }

    @GetMapping("iqc-Approve-materials-report")
    public ResponseEntity<List<IqcInspector>> getApprovedRequestReportIqc(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, ParseException {
        Date date=new Date();
        List<IqcInspector> employeeRequestList= getApproveCount1();
        OutputStream out = null;
        String fileName = "Approve_Report";
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName + ".xls");
        WritableWorkbook workbook = jxl.Workbook.createWorkbook(response.getOutputStream());
        try {
            if (employeeRequestList  != null) {
                totalRequestFqc2(workbook, employeeRequestList, response, 0) ;
            }
            if (employeeRequestList.isEmpty())
                throw new EntityNotFoundException("entries");
            workbook.write();
            workbook.close();
        } catch (Exception e) {
            throw new ServletException("Exception in excel download", e);
        } finally {
            if (out != null)
                out.close();
        }
        return new ResponseEntity(employeeRequestList, HttpStatus.OK);
    }
    private WritableWorkbook totalRequestFqc2(WritableWorkbook workbook, List<IqcInspector> employeeRequestList, HttpServletResponse response, int index) throws IOException, WriteException {
        WritableSheet s = workbook.createSheet("Approve-Materials-Report", index);
        s.getSettings().setPrintGridLines(false);
        WritableFont headerFont = new WritableFont(WritableFont.TIMES, 14);
        headerFont.setBoldStyle(WritableFont.BOLD);
        WritableCellFormat headerFormat = new WritableCellFormat(headerFont);
        headerFormat.setAlignment(CENTRE);
        WritableFont headerFontLeft = new WritableFont(WritableFont.TIMES, 14);
        headerFontLeft.setBoldStyle(WritableFont.BOLD);
        WritableCellFormat headerFormatLeft = new WritableCellFormat(headerFontLeft);
        headerFormatLeft.setAlignment(LEFT);
        WritableFont headerFontRight = new WritableFont(WritableFont.TIMES, 14);
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
        s.setColumnView(0, 5);
        s.setColumnView(1, 20);
        s.setColumnView(2, 15);
        s.setColumnView(3, 10);
        s.setColumnView(4, 10);
        s.setColumnView(5, 10);
        s.setColumnView(6, 10);
        s.setColumnView(7, 10);
        s.setColumnView(8, 10);
        s.setColumnView(9, 10);


        int rowNum = 1;
        for (IqcInspector   employeeRequest : employeeRequestList) {
            s.addCell(new Label(0, 0, "#", cellFormat));
            s.addCell(new Label(0, rowNum, "" + Integer.valueOf(rowNum)));
            s.addCell(new Label(1, 0, "Po", cellFormat));
            s.addCell(new Label(1, rowNum, "" + employeeRequest.getPo()));
            s.addCell(new Label(2, 0, "Grn", cellFormat));
            s.addCell(new Label(2, rowNum, "" + employeeRequest.getGrn()));
            s.addCell(new Label(3, 0, "Supplier Name", cellFormat));
            s.addCell(new Label(3, rowNum, "" + employeeRequest.getSupplierName()));
            s.addCell(new Label(4, 0, "Received Date", cellFormat));
            s.addCell(new Label(4, rowNum, "" + employeeRequest.getReceivedDate()));
            s.addCell(new Label(5, 0, "Production Employee", cellFormat));

        }
        return workbook;
    }


    public List<IqcInspector> getApproveCount1(){
        List<IqcInspector> iqcInspectorList=iqcInspectorRepository.findAll();
        List<IqcInspector> iqcInspectorList1=new ArrayList<>();

        for (IqcInspector i:iqcInspectorList) {
            if(i.getItemStatus().equals(MaterialsEnum.Approved)){
                iqcInspectorList1.add(i);
            }

        }


        return iqcInspectorList1;
    }


    @GetMapping("iqc-rejected-materials-report")
    public ResponseEntity<List<IqcInspector>> getRejectRequestReportIqc(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, ParseException {
        Date date=new Date();
        List<IqcInspector> employeeRequestList= getRejectCount1();
        OutputStream out = null;
        String fileName = "Reject_Report";
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName + ".xls");
        WritableWorkbook workbook = jxl.Workbook.createWorkbook(response.getOutputStream());
        try {
            if (employeeRequestList  != null) {
                totalRequestFqc3(workbook, employeeRequestList, response, 0) ;
            }
            if (employeeRequestList.isEmpty())
                throw new EntityNotFoundException("entries");
            workbook.write();
            workbook.close();
        } catch (Exception e) {
            throw new ServletException("Exception in excel download", e);
        } finally {
            if (out != null)
                out.close();
        }
        return new ResponseEntity(employeeRequestList, HttpStatus.OK);
    }
    private WritableWorkbook totalRequestFqc3(WritableWorkbook workbook, List<IqcInspector> employeeRequestList, HttpServletResponse response, int index) throws IOException, WriteException {
        WritableSheet s = workbook.createSheet("Reject-Materials-Report", index);
        s.getSettings().setPrintGridLines(false);
        WritableFont headerFont = new WritableFont(WritableFont.TIMES, 14);
        headerFont.setBoldStyle(WritableFont.BOLD);
        WritableCellFormat headerFormat = new WritableCellFormat(headerFont);
        headerFormat.setAlignment(CENTRE);
        WritableFont headerFontLeft = new WritableFont(WritableFont.TIMES, 14);
        headerFontLeft.setBoldStyle(WritableFont.BOLD);
        WritableCellFormat headerFormatLeft = new WritableCellFormat(headerFontLeft);
        headerFormatLeft.setAlignment(LEFT);
        WritableFont headerFontRight = new WritableFont(WritableFont.TIMES, 14);
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
        s.setColumnView(0, 5);
        s.setColumnView(1, 20);
        s.setColumnView(2, 15);
        s.setColumnView(3, 10);
        s.setColumnView(4, 10);
        s.setColumnView(5, 10);
        s.setColumnView(6, 10);
        s.setColumnView(7, 10);
        s.setColumnView(8, 10);
        s.setColumnView(9, 10);


        int rowNum = 1;
        for (IqcInspector   employeeRequest : employeeRequestList) {
            s.addCell(new Label(0, 0, "#", cellFormat));
            s.addCell(new Label(0, rowNum, "" + Integer.valueOf(rowNum)));
            s.addCell(new Label(1, 0, "Po", cellFormat));
            s.addCell(new Label(1, rowNum, "" + employeeRequest.getPo()));
            s.addCell(new Label(2, 0, "Grn", cellFormat));
            s.addCell(new Label(2, rowNum, "" + employeeRequest.getGrn()));
            s.addCell(new Label(3, 0, "Supplier Name", cellFormat));
            s.addCell(new Label(3, rowNum, "" + employeeRequest.getSupplierName()));
            s.addCell(new Label(4, 0, "Received Date", cellFormat));
            s.addCell(new Label(4, rowNum, "" + employeeRequest.getReceivedDate()));
            s.addCell(new Label(5, 0, "Production Employee", cellFormat));

        }
        return workbook;
    }



    public List<IqcInspector> getRejectCount1(){
        List<IqcInspector> iqcInspectorList=iqcInspectorRepository.findAll();
        List<IqcInspector> iqcInspectorList1=new ArrayList<>();
        for (IqcInspector i:iqcInspectorList) {
            if(i.getItemStatus().equals(MaterialsEnum.Rejected)){
                iqcInspectorList1.add(i);
            }

        }


        return iqcInspectorList1;
    }

}
