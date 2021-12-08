package com.dfq.coeffi.excel;

import com.dfq.coeffi.controller.BaseController;
import com.dfq.coeffi.controller.leave.leavebalance.AvailLeave;
import com.dfq.coeffi.controller.leave.leavebalance.ClosingLeave;
import com.dfq.coeffi.controller.leave.leavebalance.OpeningLeave;
import com.dfq.coeffi.controller.leave.leavebalance.earningleaverule.EarningLeaveRule;
import com.dfq.coeffi.controller.leave.leavebalance.earningleaverule.EarningLeaveRuleService;
import com.dfq.coeffi.controller.leave.leavebalance.employeeleavebalance.EmployeeLeaveBalance;
import com.dfq.coeffi.controller.leave.leavebalance.employeeleavebalance.EmployeeLeaveBalanceService;
import com.dfq.coeffi.employeePermanentContract.entities.EmpPermanentContract;
import com.dfq.coeffi.employeePermanentContract.services.PermanentContractService;
import com.dfq.coeffi.entity.hr.employee.Employee;
import com.dfq.coeffi.entity.hr.employee.EmployeeType;
import com.dfq.coeffi.entity.leave.LeaveType;
import com.dfq.coeffi.entity.master.AcademicYear;
import com.dfq.coeffi.repository.leave.AvailLeaveRepo;
import com.dfq.coeffi.repository.leave.ClosingLeaveRepo;
import com.dfq.coeffi.repository.leave.OpeningLeaveRepo;
import com.dfq.coeffi.service.AcademicYearService;
import com.dfq.coeffi.service.hr.EmployeeService;
import com.dfq.coeffi.visitor.Entities.Visitor;
import com.dfq.coeffi.visitor.Entities.VisitorPass;
import com.dfq.coeffi.visitor.Services.VisitorPassService;
import com.dfq.coeffi.visitor.Services.VisitorService;
import jxl.CellType;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;

@RestController
@Slf4j
public class ImportEmployeeMaster extends BaseController {

    @Autowired
    PermanentContractService permanentContractService;
    @Autowired
    EmployeeLeaveBalanceService employeeLeaveBalanceService;
    @Autowired
    EarningLeaveRuleService earningLeaveRuleService;
    @Autowired
    AvailLeaveRepo availLeaveRepo;
    @Autowired
    ClosingLeaveRepo closingLeaveRepo;
    @Autowired
    OpeningLeaveRepo openingLeaveRepo;
    @Autowired
    EmployeeService employeeService;
    @Autowired
    AcademicYearService academicYearService;

    @PostMapping("import-employee-master")
    public void importContract(@RequestParam("file") MultipartFile file) {
//        List<Employee> employees = employeeImport(file);
//        contractImport(file);
        contractImportAstra(file);
        employeeImportAstra(file);
//        visitorsImportAstra(file);
    }


    /**
     * Employee more data import
     * @param file
     * @return
     */
    public List<Employee> employeeImport(MultipartFile file) {
        List<Employee> dto = new ArrayList<>();
        try {
            Workbook workbook = new XSSFWorkbook(file.getInputStream());
            Sheet sheet = workbook.getSheetAt(5);
            int rowNumber = sheet.getPhysicalNumberOfRows();
            if (rowNumber > 1) {
                for (int i = 1; i < rowNumber; i++) {
                    Row row = sheet.getRow(i);
                    Employee employee = new Employee();

                    String employeeCode = String.valueOf((long) row.getCell(2).getNumericCellValue());
                    if (employeeCode != null || employeeCode != "") {

                        employee.setEmployeeCode(employeeCode);
                        Optional<Employee> employeeCheck = employeeService.getEmployeeByEmployeeCode(employee.getEmployeeCode());
                        if (!employeeCheck.isPresent()) {

                            if (row.getCell(1).getCellType() == Cell.CELL_TYPE_NUMERIC) {
                                employee.setUanNumber(String.valueOf((long) row.getCell(1).getNumericCellValue()));
                            }
                            employee.setFirstName(row.getCell(3).getStringCellValue());
                            employee.setLastName("");
                            EmployeeType type = null;
                            if (row.getCell(4).getStringCellValue().equalsIgnoreCase("WORKER")) {
                                type = EmployeeType.PERMANENT_WORKER;
                            } else if (row.getCell(4).getStringCellValue().equalsIgnoreCase("Staff")) {
                                type = EmployeeType.PERMANENT;
                            }


                            System.out.println(row.getCell(5).getCellType());
                            if (row.getCell(5).getCellType() == Cell.CELL_TYPE_STRING || row.getCell(5).getCellType() == Cell.CELL_TYPE_ERROR) {
//                                if (row.getCell(5).getStringCellValue().equalsIgnoreCase("#N/A")) {
                                employee.setDateOfBirth(null);
//                                }
                            } else {
                                System.out.println("dateofj: " + row.getCell(5).getDateCellValue());
                                employee.setDateOfBirth(row.getCell(5).getDateCellValue());
                            }

                            if (row.getCell(6).getCellType() == Cell.CELL_TYPE_STRING) {
                                employee.setMaritalStatus(row.getCell(6).getStringCellValue());
                            }

                            if (row.getCell(7).getCellType() == Cell.CELL_TYPE_NUMERIC) {
                                long phno = (long) row.getCell(7).getNumericCellValue();
                                employee.setPhoneNumber(String.valueOf(phno));
                            }

                            if (row.getCell(9).getCellType() == Cell.CELL_TYPE_STRING) {
                                employee.setFatherName(row.getCell(9).getStringCellValue());
                            }
                            if (row.getCell(12).getCellType() == Cell.CELL_TYPE_STRING) {
                                employee.setReligion(row.getCell(12).getStringCellValue());
                            }
                            if (row.getCell(13).getCellType() == Cell.CELL_TYPE_STRING) {
                                employee.setGender(row.getCell(13).getStringCellValue());
                            }

//                            employee.setDateOfJoining(row.getCell(14).getDateCellValue());

                            System.out.println(row.getCell(14).getCellType());
                            if (row.getCell(14).getCellType() == Cell.CELL_TYPE_STRING || row.getCell(14).getCellType() == Cell.CELL_TYPE_ERROR) {
//                                if (row.getCell(5).getStringCellValue().equalsIgnoreCase("#N/A")) {
                                employee.setDateOfJoining(null);
//                                }
                            } else {
                                System.out.println("dateofj: " + row.getCell(14).getDateCellValue());
                                employee.setDateOfJoining(row.getCell(14).getDateCellValue());
                            }


                            System.out.println("depart : " + row.getCell(17).getCellType());
                            if (row.getCell(17).getCellType() == Cell.CELL_TYPE_STRING) {
                                System.out.println("dep set " + row.getCell(17).getStringCellValue());
                                employee.setDepartmentName(row.getCell(17).getStringCellValue());
                            }
                            employee.setContractCompany("");
                            employee.setEmployeeType(type);
                            employee.setEsiNumber("0");
                            System.out.println("Done");
                            employeeService.save(employee);
                            allocateLeave(employee);
                            dto.add(employee);

                        } else {
                            System.out.println("Entry exists");
                        }
                    }


                }
            } else {
                throw new NoSuchFieldException("No data in excel sheet\", \"Import");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dto;
    }

    /**
     * Contract and employee for Astra and lim data
     * @param file
     * @return
     */
    public List<EmpPermanentContract> employeeImportAstra(MultipartFile file) {
        List<EmpPermanentContract> dto = new ArrayList<>();
        try {
            Workbook workbook = new XSSFWorkbook(file.getInputStream());
            Sheet sheet = workbook.getSheetAt(1);
            int rowNumber = sheet.getPhysicalNumberOfRows();
            if (rowNumber > 1) {
                for (int i = 1; i < rowNumber; i++) {
                    Row row = sheet.getRow(i);
                    EmpPermanentContract employee = new EmpPermanentContract();

//                    String employeeCode = String.valueOf((long) row.getCell(0).getNumericCellValue());
                    String employeeCode =  row.getCell(0).getStringCellValue();
                    if (employeeCode != null || employeeCode != "") {

                        employee.setEmployeeCode(employeeCode);
                        EmpPermanentContract employeeCheck = permanentContractService.get(employee.getEmployeeCode());
                        if (employeeCheck == null) {
                            employee.setFirstName(row.getCell(1).getStringCellValue());
                            employee.setLastName("");
                            employee.setCardId( String.valueOf((long)row.getCell(2).getNumericCellValue()));
                            employee.setDepartmentName(row.getCell(3).getStringCellValue());
                            EmployeeType type = EmployeeType.PERMANENT_CONTRACT;
                            employee.setEmployeeType(type);
                            employee.setContractCompany(row.getCell(5).getStringCellValue());
                            employee.setLocation("");


                            permanentContractService.save(employee);
                            dto.add(employee);

                        } else {
                            System.out.println("Entry exists");
                            employee = employeeCheck;
                            employee.setFirstName(row.getCell(1).getStringCellValue());
                            employee.setLastName("");
                            employee.setCardId( String.valueOf((long)row.getCell(2).getNumericCellValue()));
                            EmployeeType type = EmployeeType.PERMANENT_CONTRACT;
                            employee.setEmployeeType(type);
                            employee.setContractCompany(row.getCell(5).getStringCellValue());
                            employee.setLocation("");
                            employee.setDepartmentName(row.getCell(3).getStringCellValue());
                            permanentContractService.save(employee);
                            dto.add(employee);
                        }
                    }


                }
            } else {
                throw new NoSuchFieldException("No data in excel sheet\", \"Import");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dto;
    }

    public List<EmpPermanentContract> contractImportAstra(MultipartFile file) {
        List<EmpPermanentContract> dto = new ArrayList<>();
        try {
            Workbook workbook = new XSSFWorkbook(file.getInputStream());
            Sheet sheet = workbook.getSheetAt(0);
            int rowNumber = sheet.getPhysicalNumberOfRows();
            if (rowNumber > 1) {
                for (int i = 1; i < rowNumber; i++) {
                    Row row = sheet.getRow(i);
                    EmpPermanentContract employee = new EmpPermanentContract();

//                    String employeeCode = String.valueOf((long) row.getCell(0).getNumericCellValue());
                    String employeeCode =  row.getCell(0).getStringCellValue();
                    if (employeeCode != null || employeeCode != "") {

                        employee.setEmployeeCode(employeeCode);
                        EmpPermanentContract employeeCheck = permanentContractService.get(employee.getEmployeeCode());
                        if (employeeCheck == null) {
                            employee.setFirstName(row.getCell(1).getStringCellValue());
                            employee.setLastName("");
                            employee.setCardId( String.valueOf((long)row.getCell(2).getNumericCellValue()));
                            EmployeeType type = EmployeeType.CONTRACT;
                            employee.setEmployeeType(type);
                            employee.setContractCompany(row.getCell(5).getStringCellValue());
                            employee.setLocation("");
                            employee.setDepartmentName(row.getCell(3).getStringCellValue());

                            permanentContractService.save(employee);
                            dto.add(employee);

                        } else {
                            System.out.println("Entry exists");
                            employee = employeeCheck;
                            employee.setFirstName(row.getCell(1).getStringCellValue());
                            employee.setLastName("");
                            employee.setCardId( String.valueOf((long)row.getCell(2).getNumericCellValue()));
                            EmployeeType type = EmployeeType.CONTRACT;
                            employee.setEmployeeType(type);
                            employee.setContractCompany(row.getCell(5).getStringCellValue());
                            employee.setLocation("");
                            employee.setDepartmentName(row.getCell(3).getStringCellValue());
                            permanentContractService.save(employee);
                            dto.add(employee);
                        }
                    }
                }
            } else {
                throw new NoSuchFieldException("No data in excel sheet\", \"Import");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dto;
    }

    @Autowired
    VisitorPassService visitorPassService;
    public List<VisitorPass> visitorsImportAstra(MultipartFile file) {
        List<VisitorPass> dto = new ArrayList<>();
        try {
            Workbook workbook = new XSSFWorkbook(file.getInputStream());
            Sheet sheet = workbook.getSheetAt(1);
            int rowNumber = sheet.getPhysicalNumberOfRows();
            if (rowNumber > 1) {
                for (int i = 1; i < rowNumber; i++) {
                    Row row = sheet.getRow(i);
                    VisitorPass employee = new VisitorPass();

//                    String employeeCode = String.valueOf((long) row.getCell(0).getNumericCellValue());
                    String employeeCode =  row.getCell(0).getStringCellValue();
                    if (employeeCode != null || employeeCode != "") {

                        employee.setMobileNumber(employeeCode);
                        VisitorPass employeeCheck = visitorPassService.getByMobileNumber(employee.getMobileNumber());
                        if (employeeCheck == null) {
                            employee.setFirstName(row.getCell(1).getStringCellValue());
                            employee.setLastName("");
                            employee.setRfid( String.valueOf((long)row.getCell(2).getNumericCellValue()));
                            employee.setDepartmentName(row.getCell(3).getStringCellValue());
                            employee.setVisitType(row.getCell(4).getStringCellValue());
                            employee.setCompanyName(row.getCell(5).getStringCellValue());
                            employee.setVisitorLocation("");
                            visitorPassService.save(employee);
                            dto.add(employee);

                        } else {
                            System.out.println("Entry exists");
                            employee.setFirstName(row.getCell(1).getStringCellValue());
                            employee.setLastName("");
                            employee.setRfid( String.valueOf((long)row.getCell(2).getNumericCellValue()));
                            employee.setDepartmentName(row.getCell(3).getStringCellValue());
                            employee.setVisitType(row.getCell(4).getStringCellValue());
                            employee.setCompanyName(row.getCell(5).getStringCellValue());
                            employee.setVisitorLocation("");
                            visitorPassService.save(employee);
                            dto.add(employee);
                        }
                    }
                }
            } else {
                throw new NoSuchFieldException("No data in excel sheet\", \"Import");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dto;
    }


    public List<EmpPermanentContract> contractImport(MultipartFile file) {
        List<EmpPermanentContract> dto = new ArrayList<>();
        try {
            Workbook workbook = new XSSFWorkbook(file.getInputStream());
            Sheet sheet = workbook.getSheetAt(1);
            int rowNumber = sheet.getPhysicalNumberOfRows();
            if (rowNumber > 1) {
                for (int i = 1; i < rowNumber; i++) {
                    Row row = sheet.getRow(i);
                    EmpPermanentContract employee = new EmpPermanentContract();

                    String employeeCode = String.valueOf((long) row.getCell(0).getNumericCellValue());
                    if (employeeCode != null || employeeCode != "") {

                        employee.setEmployeeCode(employeeCode);
                        EmpPermanentContract employeeCheck = permanentContractService.get(employee.getEmployeeCode());
                        if (employeeCheck == null) {
                            String accId = String.valueOf((long) row.getCell(1).getNumericCellValue());
                            employee.setAccessId(accId);
                            employee.setFirstName(row.getCell(2).getStringCellValue());
                            employee.setLastName("");
                            EmployeeType type = EmployeeType.CONTRACT;
                            employee.setEmployeeType(type);
                            employee.setContractCompany(row.getCell(5).getStringCellValue());
                            employee.setLocation("Bangalore");
                            employee.setDepartmentName("Production");
                            employee.setDateOfBirth(row.getCell(11).getDateCellValue());
                            employee.setDateOfJoining(row.getCell(13).getDateCellValue());
                            employee.setGender(row.getCell(22).getStringCellValue());
                            employee.setFatherName(row.getCell(23).getStringCellValue());

                            permanentContractService.save(employee);
                            dto.add(employee);

                        } else {
                            System.out.println("Entry exists");
                            employee = employeeCheck;
                            String accId = String.valueOf((long) row.getCell(1).getNumericCellValue());
                            employee.setAccessId(accId);
                            employee.setFirstName(row.getCell(2).getStringCellValue());
                            employee.setLastName("");
                            EmployeeType type = EmployeeType.CONTRACT;
                            employee.setEmployeeType(type);
                            employee.setContractCompany(row.getCell(5).getStringCellValue());
                            employee.setLocation("Bangalore");
                            employee.setDepartmentName("Production");
                            System.out.println("depContr : "+row.getCell(11).getCellType());
                            if (row.getCell(11).getCellType() == Cell.CELL_TYPE_STRING){
                                System.out.println(row.getCell(11).getStringCellValue());
                            }
//                            if (row.getCell(11).getCellType() != Cell.CELL_TYPE_STRING || row.getCell(11).getCellType() != Cell.CELL_TYPE_ERROR) {
//                                employee.setDateOfBirth(row.getCell(11).getDateCellValue());
//                            }
                            if (row.getCell(13).getCellType() != Cell.CELL_TYPE_STRING || row.getCell(13).getCellType() != Cell.CELL_TYPE_ERROR) {
                                employee.setDateOfJoining(row.getCell(13).getDateCellValue());
                            }
                            employee.setGender(row.getCell(22).getStringCellValue());
                            employee.setFatherName(row.getCell(23).getStringCellValue());

                            permanentContractService.save(employee);
                            dto.add(employee);
                        }
                    }


                }
            } else {
                throw new NoSuchFieldException("No data in excel sheet\", \"Import");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dto;
    }

    public void allocateLeave(Employee employee) {
        System.out.println("FRESH YEAR ENTRY");
        long currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1;
        EmployeeLeaveBalance employeeLeaveBalances = new EmployeeLeaveBalance();
        Optional<AcademicYear> academicYearOptional = academicYearService.getActiveAcademicYear();
        employeeLeaveBalances.setEmployee(employee);
        employeeLeaveBalances.setAcademicYear(academicYearOptional.get());
        employeeLeaveBalances.setStatus(true);
        OpeningLeave openingLeave = new OpeningLeave();
        ClosingLeave closingLeave = new ClosingLeave();
        AvailLeave availLeave = new AvailLeave();

        List<EarningLeaveRule> earningLeaveRuleList = earningLeaveRuleService.getAllEarningLeaveRule();
        for (EarningLeaveRule earningLeaveRule : earningLeaveRuleList) {
            if (earningLeaveRule.getLeaveType().equals(LeaveType.EARN_LEAVE)) {
                openingLeave.setEarnLeave(new BigDecimal(10));
                closingLeave.setEarnLeave(new BigDecimal(10));
            } else {
                openingLeave.setEarnLeave(new BigDecimal(10));
                closingLeave.setEarnLeave(new BigDecimal(10));
            }

            if (earningLeaveRule.getLeaveType().equals(LeaveType.MEDICAL_LEAVE)) {

                openingLeave.setMedicalLeave(new BigDecimal(10));
                closingLeave.setMedicalLeave(new BigDecimal(10));


            } else {

                openingLeave.setMedicalLeave(new BigDecimal(10));
                closingLeave.setMedicalLeave(new BigDecimal(10));

            }

            openingLeave.setClearanceLeave(new BigDecimal(0));
            closingLeave.setClearanceLeave(new BigDecimal(0));
            availLeave.setEarnLeave(new BigDecimal(0));
            availLeave.setMedicalLeave(new BigDecimal(0));
            availLeave.setClearanceLeave(new BigDecimal(0));
            availLeave.setTotalLeave(new BigDecimal(0));


            openingLeave.setTotalLeave(openingLeave.getEarnLeave().add(openingLeave.getClearanceLeave().add(openingLeave.getMedicalLeave())));
            closingLeave.setTotalLeave(openingLeave.getEarnLeave().add(openingLeave.getClearanceLeave().add(openingLeave.getMedicalLeave())));

            availLeaveRepo.save(availLeave);
            openingLeaveRepo.save(openingLeave);
            closingLeaveRepo.save(closingLeave);
            employeeLeaveBalances.setAvailLeave(availLeave);
            employeeLeaveBalances.setOpeningLeave(openingLeave);
            employeeLeaveBalances.setClosingLeave(closingLeave);
            employeeLeaveBalances.setCurrentMonth(currentMonth);
            employeeLeaveBalanceService.createEmployeeLeaveBalance(employeeLeaveBalances);
        }
    }

//    public List<EmpPermanentContract> contractImport(MultipartFile file) {
//        List<EmpPermanentContract> dto = new ArrayList<>();
//        try {
//            Workbook workbook = new XSSFWorkbook(file.getInputStream());
//            Sheet sheet = workbook.getSheetAt(0);
//            int rowNumber = sheet.getPhysicalNumberOfRows();
//            if (rowNumber > 1) {
//                for (int i = 1; i < rowNumber; i++) {
//                    Row row = sheet.getRow(i);
//                    EmpPermanentContract employee = new EmpPermanentContract();
//                    if(row.getCell(0).getStringCellValue()!="") {
//                        employee.setEmployeeCode(String.valueOf(row.getCell(0).getStringCellValue()));
//                        EmpPermanentContract employeeCheck = permanentContractService.get(employee.getEmployeeCode());
//                        if (employeeCheck == null) {
//                            employee.setFirstName(row.getCell(1).getStringCellValue());
//                            employee.setLastName(row.getCell(2).getStringCellValue());
//                            employee.setAccessId(String.valueOf((long) row.getCell(3).getNumericCellValue()));
//                            employee.setDepartmentName(row.getCell(4).getStringCellValue());
//                            employee.setContractCompany(row.getCell(5).getStringCellValue());
//                            String employeeType = row.getCell(6).getStringCellValue();
//                            EmployeeType type = null;
//                            if (employeeType.equalsIgnoreCase("TEMPORARY_CONTRACT")) {
//                                type = EmployeeType.CONTRACT;
//                            } else if (employeeType.equalsIgnoreCase("PERMANENT_CONTRACT")) {
//                                type = EmployeeType.PERMANENT_CONTRACT;
//                            }
//                            employee.setEmployeeType(type);
//                            permanentContractService.save(employee);
//                            dto.add(employee);
//                        } else {
//                            System.out.println("Entry exists");
//                        }
//                    }
//                }
//            } else {
//                throw new NoSuchFieldException("No data in excel sheet\", \"Import");
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return dto;
//    }


}
