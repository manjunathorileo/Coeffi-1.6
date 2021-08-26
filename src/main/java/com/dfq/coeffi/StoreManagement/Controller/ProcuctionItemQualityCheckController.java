package com.dfq.coeffi.StoreManagement.Controller;

import com.dfq.coeffi.StoreManagement.Entity.*;
import com.dfq.coeffi.StoreManagement.Repository.*;
import com.dfq.coeffi.controller.BaseController;
import com.dfq.coeffi.entity.hr.employee.Employee;
import com.dfq.coeffi.master.shift.Shift;
import com.dfq.coeffi.master.shift.ShiftRepository;
import com.dfq.coeffi.service.hr.EmployeeService;
import jxl.write.*;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
import java.util.Optional;

import static jxl.format.Alignment.*;

@RestController
public class ProcuctionItemQualityCheckController extends BaseController {

    @Autowired
    ProductionItemQualityCheckRepository productionItemQualityCheckRepository;
    @Autowired
    ProductionMasterRepository productionMasterRepository;
    @Autowired
    BatchMasterRepository batchMasterRepository;
    @Autowired
    ProductionLineMastersRepository productionLineMastersRepository;
    @Autowired
    ShiftRepository shiftRepository;
    @Autowired
    FactroysRepository factroysRepository;
    @Autowired
    EmployeeService employeeService;

    @GetMapping("get-produced-materials")
    public ResponseEntity<List<ProductionItemQualityCheck>> getProductions() {
        List<ProductionItemQualityCheck> productionItemQualityCheckList = productionItemQualityCheckRepository.findAll();
        List<ProductionItemQualityCheck> productionItemQualityCheckList1 = new ArrayList<>();
        for (ProductionItemQualityCheck p : productionItemQualityCheckList) {
            if (p.getLqcStatus().equals(MaterialStatus.New)) {
                List<ProductionMaster> productionMasters = p.getProductionMasters();
                List<ProductionMaster> productionMasters1 = new ArrayList<>();
                for (ProductionMaster m : productionMasters) {
                    if (m.getProductionLqcItemStatus() == MaterialStatus.New) {
                        productionMasters1.add(m);
                    }
                }
                p.setProductionMasters(productionMasters1);
                productionItemQualityCheckList1.add(p);
            }
        }

        return new ResponseEntity<>(productionItemQualityCheckList1, HttpStatus.OK);
    }

//    @GetMapping("get-produced-materials")
//    public ResponseEntity<List<ProductionItemQualityCheck>> getProductions(){
//        List<ProductionItemQualityCheck> productionItemQualityCheckList=productionItemQualityCheckRepository.findAll();
//        List<ProductionItemQualityCheck> productionItemQualityCheckList1=new ArrayList<>();
//        ProductionItemQualityCheck productionItemQualityCheck=new ProductionItemQualityCheck();
//        for (ProductionItemQualityCheck p:productionItemQualityCheckList) {
//            productionItemQualityCheck.setBatchMasters(p.getBatchMasters());
//                productionItemQualityCheck.setProductionLineMasters(p.getProductionLineMasters());
//                productionItemQualityCheck.setShifts(p.getShifts());
//                List<ProductionMaster> productionMasters = p.getProductionMasters();
//                List<ProductionMaster> productionMasters1 = new ArrayList<>();
//                for (ProductionMaster m : productionMasters) {
//                    if (m.getProductionItemStatus() == MaterialStatus.New) {
//                        productionMasters1.add(m);
//                    }
//                }
//                productionItemQualityCheck.setProductionMasters(productionMasters1);
//                productionItemQualityCheckList1.add(productionItemQualityCheck);
//            }
//
//        return new ResponseEntity<>(productionItemQualityCheckList1, HttpStatus.OK);
//    }

    @GetMapping("get-Approved-materials")
    public ResponseEntity<List<ProductionItemQualityCheck>> getApproved() {
        List<ProductionItemQualityCheck> productionItemQualityCheckList = productionItemQualityCheckRepository.findAll();
        List<ProductionItemQualityCheck> productionItemQualityCheckList1 = new ArrayList<>();
        for (ProductionItemQualityCheck p : productionItemQualityCheckList) {
            if (p.getLqcStatus().equals(MaterialStatus.Approved)) {
                List<ProductionMaster> productionMasters = p.getProductionMasters();
                List<ProductionMaster> productionMasters1 = new ArrayList<>();
                for (ProductionMaster m : productionMasters) {
                    if (m.getProductionLqcItemStatus() == MaterialStatus.Approved) {
                        productionMasters1.add(m);
                    }
                }
                p.setProductionMasters(productionMasters1);
                productionItemQualityCheckList1.add(p);

            }
        }

        return new ResponseEntity<>(productionItemQualityCheckList, HttpStatus.OK);
    }

    @GetMapping("get-rejected-materials")
    public ResponseEntity<List<ProductionItemQualityCheck>> getRejected() {
        List<ProductionItemQualityCheck> productionItemQualityCheckList = productionItemQualityCheckRepository.findAll();
        List<ProductionItemQualityCheck> productionItemQualityCheckList1 = new ArrayList<>();
        for (ProductionItemQualityCheck p : productionItemQualityCheckList) {
            if (p.getLqcStatus().equals(MaterialStatus.Rejected)) {
                List<ProductionMaster> productionMasters = p.getProductionMasters();
                List<ProductionMaster> productionMasters1 = new ArrayList<>();
                for (ProductionMaster m : productionMasters) {
                    if (m.getProductionLqcItemStatus() == MaterialStatus.Rejected) {
                        productionMasters1.add(m);
                    }
                }
                p.setProductionMasters(productionMasters1);
                productionItemQualityCheckList1.add(p);
            }
        }

        return new ResponseEntity<>(productionItemQualityCheckList1, HttpStatus.OK);
    }

    @PostMapping("lqc-manager-approve/{empId}/{itemId}/{pId}")
    public ResponseEntity<ProductionMaster> lqcApprove(@PathVariable("empId") long empid, @PathVariable("itemId") long itemId, @PathVariable("pId") long pId) {
        ProductionItemQualityCheck productionItemQualityCheck = productionItemQualityCheckRepository.findOne(itemId);
        List<ProductionMaster> productionMasterList = productionItemQualityCheck.getProductionMasters();
        int count = 0;
        for (ProductionMaster p : productionMasterList) {
            if (p.getProductionLqcItemStatus() == MaterialStatus.New) {
                count = count + 1;
            }
        }
        ProductionMaster productionMaster = productionMasterRepository.findOne(pId);
        productionMaster.setProductionLqcItemStatus(MaterialStatus.Approved);
        productionMasterRepository.save(productionMaster);
        System.out.println(count);
        if (count == 1) {
            Date date = new Date();
            productionItemQualityCheck.setLqcOn(date);
            productionItemQualityCheck.setLqcStatus(MaterialStatus.Approved);
            Optional<Employee> employee = employeeService.getEmployee(empid);
            productionItemQualityCheck.setLqcBy(employee.get().getFirstName() + " " + employee.get().getLastName());
            productionItemQualityCheckRepository.save(productionItemQualityCheck);
        }
        return new ResponseEntity<>(productionMaster, HttpStatus.OK);
    }

    @PostMapping("lqc-manager-reject/{empId}/{itemId}/{pId}")
    public ResponseEntity<ProductionMaster> lqcReject(@PathVariable("empId") long empid, @PathVariable("itemId") long itemId, @PathVariable("pId") long pId) {
        ProductionItemQualityCheck productionItemQualityCheck = productionItemQualityCheckRepository.findOne(itemId);
        List<ProductionMaster> productionMasterList = productionItemQualityCheck.getProductionMasters();
        int count = 0;
        for (ProductionMaster p : productionMasterList) {
            if (p.getProductionLqcItemStatus() == MaterialStatus.New) {
                count = count + 1;
            }
        }
        ProductionMaster productionMaster = productionMasterRepository.findOne(pId);
        productionMaster.setProductionLqcItemStatus(MaterialStatus.Rejected);
        productionMasterRepository.save(productionMaster);
        if (count == 1) {
            Date date = new Date();
            productionItemQualityCheck.setLqcRejectedOn(date);
            productionItemQualityCheck.setLqcStatus(MaterialStatus.Rejected);
            Optional<Employee> employee = employeeService.getEmployee(empid);
            productionItemQualityCheck.setLqcRejectedBy(employee.get().getFirstName() + " " + employee.get().getLastName());
            productionItemQualityCheckRepository.save(productionItemQualityCheck);
        }

        return new ResponseEntity<>(productionMaster, HttpStatus.OK);
    }

    @GetMapping("get-fqc-materials")
    public ResponseEntity<List<ProductionItemQualityCheck>> getProductionsFqc() {
        List<ProductionItemQualityCheck> productionItemQualityCheckList = productionItemQualityCheckRepository.findAll();
        List<ProductionItemQualityCheck> productionItemQualityCheckList1 = new ArrayList<>();
        for (ProductionItemQualityCheck p : productionItemQualityCheckList) {
            if (p.getFqcStatus().equals(MaterialStatus.New) && p.getLqcStatus().equals(MaterialStatus.Approved)) {
                List<ProductionMaster> productionMasters = p.getProductionMasters();
                List<ProductionMaster> productionMasters1 = new ArrayList<>();
                for (ProductionMaster m : productionMasters) {
                    if (m.getProductionFqcItemStatus() == MaterialStatus.New) {
                        productionMasters1.add(m);
                    }
                }
                p.setProductionMasters(productionMasters1);
                productionItemQualityCheckList1.add(p);

            }
        }

        return new ResponseEntity<>(productionItemQualityCheckList1, HttpStatus.OK);
    }

    @GetMapping("get-fqc-Approved-materials")
    public ResponseEntity<List<ProductionItemQualityCheck>> getApprovedFqc() {
        List<ProductionItemQualityCheck> productionItemQualityCheckList = productionItemQualityCheckRepository.findAll();
        List<ProductionItemQualityCheck> productionItemQualityCheckList1 = new ArrayList<>();
        for (ProductionItemQualityCheck p : productionItemQualityCheckList) {
            if (p.getFqcStatus().equals(MaterialStatus.Approved)) {
                List<ProductionMaster> productionMasters = p.getProductionMasters();
                List<ProductionMaster> productionMasters1 = new ArrayList<>();
                for (ProductionMaster m : productionMasters) {
                    if (m.getProductionFqcItemStatus() .equals( MaterialStatus.Approved)) {
                        productionMasters1.add(m);
                    }
                }
                p.setProductionMasters(productionMasters1);
                productionItemQualityCheckList1.add(p);

            }
        }

        return new ResponseEntity<>(productionItemQualityCheckList1, HttpStatus.OK);
    }

    @GetMapping("get-fqc-rejected-materials")
    public ResponseEntity<List<ProductionItemQualityCheck>> getRejectedFqc() {
        List<ProductionItemQualityCheck> productionItemQualityCheckList = productionItemQualityCheckRepository.findAll();
        List<ProductionItemQualityCheck> productionItemQualityCheckList1 = new ArrayList<>();
        for (ProductionItemQualityCheck p : productionItemQualityCheckList) {
            if (p.getFqcStatus().equals(MaterialStatus.Rejected)) {
                List<ProductionMaster> productionMasters = p.getProductionMasters();
                List<ProductionMaster> productionMasters1 = new ArrayList<>();
                for (ProductionMaster m : productionMasters) {
                    if (m.getProductionFqcItemStatus().equals( MaterialStatus.Rejected)) {
                        productionMasters1.add(m);
                    }
                }
                p.setProductionMasters(productionMasters1);
                productionItemQualityCheckList1.add(p);

            }
        }

        return new ResponseEntity<>(productionItemQualityCheckList1, HttpStatus.OK);
    }

    @PostMapping("fqc-manager-approve/{empId}/{itemId}/{pId}")
    public ResponseEntity<ProductionMaster> fqcApprove(@PathVariable("empId") long empid, @PathVariable("itemId") long itemId, @PathVariable("pId") long pId) {
        ProductionItemQualityCheck productionItemQualityCheck = productionItemQualityCheckRepository.findOne(itemId);
        List<ProductionMaster> productionMasterList = productionItemQualityCheck.getProductionMasters();
        int count = 0;
        for (ProductionMaster p : productionMasterList) {
            if (p.getProductionFqcItemStatus() == MaterialStatus.New) {
                count = count + 1;
            }
        }
        ProductionMaster productionMaster = productionMasterRepository.findOne(pId);
        productionMaster.setProductionFqcItemStatus(MaterialStatus.Approved);
        productionMasterRepository.save(productionMaster);
        if (count == 1) {
            Date date = new Date();
            productionItemQualityCheck.setFqcOn(date);
            productionItemQualityCheck.setFqcStatus(MaterialStatus.Approved);
            Optional<Employee> employee = employeeService.getEmployee(empid);
            productionItemQualityCheck.setFqcBy(employee.get().getFirstName() + " " + employee.get().getLastName());
            productionItemQualityCheckRepository.save(productionItemQualityCheck);
        }

        return new ResponseEntity<>(productionMaster, HttpStatus.OK);
    }

    @PostMapping("fqc-manager-reject/{empId}/{itemId}/{pId}")
    public ResponseEntity<ProductionMaster> fqcReject(@PathVariable("empId") long empid, @PathVariable("itemId") long itemId, @PathVariable("pId") long pId) {
        ProductionItemQualityCheck productionItemQualityCheck = productionItemQualityCheckRepository.findOne(itemId);
        List<ProductionMaster> productionMasterList = productionItemQualityCheck.getProductionMasters();
        int count = 0;
        for (ProductionMaster p : productionMasterList) {
            if (p.getProductionFqcItemStatus() .equals(MaterialStatus.New)) {
                count = count + 1;
            }
        }
        ProductionMaster productionMaster = productionMasterRepository.findOne(pId);
        productionMaster.setProductionFqcItemStatus(MaterialStatus.Rejected);
        productionMasterRepository.save(productionMaster);
        if (count == 1) {
            Date date = new Date();
            productionItemQualityCheck.setFqcRejectedOn(date);
            productionItemQualityCheck.setFqcStatus(MaterialStatus.Rejected);
            Optional<Employee> employee = employeeService.getEmployee(empid);
            productionItemQualityCheck.setFqcRejectedBy(employee.get().getFirstName() + " " + employee.get().getLastName());
            productionItemQualityCheckRepository.save(productionItemQualityCheck);
        }

        return new ResponseEntity<>(productionMaster, HttpStatus.OK);
    }

    @GetMapping("lqc-total-material-count")
    public ResponseEntity<Long> lqcTotalCount() {
        List<ProductionItemQualityCheck> productionItemQualityCheckList = productionItemQualityCheckRepository.findAll();
        long count = 0;
        count = productionItemQualityCheckList.size();
        return new ResponseEntity<>(count, HttpStatus.OK);
    }

    @GetMapping("lqc-approved-materials-count")
    public ResponseEntity<Long> lqcApprovedCount() {
        List<ProductionItemQualityCheck> productionItemQualityCheckList = productionItemQualityCheckRepository.findAll();
        List<ProductionItemQualityCheck> productionItemQualityCheckList1 = new ArrayList<>();
        long count = 0;
        for (ProductionItemQualityCheck p : productionItemQualityCheckList) {
            if (p.getLqcStatus().equals(MaterialStatus.Approved)) {
                List<ProductionMaster> productionMasters = p.getProductionMasters();
                List<ProductionMaster> productionMasters1 = new ArrayList<>();
                for (ProductionMaster m : productionMasters) {
                    if (m.getProductionLqcItemStatus() .equals( MaterialStatus.Approved)) {
                        productionMasters1.add(m);
                    }
                }
                p.setProductionMasters(productionMasters1);
                productionItemQualityCheckList1.add(p);

            }
        }
        count = productionItemQualityCheckList1.size();
        return new ResponseEntity<>(count, HttpStatus.OK);
    }

    @GetMapping("lqc-rejected-materials-count")
    public ResponseEntity<Long> lqcRejectedCount() {
        List<ProductionItemQualityCheck> productionItemQualityCheckList = productionItemQualityCheckRepository.findAll();
        List<ProductionItemQualityCheck> productionItemQualityCheckList1 = new ArrayList<>();
        long count = 0;
        for (ProductionItemQualityCheck p : productionItemQualityCheckList) {
            if (p.getLqcStatus().equals(MaterialStatus.Rejected)) {
                List<ProductionMaster> productionMasters = p.getProductionMasters();
                List<ProductionMaster> productionMasters1 = new ArrayList<>();
                for (ProductionMaster m : productionMasters) {
                    if (m.getProductionLqcItemStatus() .equals( MaterialStatus.Rejected)) {
                        productionMasters1.add(m);
                    }
                }
                p.setProductionMasters(productionMasters1);
                productionItemQualityCheckList1.add(p);
            }
        }
        count = productionItemQualityCheckList1.size();
        return new ResponseEntity<>(count, HttpStatus.OK);
    }

    @GetMapping("lqc-total-materials-report")
    public ResponseEntity<List<ProductionItemQualityCheck>> getTotalRequestReport(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, ParseException {
        Date date = new Date();
        List<ProductionItemQualityCheck> employeeRequestList = getTotalRequest();
        OutputStream out = null;
        String fileName = "Total_Request_Report";
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName + ".xls");
        WritableWorkbook workbook = jxl.Workbook.createWorkbook(response.getOutputStream());
        try {
            if (employeeRequestList != null) {
                totalRequest(workbook, employeeRequestList, response, 0);
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

    private WritableWorkbook totalRequest(WritableWorkbook workbook, List<ProductionItemQualityCheck> employeeRequestList, HttpServletResponse response, int index) throws IOException, WriteException {
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


        int rowNum = 1;
        for (ProductionItemQualityCheck employeeRequest : employeeRequestList) {
            s.addCell(new Label(0, 0, "#", cellFormat));
            s.addCell(new Label(0, rowNum, "" + Integer.valueOf(rowNum)));
            s.addCell(new Label(1, 0, "Batch Number", cellFormat));
            s.addCell(new Label(1, rowNum, "" + employeeRequest.getBatchMasters().getBatchNumber()));
            s.addCell(new Label(2, 0, "Production Area", cellFormat));
            s.addCell(new Label(2, rowNum, "" + employeeRequest.getProductionLineMasters().getProductionArea()));
            s.addCell(new Label(3, 0, "Production Line", cellFormat));
            s.addCell(new Label(3, rowNum, "" + employeeRequest.getProductionLineMasters().getProductionLineName()));
            s.addCell(new Label(4, 0, "Product Name", cellFormat));
            s.addCell(new Label(4, rowNum, "" + employeeRequest.getProductionMasters().get(0).getClientName()));
            s.addCell(new Label(5, 0, "Production Employee", cellFormat));
            s.addCell(new Label(5, rowNum, "" + employeeRequest.getProductionLineMasters().getProductionEmployee()));
            s.addCell(new Label(6, 0, "Production Date", cellFormat));
            s.addCell(new Label(6, rowNum, "" + employeeRequest.getCreatedOn()));

        }
        return workbook;
    }

    public List<ProductionItemQualityCheck> getTotalRequest() {
        List<ProductionItemQualityCheck> productionItemQualityCheckList = productionItemQualityCheckRepository.findAll();
        return productionItemQualityCheckList;
    }

    @GetMapping("lqc-Approved-materials-report")
    public ResponseEntity<List<ProductionItemQualityCheck>> getApprovedRequestReport(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, ParseException {
        Date date = new Date();
        List<ProductionItemQualityCheck> employeeRequestList = getTotalRequest1();
        OutputStream out = null;
        String fileName = "Approved_Request_Report";
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName + ".xls");
        WritableWorkbook workbook = jxl.Workbook.createWorkbook(response.getOutputStream());
        try {
            if (employeeRequestList != null) {
                totalRequest1(workbook, employeeRequestList, response, 0);
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

    private WritableWorkbook totalRequest1(WritableWorkbook workbook, List<ProductionItemQualityCheck> employeeRequestList, HttpServletResponse response, int index) throws IOException, WriteException {
        WritableSheet s = workbook.createSheet("Approved-Materials-Report", index);
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


        int rowNum = 1;
        for (ProductionItemQualityCheck employeeRequest : employeeRequestList) {
            s.addCell(new Label(0, 0, "#", cellFormat));
            s.addCell(new Label(0, rowNum, "" + Integer.valueOf(rowNum)));
            s.addCell(new Label(1, 0, "Batch Number", cellFormat));
            s.addCell(new Label(1, rowNum, "" + employeeRequest.getBatchMasters().getBatchNumber()));
            s.addCell(new Label(2, 0, "Production Area", cellFormat));
            s.addCell(new Label(2, rowNum, "" + employeeRequest.getProductionLineMasters().getProductionArea()));
            s.addCell(new Label(3, 0, "Production Line", cellFormat));
            s.addCell(new Label(3, rowNum, "" + employeeRequest.getProductionLineMasters().getProductionLineName()));
            s.addCell(new Label(4, 0, "Product Name", cellFormat));
            s.addCell(new Label(4, rowNum, "" + employeeRequest.getProductionMasters().get(0).getClientName()));
            s.addCell(new Label(5, 0, "Production Employee", cellFormat));
            s.addCell(new Label(5, rowNum, "" + employeeRequest.getProductionLineMasters().getProductionEmployee()));
            s.addCell(new Label(6, 0, "Production Date", cellFormat));
            s.addCell(new Label(6, rowNum, "" + employeeRequest.getCreatedOn()));
            s.addCell(new Label(7, 0, "Approved Date", cellFormat));
            s.addCell(new Label(7, rowNum, "" + employeeRequest.getLqcOn()));

        }
        return workbook;
    }

    public List<ProductionItemQualityCheck> getTotalRequest1() {
        List<ProductionItemQualityCheck> productionItemQualityCheckList = productionItemQualityCheckRepository.findAll();
        List<ProductionItemQualityCheck> productionItemQualityCheckList1 = new ArrayList<>();
        for (ProductionItemQualityCheck p : productionItemQualityCheckList) {
            if (p.getLqcStatus().equals(MaterialStatus.Approved)) {
                List<ProductionMaster> productionMasters = p.getProductionMasters();
                List<ProductionMaster> productionMasters1 = new ArrayList<>();
                for (ProductionMaster m : productionMasters) {
                    if (m.getProductionLqcItemStatus() == MaterialStatus.Approved) {
                        productionMasters1.add(m);
                    }
                }
                p.setProductionMasters(productionMasters1);
                productionItemQualityCheckList1.add(p);

            }
        }

        return productionItemQualityCheckList1;
    }

    @GetMapping("lqc-rejected-materials-report")
    public ResponseEntity<List<ProductionItemQualityCheck>> getRejectedRequestReport(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, ParseException {
        Date date = new Date();
        List<ProductionItemQualityCheck> employeeRequestList = getTotalRequest2();
        OutputStream out = null;
        String fileName = "Rejected_Request_Report";
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName + ".xls");
        WritableWorkbook workbook = jxl.Workbook.createWorkbook(response.getOutputStream());
        try {
            if (employeeRequestList != null) {
                totalRequest2(workbook, employeeRequestList, response, 0);
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

    private WritableWorkbook totalRequest2(WritableWorkbook workbook, List<ProductionItemQualityCheck> employeeRequestList, HttpServletResponse response, int index) throws IOException, WriteException {
        WritableSheet s = workbook.createSheet("Rejected-Materials-Report", index);
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


        int rowNum = 1;
        for (ProductionItemQualityCheck employeeRequest : employeeRequestList) {
            s.addCell(new Label(0, 0, "#", cellFormat));
            s.addCell(new Label(0, rowNum, "" + Integer.valueOf(rowNum)));
            s.addCell(new Label(1, 0, "Batch Number", cellFormat));
            s.addCell(new Label(1, rowNum, "" + employeeRequest.getBatchMasters().getBatchNumber()));
            s.addCell(new Label(2, 0, "Production Area", cellFormat));
            s.addCell(new Label(2, rowNum, "" + employeeRequest.getProductionLineMasters().getProductionArea()));
            s.addCell(new Label(3, 0, "Production Line", cellFormat));
            s.addCell(new Label(3, rowNum, "" + employeeRequest.getProductionLineMasters().getProductionLineName()));
            s.addCell(new Label(4, 0, "Product Name", cellFormat));
            s.addCell(new Label(4, rowNum, "" + employeeRequest.getProductionMasters().get(0).getClientName()));
            s.addCell(new Label(5, 0, "Production Employee", cellFormat));
            s.addCell(new Label(5, rowNum, "" + employeeRequest.getProductionLineMasters().getProductionEmployee()));
            s.addCell(new Label(6, 0, "Production Date", cellFormat));
            s.addCell(new Label(6, rowNum, "" + employeeRequest.getCreatedOn()));
            s.addCell(new Label(7, 0, "Rejected Date", cellFormat));
            s.addCell(new Label(7, rowNum, "" + employeeRequest.getLqcRejectedOn()));

        }
        return workbook;
    }

    public List<ProductionItemQualityCheck> getTotalRequest2() {
        List<ProductionItemQualityCheck> productionItemQualityCheckList = productionItemQualityCheckRepository.findAll();
        List<ProductionItemQualityCheck> productionItemQualityCheckList1 = new ArrayList<>();
        for (ProductionItemQualityCheck p : productionItemQualityCheckList) {
            if (p.getLqcStatus().equals(MaterialStatus.Rejected)) {
                List<ProductionMaster> productionMasters = p.getProductionMasters();
                List<ProductionMaster> productionMasters1 = new ArrayList<>();
                for (ProductionMaster m : productionMasters) {
                    if (m.getProductionLqcItemStatus() == MaterialStatus.Rejected) {
                        productionMasters1.add(m);
                    }
                }
                p.setProductionMasters(productionMasters1);
                productionItemQualityCheckList1.add(p);

            }
        }

        return productionItemQualityCheckList1;
    }


    @GetMapping("fqc-total-material-count")
    public ResponseEntity<Long> fqcTotalCount() {
        List<ProductionItemQualityCheck> productionItemQualityCheckList = productionItemQualityCheckRepository.findAll();
        long count = 0;
        count = productionItemQualityCheckList.size();
        return new ResponseEntity<>(count, HttpStatus.OK);
    }

    @GetMapping("fqc-approved-materials-count")
    public ResponseEntity<Long> fqcApprovedCount() {
        List<ProductionItemQualityCheck> productionItemQualityCheckList = productionItemQualityCheckRepository.findAll();
        List<ProductionItemQualityCheck> productionItemQualityCheckList1 = new ArrayList<>();
        long count = 0;
        for (ProductionItemQualityCheck p : productionItemQualityCheckList) {
            if (p.getFqcStatus().equals(MaterialStatus.Approved)) {
                List<ProductionMaster> productionMasters = p.getProductionMasters();
                List<ProductionMaster> productionMasters1 = new ArrayList<>();
                for (ProductionMaster m : productionMasters) {
                    if (m.getProductionFqcItemStatus() == MaterialStatus.Approved) {
                        productionMasters1.add(m);
                    }
                }
                p.setProductionMasters(productionMasters1);
                productionItemQualityCheckList1.add(p);

            }
        }
        count = productionItemQualityCheckList1.size();
        return new ResponseEntity<>(count, HttpStatus.OK);
    }

    @GetMapping("fqc-rejected-materials-count")
    public ResponseEntity<Long> fqcRejectedCount() {
        List<ProductionItemQualityCheck> productionItemQualityCheckList = productionItemQualityCheckRepository.findAll();
        List<ProductionItemQualityCheck> productionItemQualityCheckList1 = new ArrayList<>();
        long count = 0;
        for (ProductionItemQualityCheck p : productionItemQualityCheckList) {
            if (p.getFqcStatus().equals(MaterialStatus.Rejected)) {
                List<ProductionMaster> productionMasters = p.getProductionMasters();
                List<ProductionMaster> productionMasters1 = new ArrayList<>();
                for (ProductionMaster m : productionMasters) {
                    if (m.getProductionFqcItemStatus() == MaterialStatus.Rejected) {
                        productionMasters1.add(m);
                    }
                }
                p.setProductionMasters(productionMasters1);
                productionItemQualityCheckList1.add(p);
            }
        }
        count = productionItemQualityCheckList1.size();
        return new ResponseEntity<>(count, HttpStatus.OK);
    }

    @GetMapping("fqc-total-materials-report")
    public ResponseEntity<List<ProductionItemQualityCheck>> getTotalRequestReportFqc(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, ParseException {
        Date date = new Date();
        List<ProductionItemQualityCheck> employeeRequestList = getTotalRequestFqc();
        OutputStream out = null;
        String fileName = "Total_Request_Report";
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName + ".xls");
        WritableWorkbook workbook = jxl.Workbook.createWorkbook(response.getOutputStream());
        try {
            if (employeeRequestList != null) {
                totalRequest1(workbook, employeeRequestList, response, 0);
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

    private WritableWorkbook totalRequestFqc(WritableWorkbook workbook, List<ProductionItemQualityCheck> employeeRequestList, HttpServletResponse response, int index) throws IOException, WriteException {
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


        int rowNum = 1;
        for (ProductionItemQualityCheck employeeRequest : employeeRequestList) {
            s.addCell(new Label(0, 0, "#", cellFormat));
            s.addCell(new Label(0, rowNum, "" + Integer.valueOf(rowNum)));
            s.addCell(new Label(1, 0, "Batch Number", cellFormat));
            s.addCell(new Label(1, rowNum, "" + employeeRequest.getBatchMasters().getBatchNumber()));
            s.addCell(new Label(2, 0, "Production Area", cellFormat));
            s.addCell(new Label(2, rowNum, "" + employeeRequest.getProductionLineMasters().getProductionArea()));
            s.addCell(new Label(3, 0, "Production Line", cellFormat));
            s.addCell(new Label(3, rowNum, "" + employeeRequest.getProductionLineMasters().getProductionLineName()));
            s.addCell(new Label(4, 0, "Product Name", cellFormat));
            s.addCell(new Label(4, rowNum, "" + employeeRequest.getProductionMasters().get(0).getClientName()));
            s.addCell(new Label(5, 0, "Production Employee", cellFormat));
            s.addCell(new Label(5, rowNum, "" + employeeRequest.getProductionLineMasters().getProductionEmployee()));
            s.addCell(new Label(6, 0, "Production Date", cellFormat));
            s.addCell(new Label(6, rowNum, "" + employeeRequest.getCreatedOn()));
            s.addCell(new Label(7, 0, "Lqc Checked By", cellFormat));
            s.addCell(new Label(7, rowNum, "" + employeeRequest.getLqcBy()));
            s.addCell(new Label(8, 0, "Lqc Approved Date", cellFormat));
            s.addCell(new Label(8, rowNum, "" + employeeRequest.getLqcOn()));


        }
        return workbook;
    }

    public List<ProductionItemQualityCheck> getTotalRequestFqc() {
        Date date = new Date();
        List<ProductionItemQualityCheck> productionItemQualityCheckList = productionItemQualityCheckRepository.findAll();
        return productionItemQualityCheckList;
    }

    @GetMapping("fqc-Approved-materials-report")
    public ResponseEntity<List<ProductionItemQualityCheck>> getApprovedRequestReportFqc(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, ParseException {
        Date date = new Date();
        List<ProductionItemQualityCheck> employeeRequestList = getTotalRequestFqc1();
        OutputStream out = null;
        String fileName = "Approved_Request_Report";
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName + ".xls");
        WritableWorkbook workbook = jxl.Workbook.createWorkbook(response.getOutputStream());
        try {
            if (employeeRequestList != null) {
                totalRequestFqc1(workbook, employeeRequestList, response, 0);
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

    private WritableWorkbook totalRequestFqc1(WritableWorkbook workbook, List<ProductionItemQualityCheck> employeeRequestList, HttpServletResponse response, int index) throws IOException, WriteException {
        WritableSheet s = workbook.createSheet("Approved-Materials-Report", index);
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
        for (ProductionItemQualityCheck employeeRequest : employeeRequestList) {
            s.addCell(new Label(0, 0, "#", cellFormat));
            s.addCell(new Label(0, rowNum, "" + Integer.valueOf(rowNum)));
            s.addCell(new Label(1, 0, "Batch Number", cellFormat));
            s.addCell(new Label(1, rowNum, "" + employeeRequest.getBatchMasters().getBatchNumber()));
            s.addCell(new Label(2, 0, "Production Area", cellFormat));
            s.addCell(new Label(2, rowNum, "" + employeeRequest.getProductionLineMasters().getProductionArea()));
            s.addCell(new Label(3, 0, "Production Line", cellFormat));
            s.addCell(new Label(3, rowNum, "" + employeeRequest.getProductionLineMasters().getProductionLineName()));
            s.addCell(new Label(4, 0, "Product Name", cellFormat));
            s.addCell(new Label(4, rowNum, "" + employeeRequest.getProductionMasters().get(0).getClientName()));
            s.addCell(new Label(5, 0, "Production Employee", cellFormat));
            s.addCell(new Label(5, rowNum, "" + employeeRequest.getProductionLineMasters().getProductionEmployee()));
            s.addCell(new Label(6, 0, "Production Date", cellFormat));
            s.addCell(new Label(6, rowNum, "" + employeeRequest.getCreatedOn()));
            s.addCell(new Label(7, 0, "Lqc Checked By", cellFormat));
            s.addCell(new Label(7, rowNum, "" + employeeRequest.getLqcBy()));
            s.addCell(new Label(8, 0, "Lqc Approved Date", cellFormat));
            s.addCell(new Label(8, rowNum, "" + employeeRequest.getLqcOn()));
            s.addCell(new Label(9, 0, "Fqc Approved Date", cellFormat));
            s.addCell(new Label(9, rowNum, "" + employeeRequest.getFqcOn()));

        }
        return workbook;
    }

    public List<ProductionItemQualityCheck> getTotalRequestFqc1() {
        List<ProductionItemQualityCheck> productionItemQualityCheckList = productionItemQualityCheckRepository.findAll();
        List<ProductionItemQualityCheck> productionItemQualityCheckList1 = new ArrayList<>();
        for (ProductionItemQualityCheck p : productionItemQualityCheckList) {
            if (p.getLqcStatus().equals(MaterialStatus.Approved)) {
                List<ProductionMaster> productionMasters = p.getProductionMasters();
                List<ProductionMaster> productionMasters1 = new ArrayList<>();
                for (ProductionMaster m : productionMasters) {
                    if (m.getProductionLqcItemStatus() == MaterialStatus.Approved) {
                        productionMasters1.add(m);
                    }
                }
                p.setProductionMasters(productionMasters1);
                productionItemQualityCheckList1.add(p);

            }
        }

        return productionItemQualityCheckList1;
    }

    @GetMapping("fqc-rejected-materials-report")
    public ResponseEntity<List<ProductionItemQualityCheck>> getRejectedRequestReportFqc(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, ParseException {
        Date date = new Date();
        List<ProductionItemQualityCheck> employeeRequestList = getTotalRequestFqc2();
        OutputStream out = null;
        String fileName = "Rejected_Request_Report";
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName + ".xls");
        WritableWorkbook workbook = jxl.Workbook.createWorkbook(response.getOutputStream());
        try {
            if (employeeRequestList != null) {
                totalRequestFqc2(workbook, employeeRequestList, response, 0);
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

    private WritableWorkbook totalRequestFqc2(WritableWorkbook workbook, List<ProductionItemQualityCheck> employeeRequestList, HttpServletResponse response, int index) throws IOException, WriteException {
        WritableSheet s = workbook.createSheet("Rejected-Materials-Report", index);
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
        for (ProductionItemQualityCheck employeeRequest : employeeRequestList) {
            s.addCell(new Label(0, 0, "#", cellFormat));
            s.addCell(new Label(0, rowNum, "" + Integer.valueOf(rowNum)));
            s.addCell(new Label(1, 0, "Batch Number", cellFormat));
            s.addCell(new Label(1, rowNum, "" + employeeRequest.getBatchMasters().getBatchNumber()));
            s.addCell(new Label(2, 0, "Production Area", cellFormat));
            s.addCell(new Label(2, rowNum, "" + employeeRequest.getProductionLineMasters().getProductionArea()));
            s.addCell(new Label(3, 0, "Production Line", cellFormat));
            s.addCell(new Label(3, rowNum, "" + employeeRequest.getProductionLineMasters().getProductionLineName()));
            s.addCell(new Label(4, 0, "Product Name", cellFormat));
            s.addCell(new Label(4, rowNum, "" + employeeRequest.getProductionMasters().get(0).getClientName()));
            s.addCell(new Label(5, 0, "Production Employee", cellFormat));
            s.addCell(new Label(5, rowNum, "" + employeeRequest.getProductionLineMasters().getProductionEmployee()));
            s.addCell(new Label(6, 0, "Production Date", cellFormat));
            s.addCell(new Label(6, rowNum, "" + employeeRequest.getCreatedOn()));
            s.addCell(new Label(7, 0, "Lqc Checked By", cellFormat));
            s.addCell(new Label(7, rowNum, "" + employeeRequest.getLqcBy()));
            s.addCell(new Label(8, 0, "Lqc Approved Date", cellFormat));
            s.addCell(new Label(8, rowNum, "" + employeeRequest.getLqcOn()));
            s.addCell(new Label(9, 0, "Fqc Rejected Date", cellFormat));
            s.addCell(new Label(9, rowNum, "" + employeeRequest.getFqcRejectedOn()));


        }
        return workbook;
    }

    public List<ProductionItemQualityCheck> getTotalRequestFqc2() {
        List<ProductionItemQualityCheck> productionItemQualityCheckList = productionItemQualityCheckRepository.findAll();
        List<ProductionItemQualityCheck> productionItemQualityCheckList1 = new ArrayList<>();
        for (ProductionItemQualityCheck p : productionItemQualityCheckList) {
            if (p.getLqcStatus().equals(MaterialStatus.Rejected)) {
                List<ProductionMaster> productionMasters = p.getProductionMasters();
                List<ProductionMaster> productionMasters1 = new ArrayList<>();
                for (ProductionMaster m : productionMasters) {
                    if (m.getProductionLqcItemStatus() == MaterialStatus.Rejected) {
                        productionMasters1.add(m);
                    }
                }
                p.setProductionMasters(productionMasters1);
                productionItemQualityCheckList1.add(p);

            }
        }

        return productionItemQualityCheckList1;
    }


    @GetMapping("products/template-download")
    private void createItemDetails(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        OutputStream out = null;
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment; filename= product.xlsx");
        WritableWorkbook workbook = jxl.Workbook.createWorkbook(response.getOutputStream());
        try {
            writeVehicleToSheet(workbook, response, 0);
            workbook.write();
            workbook.close();
        } catch (Exception e) {
            throw new ServletException("Exception in Excel download", e);
        } finally {
            if (out != null)
                out.close();
        }
    }


    private WritableWorkbook writeVehicleToSheet(WritableWorkbook workbook, HttpServletResponse response, int index) throws IOException, WriteException {
        WritableSheet s = workbook.createSheet("Data Input", index);
        s.getSettings().setPrintGridLines(false);

        WritableFont headerFont = new WritableFont(WritableFont.TIMES, 14);
        headerFont.setBoldStyle(WritableFont.BOLD);
        WritableCellFormat headerFormat = new WritableCellFormat(headerFont);
        headerFormat.setAlignment(CENTRE);
        headerFormat.setBackground(Colour.GRAY_25);

        s.setColumnView(0, 10);
        s.setColumnView(1, 20);
        s.setColumnView(2, 20);
        s.setColumnView(3, 20);
        s.setColumnView(4, 20);

        s.addCell(new Label(0, 0, "#", headerFormat));
        s.addCell(new Label(1, 0, "Serial Number", headerFormat));
        return workbook;
    }

    @PostMapping("product-bulk-upload/{pName}")
    public ResponseEntity<List<ProductionMaster>> ItemsBulkUpload(@PathVariable("pName") String pName, @RequestParam("file") MultipartFile file) throws Exception {
        XSSFWorkbook wb = new XSSFWorkbook(file.getInputStream());
        XSSFSheet sheet = wb.getSheetAt(0);
        List<ProductionMaster> productionMasterList = new ArrayList<>();
        for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {
            ProductionMaster productionMaster = new ProductionMaster();
            XSSFRow row = sheet.getRow(i);
            productionMaster.setSerialNumber(row.getCell(1).getNumericCellValue());
            productionMaster.setProductionName(pName);
            productionMaster.setProductionFqcItemStatus(MaterialStatus.New);
            productionMaster.setProductionLqcItemStatus(MaterialStatus.New);
            productionMasterList.add(productionMaster);
        }
        return new ResponseEntity<>(productionMasterList, HttpStatus.OK);
    }

    @PostMapping("product-save")
    public ResponseEntity<ProductionItemQualityCheck> saveQuality(@RequestBody ProductionItemQualityCheckDto productionItemQualityCheckDto) {
        ProductionItemQualityCheck productionItemQualityCheck = new ProductionItemQualityCheck();
        Optional<Employee> employee = employeeService.getEmployee(productionItemQualityCheckDto.getEmployeeId());
        productionItemQualityCheck.setProductionEmployee(employee.get().getFirstName() + " " + employee.get().getLastName());
        BatchMaster batchMaster = batchMasterRepository.findOne(productionItemQualityCheckDto.getBatchMaster());
        productionItemQualityCheck.setBatchMasters(batchMaster);
        ProductionLineMasters productionLineMasters = productionLineMastersRepository.findOne(productionItemQualityCheckDto.getProductionLineMasters());
        productionItemQualityCheck.setProductionLineMasters(productionLineMasters);
        productionItemQualityCheck.setProductionMasters(productionItemQualityCheckDto.getProductionMasters());
        productionItemQualityCheck.setFqcStatus(MaterialStatus.New);
        productionItemQualityCheck.setLqcStatus(MaterialStatus.New);
        Factorys factorys = factroysRepository.findOne(productionItemQualityCheckDto.getFactorys());
        productionItemQualityCheck.setFactorys(factorys);
        Date date = new Date();
        productionItemQualityCheck.setCreatedOn(date);
        Shift shift = shiftRepository.findOne(productionItemQualityCheckDto.getShift());
        productionItemQualityCheck.setShifts(shift);
        productionItemQualityCheckRepository.save(productionItemQualityCheck);

        return new ResponseEntity<>(productionItemQualityCheck, HttpStatus.CREATED);
    }

    @GetMapping("get-production-upload")
    public ResponseEntity<List<ProductionItemQualityCheck>> getProductionUpload() {
        List<ProductionItemQualityCheck> productionItemQualityCheckList = productionItemQualityCheckRepository.findAll();

        return new ResponseEntity<>(productionItemQualityCheckList, HttpStatus.OK);
    }

    @PostMapping("get-lqc-approve-reject-filter")
    public ResponseEntity<List<ProductionItemQualityCheck>> getProductDates(@RequestBody FilterDateDto filterDateDto) {
        List<ProductionItemQualityCheck> productionItemQualityCheckList = productionItemQualityCheckRepository.getProductionBetweenDate(filterDateDto.getStartDate(), filterDateDto.getEndDate());
        List<ProductionItemQualityCheck> productionItemQualityCheckList1 = new ArrayList<>();
        for (ProductionItemQualityCheck p : productionItemQualityCheckList) {
            if (p.getLqcOn() != null) {
                productionItemQualityCheckList1.add(p);
            }

        }
        return new ResponseEntity<>(productionItemQualityCheckList1, HttpStatus.OK);
    }


    @PostMapping("lqc-approve-reject-filter-report")
    public ResponseEntity<List<ProductionItemQualityCheck>> getProductionReport(@RequestBody FilterDateDto filterDateDto, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, ParseException {
        Date date = new Date();
        List<ProductionItemQualityCheck> productionItemQualityCheckList = getProductBetweenDates(filterDateDto.getStartDate(), filterDateDto.getEndDate());
        OutputStream out = null;
        String fileName = "Total_Request_Report";
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName + ".xls");
        WritableWorkbook workbook = jxl.Workbook.createWorkbook(response.getOutputStream());
        try {
            if (productionItemQualityCheckList != null) {
                totalRequest11(workbook, productionItemQualityCheckList, response, 0);
            }
            if (productionItemQualityCheckList.isEmpty())
                throw new EntityNotFoundException("entries");
            workbook.write();
            workbook.close();
        } catch (Exception e) {
            throw new ServletException("Exception in excel download", e);
        } finally {
            if (out != null)
                out.close();
        }
        return new ResponseEntity(productionItemQualityCheckList, HttpStatus.OK);
    }


    private WritableWorkbook totalRequest11(WritableWorkbook workbook, List<ProductionItemQualityCheck> employeeRequestList, HttpServletResponse response, int index) throws IOException, WriteException {
        WritableSheet s = workbook.createSheet("Approve-Reject-Report", index);
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


        int rowNum = 1;
        for (ProductionItemQualityCheck employeeRequest : employeeRequestList) {
            s.addCell(new Label(0, 0, "#", cellFormat));
            s.addCell(new Label(0, rowNum, "" + Integer.valueOf(rowNum)));
            s.addCell(new Label(1, 0, "Production Line", cellFormat));
            s.addCell(new Label(1, rowNum, "" + employeeRequest.getProductionLineMasters().getProductionLineName()));
            s.addCell(new Label(2, 0, "Product Name", cellFormat));
            s.addCell(new Label(2, rowNum, "" + employeeRequest.getProductionMasters().get(0).getProductionName()));
            s.addCell(new Label(3, 0, "Batch Number", cellFormat));
            s.addCell(new Label(3, rowNum, "" + employeeRequest.getBatchMasters().getBatchNumber()));
            s.addCell(new Label(4, 0, "Production Date", cellFormat));
            s.addCell(new Label(4, rowNum, "" + employeeRequest.getCreatedOn()));
            s.addCell(new Label(5, 0, "Shift", cellFormat));
            s.addCell(new Label(5, rowNum, "" + employeeRequest.getShifts().getName()));


            int colNum = 6;
            List<ProductionMaster> productionMasterList = employeeRequest.getProductionMasters();
            int count = 0;
            int c = rowNum;
            for (ProductionMaster met : productionMasterList) {
                s.addCell(new Label(6, 0, "Item Serial Number", cellFormat));
                s.addCell(new Label(colNum, c, "" + met.getSerialNumber()));
                int a = colNum + 1;
                s.addCell(new Label(7, 0, "Production Name", cellFormat));
                s.addCell(new Label(a, c, "" + met.getProductionName()));
                int b = colNum + 2;
                s.addCell(new Label(8, 0, "Quantity", cellFormat));
                s.addCell(new Label(b, c, "" + met.getClientName()));
                c = rowNum + 1;
                count = count + 1;
            }

            rowNum = count + 1;


        }
        return workbook;
    }


    public List<ProductionItemQualityCheck> getProductBetweenDates(Date startDate, Date endDate) {
        List<ProductionItemQualityCheck> productionItemQualityCheckList = productionItemQualityCheckRepository.getProductionBetweenDate(startDate, endDate);
        List<ProductionItemQualityCheck> productionItemQualityCheckList1 = new ArrayList<>();
        for (ProductionItemQualityCheck p : productionItemQualityCheckList) {
            if (p.getLqcOn() != null) {
                productionItemQualityCheckList1.add(p);
            }

        }
        return productionItemQualityCheckList1;
    }

    @PostMapping("get-fqc-approve-reject-filter")
    public ResponseEntity<List<ProductionItemQualityCheck>> getFqcProductDates(@RequestBody FilterDateDto filterDateDto) {
        List<ProductionItemQualityCheck> productionItemQualityCheckList = productionItemQualityCheckRepository.getProductionBetweenDate(filterDateDto.getStartDate(), filterDateDto.getEndDate());
        List<ProductionItemQualityCheck> productionItemQualityCheckList1 = new ArrayList<>();
        for (ProductionItemQualityCheck p : productionItemQualityCheckList) {
            if (p.getFqcOn() != null) {
                productionItemQualityCheckList1.add(p);
            }

        }
        return new ResponseEntity<>(productionItemQualityCheckList1, HttpStatus.OK);
    }

    @PostMapping("fqc-approve-reject-filter-report")
    public ResponseEntity<List<ProductionItemQualityCheck>> getFqcProductionReport(@RequestBody FilterDateDto filterDateDto, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, ParseException {
        Date date = new Date();
        List<ProductionItemQualityCheck> productionItemQualityCheckList = getFqcProductBetweenDates(filterDateDto.getStartDate(), filterDateDto.getEndDate());
        OutputStream out = null;
        String fileName = "Total_Request_Report";
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName + ".xls");
        WritableWorkbook workbook = jxl.Workbook.createWorkbook(response.getOutputStream());
        try {
            if (productionItemQualityCheckList != null) {
                totalRequest12(workbook, productionItemQualityCheckList, response, 0);
            }
            if (productionItemQualityCheckList.isEmpty())
                throw new EntityNotFoundException("entries");
            workbook.write();
            workbook.close();
        } catch (Exception e) {
            throw new ServletException("Exception in excel download", e);
        } finally {
            if (out != null)
                out.close();
        }
        return new ResponseEntity(productionItemQualityCheckList, HttpStatus.OK);
    }


    private WritableWorkbook totalRequest12(WritableWorkbook workbook, List<ProductionItemQualityCheck> employeeRequestList, HttpServletResponse response, int index) throws IOException, WriteException {
        WritableSheet s = workbook.createSheet("Approve-Reject-Report", index);
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


        int rowNum = 1;
        for (ProductionItemQualityCheck employeeRequest : employeeRequestList) {
            s.addCell(new Label(0, 0, "#", cellFormat));
            s.addCell(new Label(0, rowNum, "" + Integer.valueOf(rowNum)));
            s.addCell(new Label(1, 0, "Production Line", cellFormat));
            s.addCell(new Label(1, rowNum, "" + employeeRequest.getProductionLineMasters().getProductionLineName()));
            s.addCell(new Label(2, 0, "Product Name", cellFormat));
            s.addCell(new Label(2, rowNum, "" + employeeRequest.getProductionMasters().get(0).getProductionName()));
            s.addCell(new Label(3, 0, "Batch Number", cellFormat));
            s.addCell(new Label(3, rowNum, "" + employeeRequest.getBatchMasters().getBatchNumber()));
            s.addCell(new Label(4, 0, "Production Date", cellFormat));
            s.addCell(new Label(4, rowNum, "" + employeeRequest.getCreatedOn()));
            s.addCell(new Label(5, 0, "Shift", cellFormat));
            s.addCell(new Label(5, rowNum, "" + employeeRequest.getShifts().getName()));
            s.addCell(new Label(6, 0, "Lqc On", cellFormat));
            s.addCell(new Label(6, rowNum, "" + employeeRequest.getLqcOn()));
            s.addCell(new Label(7, 0, "Lqc By", cellFormat));
            s.addCell(new Label(7, rowNum, "" + employeeRequest.getLqcBy()));


            int colNum = 8;
            List<ProductionMaster> productionMasterList = employeeRequest.getProductionMasters();
            int count = 0;
            int c = rowNum;
            for (ProductionMaster met : productionMasterList) {
                s.addCell(new Label(8, 0, "Item Serial Number", cellFormat));
                s.addCell(new Label(colNum, c, "" + met.getSerialNumber()));
                int a = colNum + 1;
                s.addCell(new Label(9, 0, "Production Name", cellFormat));
                s.addCell(new Label(a, c, "" + met.getProductionName()));
                int b = colNum + 2;
                s.addCell(new Label(10, 0, "Quantity", cellFormat));
                s.addCell(new Label(b, c, "" + met.getClientName()));
                c = rowNum + 1;
                count = count + 1;
            }

            rowNum = count + 1;


        }
        return workbook;
    }

    public List<ProductionItemQualityCheck> getFqcProductBetweenDates(Date startDate, Date endDate) {
        List<ProductionItemQualityCheck> productionItemQualityCheckList = productionItemQualityCheckRepository.getProductionBetweenDate(startDate, endDate);
        List<ProductionItemQualityCheck> productionItemQualityCheckList1 = new ArrayList<>();
        for (ProductionItemQualityCheck p : productionItemQualityCheckList) {
            if (p.getFqcOn() != null) {
                productionItemQualityCheckList1.add(p);
            }

        }
        return productionItemQualityCheckList1;
    }


}
