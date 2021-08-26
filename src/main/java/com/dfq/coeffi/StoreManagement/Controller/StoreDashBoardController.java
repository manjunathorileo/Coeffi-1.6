package com.dfq.coeffi.StoreManagement.Controller;

import com.dfq.coeffi.StoreManagement.Entity.EmployeeRequest;
import com.dfq.coeffi.StoreManagement.Entity.Materials;
import com.dfq.coeffi.StoreManagement.Entity.MaterialsEnum;
import com.dfq.coeffi.StoreManagement.Entity.TopItemDto;
import com.dfq.coeffi.StoreManagement.Repository.EmployeeRequestRepository;
import com.dfq.coeffi.StoreManagement.Service.EmployeeRequestService;
import com.dfq.coeffi.StoreManagement.Service.MaterialsService;
import com.dfq.coeffi.controller.BaseController;
import jxl.format.Colour;
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
public class StoreDashBoardController extends BaseController {
    @Autowired
    EmployeeRequestService employeeRequestService;
    @Autowired
    MaterialsService materialsService;
    @Autowired
    EmployeeRequestRepository employeeRequestRepository;


    @GetMapping("total-request-count")
    public ResponseEntity<Long> getRequestCount(){
        List<EmployeeRequest> employeeRequestList=employeeRequestRepository.getByEmployeeRequestAndMarkedOn(new Date());
        long count=0;
        count=employeeRequestList.size();
        return new ResponseEntity<>(count, HttpStatus.OK);
    }

    @GetMapping("completed-request-count")
    public ResponseEntity<Long> getcompletedCount(){
        List<EmployeeRequest> employeeRequestList=employeeRequestRepository.getByEmployeeRequestAndMarkedOn(new Date());
        List<EmployeeRequest> employeeRequests=new ArrayList<>();
        long count=0;
        for (EmployeeRequest e:employeeRequestList) {
            if (e.getMaterialsStatus().equals(MaterialsEnum.Completed)){
                employeeRequests.add(e);
            }
        }
        count=employeeRequests.size();
        return new ResponseEntity<>(count,HttpStatus.OK);
    }

    @GetMapping("pending-request-count")
    public ResponseEntity<Long> getPendingCount(){
        List<EmployeeRequest> employeeRequestList=employeeRequestService.getRequests();
        List<EmployeeRequest> employeeRequests=new ArrayList<>();
        long count=0,pcount=0;
        count=employeeRequestList.size();
        pcount=count-getcompletedCountRequest();
        return new ResponseEntity<>(pcount,HttpStatus.OK);
    }

    public Long getcompletedCountRequest(){
        List<EmployeeRequest> employeeRequestList=employeeRequestRepository.getByEmployeeRequestAndMarkedOn(new Date());
        List<EmployeeRequest> employeeRequests=new ArrayList<>();
        long count=0;
        for (EmployeeRequest e:employeeRequestList) {
            if (e.getMaterialsStatus().equals(MaterialsEnum.Completed)){
                employeeRequests.add(e);
            }
        }
        count=employeeRequests.size();
        return count;
    }


    @GetMapping("total-request-report")
    public ResponseEntity<List<EmployeeRequest>> getTotalRequestReport(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, ParseException {
        Date date=new Date();
        List<EmployeeRequest> employeeRequestList= getTotalRequest();
        OutputStream out = null;
        String fileName = "Total_Request_Report";
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName + ".xls");
        WritableWorkbook workbook = jxl.Workbook.createWorkbook(response.getOutputStream());
        try {
            if (employeeRequestList  != null) {
                totalRequest(workbook, employeeRequestList, response, 0) ;
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
    private WritableWorkbook totalRequest(WritableWorkbook workbook, List<EmployeeRequest> employeeRequestList, HttpServletResponse response, int index) throws IOException, WriteException {
        WritableSheet s = workbook.createSheet("Total-Request-Report", index);
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
        cellFormat.setBackground(Colour.GRAY_25);
        WritableCellFormat cellFormatDate = new WritableCellFormat(cellFont);
        cellFormatDate.setAlignment(CENTRE);
        cellFormatDate.setBackground(Colour.ICE_BLUE);
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


        int rowNum = 1;
        for (EmployeeRequest  employeeRequest : employeeRequestList) {
            s.addCell(new Label(0, 0, "#", cellFormat));
            s.addCell(new Label(0, rowNum, "" + Integer.valueOf(rowNum)));
            s.addCell(new Label(1, 0, "Request Number", cellFormat));
            s.addCell(new Label(1, rowNum, "" + employeeRequest.getRequestNumber()));
            s.addCell(new Label(2, 0, "Requested By", cellFormat));
            s.addCell(new Label(2, rowNum, "" + employeeRequest.getRequestedBy()));
            s.addCell(new Label(3, 0, "Department", cellFormat));
            s.addCell(new Label(3, rowNum, "" + employeeRequest.getDepartment()));
            int colNum = 4;
            List<Materials> materials=employeeRequest.getMaterials();
            int count=0;
            int c=rowNum;
            for (Materials met:materials) {
                s.addCell(new Label(4, 0, "Item Number", cellFormat));
                s.addCell(new Label(colNum, c, "" + met.getItemNumber()));
                int a=colNum+1;
                s.addCell(new Label(5, 0, "Item Name", cellFormat));
                s.addCell(new Label(a, c, "" + met.getItemName()));
                int b=colNum+2;
                s.addCell(new Label(6, 0, "Quantity", cellFormat));
                s.addCell(new Label(b, c, "" + met.getQuantity()));
                c=rowNum+1;
                count=count+1;
            }

            rowNum = count + 1;


        }
        return workbook;
    }

    public List<EmployeeRequest>  getTotalRequest(){
        List<EmployeeRequest> employeeRequestList=employeeRequestRepository.getByEmployeeRequestAndMarkedOn(new Date());
        return employeeRequestList;
    }

    @GetMapping("completed-request-report")
    public ResponseEntity<List<EmployeeRequest>> getCompletedReport(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, ParseException {
        List<EmployeeRequest> employeeRequestList = getCompletedRequest();
        OutputStream out = null;
        String fileName = "Completed_Request_Report";
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName + ".xls");
        WritableWorkbook workbook = jxl.Workbook.createWorkbook(response.getOutputStream());
        try {
            if (employeeRequestList != null) {
                completedReport(workbook, employeeRequestList, response, 0);
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
    private WritableWorkbook completedReport(WritableWorkbook workbook, List<EmployeeRequest> employeeRequestList, HttpServletResponse response, int index) throws IOException, WriteException {
        WritableSheet s = workbook.createSheet("Completed-Request-Report", index);
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
        cellFormat.setBackground(Colour.GRAY_25);
        WritableCellFormat cellFormatDate = new WritableCellFormat(cellFont);
        cellFormatDate.setAlignment(CENTRE);
        cellFormatDate.setBackground(Colour.ICE_BLUE);
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
        int rowNum = 1;
        for (EmployeeRequest  employeeRequest : employeeRequestList) {
            s.addCell(new Label(0, 0, "#", cellFormat));
            s.addCell(new Label(0, rowNum, "" + Integer.valueOf(rowNum)));
            s.addCell(new Label(1, 0, "Request Number", cellFormat));
            s.addCell(new Label(1, rowNum, "" + employeeRequest.getRequestNumber()));
            s.addCell(new Label(2, 0, "Requested By", cellFormat));
            s.addCell(new Label(2, rowNum, "" + employeeRequest.getRequestedBy()));
            s.addCell(new Label(3, 0, "Department", cellFormat));
            s.addCell(new Label(3, rowNum, "" + employeeRequest.getDepartment()));
            int colNum = 4;
            List<Materials> materials=employeeRequest.getMaterials();
            int count=0;
            int c=rowNum;
            for (Materials met:materials) {
                s.addCell(new Label(4, 0, "Item Number", cellFormat));
                s.addCell(new Label(colNum, c, "" + met.getItemNumber()));
                int a=colNum+1;
                s.addCell(new Label(5, 0, "Item Name", cellFormat));
                s.addCell(new Label(a, c, "" + met.getItemName()));
                int b=colNum+2;
                s.addCell(new Label(6, 0, "Quantity", cellFormat));
                s.addCell(new Label(b, c, "" + met.getQuantity()));
                c=rowNum+1;
                count=count+1;
            }
            rowNum = count + 1;
        }
        return workbook;
    }

    public List<EmployeeRequest> getCompletedRequest(){
        List<EmployeeRequest> employeeRequestList=employeeRequestRepository.getByEmployeeRequestAndMarkedOn(new Date());
        List<EmployeeRequest> employeeRequests=new ArrayList<>();
        long count=0;
        for (EmployeeRequest e:employeeRequestList) {
            if (e.getMaterialsStatus().equals(MaterialsEnum.Completed)){
                employeeRequests.add(e);
            }
        }
        return employeeRequests;
    }

    @GetMapping("pending-request-report")
    public ResponseEntity<List<EmployeeRequest>> getPendingReport(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, ParseException {
        List<EmployeeRequest> employeeRequestList = getPendingRequest();
        OutputStream out = null;
        String fileName = "Pending_Request_Report";
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName + ".xls");
        WritableWorkbook workbook = jxl.Workbook.createWorkbook(response.getOutputStream());
        try {
            if (employeeRequestList != null) {
                pendingReport(workbook, employeeRequestList, response, 0);
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
    private WritableWorkbook pendingReport(WritableWorkbook workbook, List<EmployeeRequest> employeeRequestList , HttpServletResponse response, int index) throws IOException, WriteException {
        WritableSheet s = workbook.createSheet("Pending-Request-Report", index);
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
        cellFormat.setBackground(Colour.GRAY_25);
        WritableCellFormat cellFormatDate = new WritableCellFormat(cellFont);
        cellFormatDate.setAlignment(CENTRE);
        cellFormatDate.setBackground(Colour.ICE_BLUE);
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
        int rowNum = 1;
        for (EmployeeRequest  employeeRequest : employeeRequestList) {
            s.addCell(new Label(0, 0, "#", cellFormat));
            s.addCell(new Label(0, rowNum, "" + Integer.valueOf(rowNum)));
            s.addCell(new Label(1, 0, "Request Number", cellFormat));
            s.addCell(new Label(1, rowNum, "" + employeeRequest.getRequestNumber()));
            s.addCell(new Label(2, 0, "Requested By", cellFormat));
            s.addCell(new Label(2, rowNum, "" + employeeRequest.getRequestedBy()));
            s.addCell(new Label(3, 0, "Department", cellFormat));
            s.addCell(new Label(3, rowNum, "" + employeeRequest.getDepartment()));
            int colNum = 4;
            List<Materials> materials=employeeRequest.getMaterials();
            int count=0;
            int c=rowNum;
            for (Materials met:materials) {
                s.addCell(new Label(4, 0, "Item Number", cellFormat));
                s.addCell(new Label(colNum, c, "" + met.getItemNumber()));
                int a=colNum+1;
                s.addCell(new Label(5, 0, "Item Name", cellFormat));
                s.addCell(new Label(a, c, "" + met.getItemName()));
                int b=colNum+2;
                s.addCell(new Label(6, 0, "Quantity", cellFormat));
                s.addCell(new Label(b, c, "" + met.getQuantity()));
                c=rowNum+1;
                count=count+1;
            }
            rowNum = count + 1;
        }
        return workbook;
    }
    public List<EmployeeRequest> getPendingRequest(){
        List<EmployeeRequest> employeeRequestList=employeeRequestRepository.getByEmployeeRequestAndMarkedOn(new Date());
        List<EmployeeRequest> employeeRequests=new ArrayList<>();
        long count=0;
        for (EmployeeRequest e:employeeRequestList) {
            if (e.getMaterialsStatus().equals(MaterialsEnum.Completed)){
                employeeRequests.add(e);
            }
        }
        List<EmployeeRequest> requestList=new ArrayList<>();
        for (EmployeeRequest full:employeeRequestList) {
            for (EmployeeRequest com:employeeRequests) {
                if(full!=com){
                    requestList.add(full);
                }

            }

        }
        return requestList;
    }

    @GetMapping("top-item-name")
    public ResponseEntity<TopItemDto> getTopItemName(){
        List<Materials> materials=materialsService.getMaterials();
        TopItemDto topItemDto=new TopItemDto();
        for (Materials m:materials) {
            int i=1;
            if(m.getQuantity()>materials.get(i).getQuantity()){
                topItemDto.setItemNumber(m.getItemNumber());
                topItemDto.setItemName(m.getItemName());
            }
            else{
                i=i+1;
            }

        }
        return new ResponseEntity<>(topItemDto,HttpStatus.OK);
    }

    @GetMapping("top-item-report")
    public ResponseEntity<List<EmployeeRequest>> getTopItemReport(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, ParseException {
        List<Materials> materialsList = getTopItemNameList();
        OutputStream out = null;
        String fileName = "Pending_Request_Report";
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName + ".xls");
        WritableWorkbook workbook = jxl.Workbook.createWorkbook(response.getOutputStream());
        try {
            if (materialsList != null) {
                materials(workbook, materialsList, response, 0);
            }
            if (materialsList.isEmpty())
                throw new EntityNotFoundException("entries");
            workbook.write();
            workbook.close();
        } catch (Exception e) {
            throw new ServletException("Exception in excel download", e);
        } finally {
            if (out != null)
                out.close();
        }
        return new ResponseEntity(materialsList, HttpStatus.OK);
    }
    private WritableWorkbook materials(WritableWorkbook workbook, List<Materials> materialsList, HttpServletResponse response, int index) throws IOException, WriteException {
        WritableSheet s = workbook.createSheet("Pending-Request-Report", index);
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
        cellFormat.setBackground(Colour.GRAY_25);
        WritableCellFormat cellFormatDate = new WritableCellFormat(cellFont);
        cellFormatDate.setAlignment(CENTRE);
        cellFormatDate.setBackground(Colour.ICE_BLUE);
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
        int rowNum = 1;
        for (Materials   materials : materialsList) {
            s.addCell(new Label(0, 0, "#", cellFormat));
            s.addCell(new Label(0, rowNum, "" + Integer.valueOf(rowNum)));
            s.addCell(new Label(1, 0, "Item Number", cellFormat));
            s.addCell(new Label(1, rowNum, "" + materials.getItemNumber()));
            s.addCell(new Label(2, 0, "Item Name", cellFormat));
            s.addCell(new Label(2, rowNum, "" + materials.getItemName()));
            s.addCell(new Label(3, 0, "Quantity", cellFormat));
            s.addCell(new Label(3, rowNum, "" + materials.getQuantity()));
            rowNum = rowNum  + 1;
        }
        return workbook;
    }

    public List<Materials> getTopItemNameList(){
        List<Materials> materials=materialsService.getMaterials();
        List<Materials> materialsList=new ArrayList<>();
        for (Materials m:materials) {
            int i=1;
            if(i<=10) {
                if (m.getQuantity() > materials.get(i).getQuantity()) {
                   materialsList.add(m);
                } else {
                    i = i + 1;
                }
            }

        }
        return materialsList;
    }
}
