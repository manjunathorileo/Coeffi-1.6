package com.dfq.coeffi.report;

import com.dfq.coeffi.controller.BaseController;
import com.dfq.coeffi.employeePermanentContract.entities.EmpPermanentContract;
import com.dfq.coeffi.employeePermanentContract.services.PermanentContractService;
import com.dfq.coeffi.entity.hr.employee.*;
import com.dfq.coeffi.service.hr.EmployeeService;
import com.dfq.coeffi.visitor.Entities.VisitorPass;
import com.dfq.coeffi.visitor.Services.VisitorPassService;
import jxl.write.*;
import lombok.extern.slf4j.Slf4j;
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
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static jxl.format.Alignment.*;

@RestController
@Slf4j
public class ExportEmployeeMaster extends BaseController {

    @Autowired
    EmployeeService employeeService;

    @GetMapping("employee-export")
    public ResponseEntity<List<Employee>> getEmployeeExport(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, ParseException {


        List<Employee> employeeList = getRegisteredEmployees();
        List<Employee> employeeList1 = getCtcData();
        List<Employee> employeeList3 = getEducationalDetails();
        List<Employee> employeeList4 = getPreviousEmployeeMentDetails();
        List<Employee> employeeList2 = getFamilyDetails();
        OutputStream out = null;
        String fileName = "Employee_Details";
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName + ".xls");
        WritableWorkbook workbook = jxl.Workbook.createWorkbook(response.getOutputStream());


        try {
            if (employeeList != null) {
                employeeDetails(workbook, employeeList, response, 0);
            }
            if (employeeList1 != null) {
                ctcDetails(workbook, employeeList1, response, 1);
            }
            if (employeeList2 != null) {
                employeeFamilyDetails(workbook, employeeList2, response, 2);
            }
            if (employeeList3 != null) {
                employeeEducationDetails(workbook, employeeList3, response, 3);
            }
            if (employeeList4 != null) {
                employeePreviousEmployementDetails(workbook, employeeList4, response, 4);
            }

            workbook.write();
            workbook.close();
        } catch (Exception e) {
            throw new ServletException("Exception in excel download", e);
        } finally {
            if (out != null)
                out.close();
        }
        return new ResponseEntity(workbook, HttpStatus.OK);
    }

    @Autowired
    PermanentContractService permanentContractService;

    public List<Employee> getAllPermanentContract() {
//        List<EmpPermanentContract> obj = permanentContractService.getAll(true);
        List<EmpPermanentContract> obj = permanentContractService.getAll(false);
        if (obj.isEmpty()) {
            throw new EntityNotFoundException("No registered Contract employees");
        }
        List<EmpPermanentContract> empPermanentContractList = new ArrayList<>();
        List<Employee> employeeList = new ArrayList<>();
        for (EmpPermanentContract permanentContract : obj) {
                    if (permanentContract.getEmployeeType().equals(EmployeeType.CONTRACT)) {
                        Optional<Employee> employee = employeeService.getEmployeeByEmployeeCode(permanentContract.getEmployeeCode());
                        if (employee.isPresent()) {
                            employeeList.add(employee.get());
                        }
                    }
        }
        return employeeList;
    }
    @Autowired
    VisitorPassService visitorPassService;

    public List<Employee> getvisitors() {
        List<VisitorPass> obj = visitorPassService.getAllVisitors();
        if (obj.isEmpty()) {
            throw new EntityNotFoundException("No registered Visitors employees");
        }
        List<Employee> employeeList = new ArrayList<>();
        for (VisitorPass permanentContract : obj) {
                Optional<Employee> employee = employeeService.getEmployeeByEmployeeCode(permanentContract.getMobileNumber());
                if (employee.isPresent()) {
                    employeeList.add(employee.get());
                }
        }
        return employeeList;
    }

    public List<Employee> getRegisteredEmployees(){
        List<Employee> employeeList = new ArrayList<>();
        List<Employee> employeeStaff = employeeService.getEmployeeByType(EmployeeType.PERMANENT,true);
        List<Employee> employeeWorker = employeeService.getEmployeeByType(EmployeeType.PERMANENT_WORKER,true);
        employeeList.addAll(employeeStaff);
        employeeList.addAll(employeeWorker);
        List<Employee> empPermanentContracts = getAllPermanentContract();
        List<Employee> empVisitors = getvisitors();
        employeeList.addAll(empPermanentContracts);
        employeeList.addAll(empVisitors);
        return employeeList;
    }

    private WritableWorkbook employeeDetails(WritableWorkbook workbook, List<Employee> employeeList, HttpServletResponse response, int index) throws IOException, WriteException {
        WritableSheet s = workbook.createSheet("Employee-Details", index);
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
        cLeft.setBorder(jxl.format.Border.ALL, BorderLineStyle.THIN);

        s.mergeCells(0, 0, 33, 0);
        Label lable = new Label(0, 0, "Employees Details", headerFormat);
        s.addCell(lable);

        int rowNum = 2;
        for (Employee employee : employeeList) {
            if (employee.getStatus()) {
                s.addCell(new Label(0, 1, "#", cellFormat));
                s.addCell(new Label(0, rowNum, "" + Integer.valueOf(rowNum - 1), cLeft));
                s.addCell(new Label(1, 1, "Employee Code", cellFormat));
                if (employee.getEmployeeCode() != null) {
                    s.addCell(new Label(1, rowNum, "" + employee.getEmployeeCode(), cLeft));
                } else {
                    s.addCell(new Label(1, rowNum, "" + " ", cLeft));
                }
                s.addCell(new Label(2, 1, "Employee Name", cellFormat));
                if (employee.getFirstName() != null && employee.getLastName() != null) {
                    s.addCell(new Label(2, rowNum, "" + employee.getFirstName() + " " + employee.getLastName(), cLeft));
                } else {
                    s.addCell(new Label(2, rowNum, "" + " ", cLeft));
                }

                s.addCell(new Label(3, 1, "RFID", cellFormat));
                if (employee.getRfid() != null) {
                    s.addCell(new Label(3, rowNum, "" + employee.getRfid(), cLeft));
                } else {
                    s.addCell(new Label(3, rowNum, "" + " ", cLeft));
                }

                s.addCell(new Label(4, 1, "Employee Type", cellFormat));
                if (employee.getEmployeeType() != null) {
                    s.addCell(new Label(4, rowNum, "" + employee.getEmployeeType(), cLeft));
                } else {
                    s.addCell(new Label(4, rowNum, "" + " ", cLeft));
                }

                s.addCell(new Label(5, 1, "Department", cellFormat));
                if (employee.getDepartment() != null) {
                    s.addCell(new Label(5, rowNum, "" + employee.getDepartment().getName(), cLeft));
                } else {
                    s.addCell(new Label(5, rowNum, "" + " ", cLeft));
                }

                s.addCell(new Label(6, 1, "Designation", cellFormat));
                if (employee.getDesignation() != null) {
                    s.addCell(new Label(6, rowNum, "" + employee.getDesignation().getName(), cLeft));
                } else {
                    s.addCell(new Label(6, rowNum, "" + " ", cLeft));
                }

                s.addCell(new Label(7, 1, "Age", cellFormat));
                if (employee.getAge() != null) {
                    s.addCell(new Label(7, rowNum, "" + employee.getAge(), cLeft));
                } else {
                    s.addCell(new Label(7, rowNum, "" + " ", cLeft));
                }

                s.addCell(new Label(8, 1, "Gender", cellFormat));
                if (employee.getAge() != null) {
                    s.addCell(new Label(8, rowNum, "" + employee.getGender(), cLeft));
                } else {
                    s.addCell(new Label(8, rowNum, "" + " ", cLeft));
                }
                s.addCell(new Label(9, 1, "Blood Group", cellFormat));
                if (employee.getBloodGroup() != null) {
                    s.addCell(new Label(9, rowNum, "" + employee.getBloodGroup(), cLeft));
                } else {
                    s.addCell(new Label(9, rowNum, "" + " ", cLeft));
                }

                s.addCell(new Label(10, 1, "Phone Number", cellFormat));
                if (employee.getPhoneNumber() != null) {
                    s.addCell(new Label(10, rowNum, "" + employee.getPhoneNumber(), cLeft));
                } else {
                    s.addCell(new Label(10, rowNum, "" + " ", cLeft));
                }

                s.addCell(new Label(11, 1, "Address", cellFormat));
                if (employee.getCurrentAddress() != null) {
                    s.addCell(new Label(11, rowNum, "" + employee.getCurrentAddress(), cLeft));
                } else {
                    s.addCell(new Label(11, rowNum, "" + " ", cLeft));
                }
                s.addCell(new Label(12, 1, "Blood Group", cellFormat));
                if (employee.getPermanentAddress() != null) {
                    s.addCell(new Label(12, rowNum, "" + employee.getPermanentAddress(), cLeft));
                } else {
                    s.addCell(new Label(12, rowNum, "" + " ", cLeft));
                }

                s.addCell(new Label(13, 1, "Date Of Birth", cellFormat));
                if (employee.getDateOfBirth() != null) {
                    s.addCell(new Label(13, rowNum, "" + employee.getDateOfBirth(), cLeft));
                } else {
                    s.addCell(new Label(13, rowNum, "" + " ", cLeft));
                }

                s.addCell(new Label(14, 1, "Emergency Contact Number", cellFormat));
                if (employee.getEmergencyPhoneNumber() != null) {
                    s.addCell(new Label(14, rowNum, "" + employee.getEmergencyPhoneNumber(), cLeft));
                } else {
                    s.addCell(new Label(14, rowNum, "" + " ", cLeft));
                }

                s.addCell(new Label(15, 1, "Date Of Joining", cellFormat));
                if (employee.getDateOfJoining() != null) {
                    s.addCell(new Label(15, rowNum, "" + employee.getDateOfJoining(), cLeft));
                } else {
                    s.addCell(new Label(15, rowNum, "" + " ", cLeft));
                }

                s.addCell(new Label(16, 1, "Level", cellFormat));
                if (employee.getLevel() != null) {
                    s.addCell(new Label(16, rowNum, "" + employee.getLevel(), cLeft));
                } else {
                    s.addCell(new Label(16, rowNum, "" + " ", cLeft));
                }

                s.addCell(new Label(17, 1, "PF UAN Number", cellFormat));
                if (employee.getUanNumber() != null) {
                    s.addCell(new Label(17, rowNum, "" + employee.getUanNumber(), cLeft));
                } else {
                    s.addCell(new Label(17, rowNum, "" + " ", cLeft));
                }

                s.addCell(new Label(18, 1, "PF Number", cellFormat));
                if (employee.getPfNumber() != null) {
                    s.addCell(new Label(18, rowNum, "" + employee.getPfNumber(), cLeft));
                } else {
                    s.addCell(new Label(18, rowNum, "" + " ", cLeft));
                }

                s.addCell(new Label(19, 1, "ESIC Number", cellFormat));
                if (employee.getEsiNumber() != null) {
                    s.addCell(new Label(19, rowNum, "" + employee.getEsiNumber(), cLeft));
                } else {
                    s.addCell(new Label(19, rowNum, "" + " ", cLeft));
                }

                s.addCell(new Label(20, 1, "PAN Number", cellFormat));
                if (employee.getPanNumber() != null) {
                    s.addCell(new Label(20, rowNum, "" + employee.getPanNumber(), cLeft));
                } else {
                    s.addCell(new Label(20, rowNum, "" + " ", cLeft));
                }

                s.addCell(new Label(21, 1, "Father's Name", cellFormat));
                if (employee.getFatherName() != null) {
                    s.addCell(new Label(21, rowNum, "" + employee.getFatherName(), cLeft));
                } else {
                    s.addCell(new Label(21, rowNum, "" + " ", cLeft));
                }

                s.addCell(new Label(22, 1, "Mother's Name", cellFormat));
                if (employee.getMotherName() != null) {
                    s.addCell(new Label(22, rowNum, "" + employee.getMotherName(), cLeft));
                } else {
                    s.addCell(new Label(22, rowNum, "" + " ", cLeft));
                }

                s.addCell(new Label(23, 1, "First Manager", cellFormat));
                if (employee.getFirstApprovalManager() != null) {
                    s.addCell(new Label(23, rowNum, "" + employee.getFirstApprovalManager().getFirstName() + " " + employee.getFirstApprovalManager().getLastName(), cLeft));
                } else {
                    s.addCell(new Label(23, rowNum, "" + " ", cLeft));
                }

                s.addCell(new Label(24, 1, "Second Manager", cellFormat));
                if (employee.getSecondApprovalManager() != null) {
                    s.addCell(new Label(24, rowNum, "" + employee.getSecondApprovalManager().getFirstName() + " " + employee.getSecondApprovalManager().getLastName(), cLeft));
                } else {
                    s.addCell(new Label(24, rowNum, "" + " ", cLeft));
                }

                s.addCell(new Label(25, 1, "First Week Off", cellFormat));
                if (employee.getFirstWeekOffName() != null) {
                    s.addCell(new Label(25, rowNum, "" + employee.getFirstWeekOffName(), cLeft));
                } else {
                    s.addCell(new Label(25, rowNum, "" + " ", cLeft));
                }

                s.addCell(new Label(26, 1, "Second Week Off", cellFormat));
                if (employee.getSecondWeekOffName() != null) {
                    s.addCell(new Label(26, rowNum, "" + employee.getSecondWeekOffName(), cLeft));
                } else {
                    s.addCell(new Label(26, rowNum, "" + " ", cLeft));
                }

                s.addCell(new Label(27, 1, "Login Id", cellFormat));
                if (employee.getEmployeeLogin() != null) {
                    s.addCell(new Label(27, rowNum, "" + employee.getEmployeeLogin().getEmail(), cLeft));
                } else {
                    s.addCell(new Label(27, rowNum, "" + " ", cLeft));
                }

                s.addCell(new Label(28, 1, "Password", cellFormat));
                if (employee.getEmployeeLogin() != null) {
                    s.addCell(new Label(28, rowNum, "" + employee.getEmployeeLogin().getPassword(), cLeft));
                } else {
                    s.addCell(new Label(28, rowNum, "" + " ", cLeft));
                }

                s.addCell(new Label(29, 1, "Bank Name", cellFormat));
                if (employee.getEmployeeBank() != null) {
                    s.addCell(new Label(29, rowNum, "" + employee.getEmployeeBank().getBankName(), cLeft));
                } else {
                    s.addCell(new Label(29, rowNum, "" + " ", cLeft));
                }

                s.addCell(new Label(30, 1, "Branch", cellFormat));
                if (employee.getEmployeeBank() != null) {
                    s.addCell(new Label(30, rowNum, "" + employee.getEmployeeBank().getBranch(), cLeft));
                } else {
                    s.addCell(new Label(30, rowNum, "" + " ", cLeft));
                }

                s.addCell(new Label(31, 1, "Account Number", cellFormat));
                if (employee.getEmployeeBank() != null) {
                    s.addCell(new Label(31, rowNum, "" + employee.getEmployeeBank().getAccountNumber(), cLeft));
                } else {
                    s.addCell(new Label(31, rowNum, "" + " ", cLeft));
                }

                s.addCell(new Label(32, 1, "Account Type", cellFormat));
                if (employee.getEmployeeBank() != null) {
                    s.addCell(new Label(32, rowNum, "" + employee.getEmployeeBank().getAccountType(), cLeft));
                } else {
                    s.addCell(new Label(32, rowNum, "" + " ", cLeft));
                }

                s.addCell(new Label(33, 1, "IFSC Code", cellFormat));
                if (employee.getEmployeeBank() != null) {
                    s.addCell(new Label(33, rowNum, "" + employee.getEmployeeBank().getIfscCode(), cLeft));
                } else {
                    s.addCell(new Label(33, rowNum, "" + " ", cLeft));
                }

                rowNum = rowNum + 1;
            }

        }
        return workbook;
    }

    private WritableWorkbook ctcDetails(WritableWorkbook workbook, List<Employee> employeeList, HttpServletResponse response, int index) throws IOException, WriteException {
        WritableSheet s = workbook.createSheet("CTC-Details", index);
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
        cLeft.setBorder(jxl.format.Border.ALL, BorderLineStyle.THIN);

        s.mergeCells(0, 0, 18, 0);
        Label lable = new Label(0, 0, "Employee CTC Details", headerFormat);
        s.addCell(lable);

        s.mergeCells(0, 0, 12, 0);
        Label lable1 = new Label(2, 1, "Earned Salary", headerFormat);
        s.addCell(lable1);
        s.mergeCells(14, 0, 17, 0);
        Label lable2 = new Label(14, 1, "Deductions", headerFormat);
        s.addCell(lable2);

        int rowNum = 3;
        for (Employee employee : employeeList) {
            s.addCell(new Label(0, 2, "#", cellFormat));
            s.addCell(new Label(0, rowNum, "" + Integer.valueOf(rowNum - 2), cLeft));
            s.addCell(new Label(1, 2, "Employee Code", cellFormat));
            if (employee.getEmployeeCode() != null) {
                s.addCell(new Label(1, rowNum, "" + employee.getEmployeeCode(), cLeft));
            } else {
                s.addCell(new Label(1, rowNum, "" + " ", cLeft));
            }
            s.addCell(new Label(2, 2, "Employee Name", cellFormat));
            if (employee.getFirstName() != null && employee.getLastName() != null) {
                s.addCell(new Label(2, rowNum, "" + employee.getFirstName() + " " + employee.getLastName(), cLeft));
            } else {
                s.addCell(new Label(2, rowNum, "" + " ", cLeft));
            }
            s.addCell(new Label(3, 2, "Basic Amount", cellFormat));
            if (employee.getEmployeeCTCData().getBasicSalary() == null) {
                s.addCell(new Label(3, rowNum, "" + " ", cLeft));
            } else {
                s.addCell(new Label(3, rowNum, "" + employee.getEmployeeCTCData().getBasicSalary(), cLeft));

            }

            s.addCell(new Label(4, 2, "CON", cellFormat));
            if (employee.getEmployeeCTCData().getConveyanceAllowance() != null) {
                s.addCell(new Label(4, rowNum, "" + employee.getEmployeeCTCData().getConveyanceAllowance(), cLeft));
            } else {
                s.addCell(new Label(4, rowNum, "" + " ", cLeft));
            }
            s.addCell(new Label(5, 2, "HRN", cellFormat));
            if (employee.getEmployeeCTCData().getHouseRentAllowance() != null) {
                s.addCell(new Label(5, rowNum, "" + employee.getEmployeeCTCData().getHouseRentAllowance(), cLeft));
            } else {
                s.addCell(new Label(5, rowNum, "" + " ", cLeft));
            }

            s.addCell(new Label(6, 2, "EDU", cellFormat));
            if (employee.getEmployeeCTCData().getEducationalAllowance() != null) {
                s.addCell(new Label(6, rowNum, "" + employee.getEmployeeCTCData().getEducationalAllowance(), cLeft));
            } else {
                s.addCell(new Label(6, rowNum, "" + " ", cLeft));
            }

            s.addCell(new Label(7, 2, "MA", cellFormat));
            if (employee.getEmployeeCTCData().getMiscellaneousAllowance() != null) {
                s.addCell(new Label(7, rowNum, "" + employee.getEmployeeCTCData().getMiscellaneousAllowance(), cLeft));
            } else {
                s.addCell(new Label(7, rowNum, "" + " ", cLeft));
            }
            s.addCell(new Label(8, 2, "VA", cellFormat));
            if (employee.getEmployeeCTCData().getVariableDearnessAllowance() != null) {
                s.addCell(new Label(8, rowNum, "" + employee.getEmployeeCTCData().getVariableDearnessAllowance(), cLeft));
            } else {
                s.addCell(new Label(8, rowNum, "" + " ", cLeft));
            }

            s.addCell(new Label(9, 2, "Other Allow", cellFormat));
            if (employee.getDateOfBirth() != null) {
                s.addCell(new Label(9, rowNum, "" + employee.getEmployeeCTCData().getOtherAllowance(), cLeft));
            } else {
                s.addCell(new Label(9, rowNum, "" + " ", cLeft));
            }

            s.addCell(new Label(10, 2, "MISC", cellFormat));
            if (employee.getEmployeeCTCData().getMiscellaneousAllowance() != null) {
                s.addCell(new Label(10, rowNum, "" + employee.getEmployeeCTCData().getMiscellaneousAllowance(), cLeft));
            } else {
                s.addCell(new Label(10, rowNum, "" + " ", cLeft));
            }

            s.addCell(new Label(11, 2, "Mobile", cellFormat));
            if (employee.getEmployeeCTCData().getMobileAllowance() != null) {
                s.addCell(new Label(11, rowNum, "" + employee.getEmployeeCTCData().getMobileAllowance(), cLeft));
            } else {
                s.addCell(new Label(11, rowNum, "" + " ", cLeft));
            }

            s.addCell(new Label(12, 2, "Gross", cellFormat));
            if (employee.getEmployeeCTCData().getMonthlyCtc() != null) {
                s.addCell(new Label(12, rowNum, "" + employee.getEmployeeCTCData().getMonthlyCtc(), cLeft));
            } else {
                s.addCell(new Label(12, rowNum, "" + " ", cLeft));
            }


            s.addCell(new Label(13, 2, "-", cellFormat));
            s.addCell(new Label(13, rowNum, " " + " ", cLeft));

            s.addCell(new Label(14, 2, "ESIC", cellFormat));
            if (employee.getEmployeeCTCData().getEmployeeEsicContribution() != null) {
                s.addCell(new Label(14, rowNum, "" + employee.getEmployeeCTCData().getEmployeeEsicContribution(), cLeft));
            } else {
                s.addCell(new Label(14, rowNum, "" + " ", cLeft));
            }

            s.addCell(new Label(15, 2, "Meal", cellFormat));
            if (employee.getEmployeeCTCData().getMealsAllowance() != null) {
                s.addCell(new Label(15, rowNum, "" + employee.getEmployeeCTCData().getMealsAllowance(), cLeft));
            } else {
                s.addCell(new Label(15, rowNum, "" + " ", cLeft));
            }

            s.addCell(new Label(16, 2, "Profession Tax", cellFormat));
            if (employee.getEmployeeCTCData().getTpt() != null) {
                s.addCell(new Label(16, rowNum, "" + employee.getEmployeeCTCData().getTpt(), cLeft));
            } else {
                s.addCell(new Label(16, rowNum, "" + " ", cLeft));
            }

            s.addCell(new Label(17, 2, "PF", cellFormat));
            if (employee.getEmployeeCTCData().getEpfContribution() != null) {
                s.addCell(new Label(17, rowNum, "" + employee.getEmployeeCTCData().getEpfContribution(), cLeft));
            } else {
                s.addCell(new Label(17, rowNum, "" + " ", cLeft));
            }

            BigDecimal deduction = employee.getEmployeeCTCData().getEmployeeEsicContribution().
                    add(employee.getEmployeeCTCData().getMealsAllowance()).
                    add(employee.getEmployeeCTCData().getTpt()).
                    add(employee.getEmployeeCTCData().getEpfContribution());
            BigDecimal netPay = employee.getEmployeeCTCData().getMonthlyCtc().subtract(deduction);

            s.addCell(new Label(18, 2, "Net Pay", cellFormat));
            if (employee.getEmployeeCTCData() != null) {
                s.addCell(new Label(18, rowNum, "" + netPay, cLeft));
            } else {
                s.addCell(new Label(18, rowNum, "" + " ", cLeft));
            }


            rowNum = rowNum + 1;

        }
        return workbook;
    }

    private WritableWorkbook employeeFamilyDetails(WritableWorkbook workbook, List<Employee> employeeList, HttpServletResponse response, int index) throws IOException, WriteException {
        WritableSheet s = workbook.createSheet("Family-Members-Details", index);
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
        cLeft.setBorder(jxl.format.Border.ALL, BorderLineStyle.THIN);

        s.mergeCells(0, 0, 4, 0);
        Label lable = new Label(0, 0, "Family Members Details", headerFormat);
        s.addCell(lable);

        int rowNum = 2;
        int slCount = 1;
        for (Employee employee : employeeList) {
            s.addCell(new Label(0, 1, "#", cellFormat));
            s.addCell(new Label(0, rowNum, "" + Integer.valueOf(slCount), cLeft));
            s.addCell(new Label(1, 1, "Employee Code", cellFormat));
            if (employee.getEmployeeCode() != null) {
                s.addCell(new Label(1, rowNum, "" + employee.getEmployeeCode(), cLeft));
            } else {
                s.addCell(new Label(1, rowNum, "" + " ", cLeft));
            }
            s.addCell(new Label(2, 1, "Employee Name", cellFormat));
            if (employee.getFirstName() != null && employee.getLastName() != null) {
                s.addCell(new Label(2, rowNum, "" + employee.getFirstName() + " " + employee.getLastName(), cLeft));
            } else {
                s.addCell(new Label(2, rowNum, "" + " ", cLeft));
            }
            for (FamilyMember f : employee.getFamilyMember()) {
                s.addCell(new Label(3, 1, "Family Dependent Name", cellFormat));
                if (f.getName() != null) {
                    s.addCell(new Label(3, rowNum, "" + f.getName(), cLeft));
                } else {
                    s.addCell(new Label(3, rowNum, "" + " ", cLeft));
                }
                s.addCell(new Label(4, 1, "Relation Type", cellFormat));
                if (f.getRelation() != null) {
                    s.addCell(new Label(4, rowNum, "" + f.getRelation(), cLeft));
                } else {
                    s.addCell(new Label(4, rowNum, "" + " ", cLeft));
                }
                rowNum = rowNum + 1;

            }
            slCount = slCount + 1;

        }
        return workbook;
    }

    private WritableWorkbook employeeEducationDetails(WritableWorkbook workbook, List<Employee> employeeList, HttpServletResponse response, int index) throws IOException, WriteException {
        WritableSheet s = workbook.createSheet("Educational-Details", index);
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
        cLeft.setBorder(jxl.format.Border.ALL, BorderLineStyle.THIN);

        s.mergeCells(0, 0, 6, 0);
        Label lable = new Label(0, 0, "Educational Details", headerFormat);
        s.addCell(lable);

        int rowNum = 2;
        int slCount = 1;
        for (Employee employee : employeeList) {
            s.addCell(new Label(0, 1, "#", cellFormat));
            s.addCell(new Label(0, rowNum, "" + Integer.valueOf(slCount), cLeft));
            s.addCell(new Label(1, 1, "Employee Code", cellFormat));
            if (employee.getEmployeeCode() != null) {
                s.addCell(new Label(1, rowNum, "" + employee.getEmployeeCode(), cLeft));
            } else {
                s.addCell(new Label(1, rowNum, "" + " ", cLeft));
            }
            s.addCell(new Label(2, 1, "Employee Name", cellFormat));
            if (employee.getFirstName() != null && employee.getLastName() != null) {
                s.addCell(new Label(2, rowNum, "" + employee.getFirstName() + " " + employee.getLastName(), cLeft));
            } else {
                s.addCell(new Label(2, rowNum, "" + " ", cLeft));
            }
            for (EmployeeCertification certification : employee.getEmployeeCertifications()) {
                s.addCell(new Label(3, 1, "Certification Name", cellFormat));
                if (certification.getCertificationName() != null) {
                    s.addCell(new Label(3, rowNum, "" + certification.getCertificationName(), cLeft));
                } else {
                    s.addCell(new Label(3, rowNum, "" + " ", cLeft));
                }
                s.addCell(new Label(4, 1, "Institute Name", cellFormat));
                if (certification.getInstituteName() != null) {
                    s.addCell(new Label(4, rowNum, "" + certification.getInstituteName(), cLeft));
                } else {
                    s.addCell(new Label(4, rowNum, "" + " ", cLeft));
                }

                s.addCell(new Label(5, 1, "Start Date", cellFormat));
                if (certification.getStartDate() != null) {
                    s.addCell(new Label(5, rowNum, "" + certification.getInstituteName(), cLeft));
                } else {
                    s.addCell(new Label(5, rowNum, "" + " ", cLeft));
                }

                s.addCell(new Label(6, 1, "End Date", cellFormat));
                if (certification.getEndDate() != null) {
                    s.addCell(new Label(6, rowNum, "" + certification.getEndDate(), cLeft));
                } else {
                    s.addCell(new Label(6, rowNum, "" + " ", cLeft));
                }

                rowNum = rowNum + 1;

            }
            slCount = slCount + 1;

        }
        return workbook;
    }

    private WritableWorkbook employeePreviousEmployementDetails(WritableWorkbook workbook, List<Employee> employeeList, HttpServletResponse response, int index) throws IOException, WriteException {
        WritableSheet s = workbook.createSheet("Previous Employment", index);
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
        cLeft.setBorder(jxl.format.Border.ALL, BorderLineStyle.THIN);

        s.mergeCells(0, 0, 8, 0);
        Label lable = new Label(0, 0, "Previous Employment", headerFormat);
        s.addCell(lable);

        int rowNum = 2;
        int slCount = 1;
        for (Employee employee : employeeList) {
            s.addCell(new Label(0, 1, "#", cellFormat));
            s.addCell(new Label(0, rowNum, "" + Integer.valueOf(slCount), cLeft));
            s.addCell(new Label(1, 1, "Employee Code", cellFormat));
            if (employee.getEmployeeCode() != null) {
                s.addCell(new Label(1, rowNum, "" + employee.getEmployeeCode(), cLeft));
            } else {
                s.addCell(new Label(1, rowNum, "" + " ", cLeft));
            }
            s.addCell(new Label(2, 1, "Employee Name", cellFormat));
            if (employee.getFirstName() != null && employee.getLastName() != null) {
                s.addCell(new Label(2, rowNum, "" + employee.getFirstName() + " " + employee.getLastName(), cLeft));
            } else {
                s.addCell(new Label(2, rowNum, "" + " ", cLeft));
            }
            for (PreviousEmployement previousEmployement : employee.getPreviousEmployement()) {
                s.addCell(new Label(3, 1, "Position", cellFormat));
                if (previousEmployement.getPosition() != null) {
                    s.addCell(new Label(3, rowNum, "" + previousEmployement.getPosition(), cLeft));
                } else {
                    s.addCell(new Label(3, rowNum, "" + " ", cLeft));
                }
                s.addCell(new Label(4, 1, "Employee Type", cellFormat));
                if (previousEmployement.getEmployementType() != null) {
                    s.addCell(new Label(4, rowNum, "" + previousEmployement.getEmployementType(), cLeft));
                } else {
                    s.addCell(new Label(4, rowNum, "" + " ", cLeft));
                }

                s.addCell(new Label(5, 1, "Start Date", cellFormat));
                if (previousEmployement.getStartDate() != null) {
                    s.addCell(new Label(5, rowNum, "" + previousEmployement.getStartDate(), cLeft));
                } else {
                    s.addCell(new Label(5, rowNum, "" + " ", cLeft));
                }

                s.addCell(new Label(6, 1, "End Date", cellFormat));
                if (previousEmployement.getEndDate() != null) {
                    s.addCell(new Label(6, rowNum, "" + previousEmployement.getEndDate(), cLeft));
                } else {
                    s.addCell(new Label(6, rowNum, "" + " ", cLeft));
                }

                s.addCell(new Label(7, 1, "Experience In Month", cellFormat));
                if (previousEmployement.getExperienceInMonth() != 0) {
                    s.addCell(new Label(7, rowNum, "" + previousEmployement.getExperienceInMonth(), cLeft));
                } else {
                    s.addCell(new Label(7, rowNum, "" + " ", cLeft));
                }

                s.addCell(new Label(8, 1, "Experience In Year", cellFormat));
                if (previousEmployement.getExperienceInYear() != 0) {
                    s.addCell(new Label(8, rowNum, "" + previousEmployement.getExperienceInYear(), cLeft));
                } else {
                    s.addCell(new Label(8, rowNum, "" + " ", cLeft));
                }

                rowNum = rowNum + 1;

            }
            slCount = slCount + 1;

        }
        return workbook;
    }


    public List<Employee> getCtcData() {
        List<Employee> employeeList = employeeService.findAll();
        List<Employee> employeeList1 = new ArrayList<>();
        for (Employee e : employeeList) {
            if (e.getEmployeeCTCData() != null) {
                employeeList1.add(e);
            }

        }
        return employeeList1;
    }

    public List<Employee> getFamilyDetails() {
        List<Employee> employeeList = employeeService.findAll();
        List<Employee> employeeList1 = new ArrayList<>();
        for (Employee e : employeeList) {
            if (e.getFamilyMember() != null) {
                employeeList1.add(e);
            }

        }
        return employeeList1;
    }

    public List<Employee> getEducationalDetails() {
        List<Employee> employeeList = employeeService.findAll();
        List<Employee> employeeList1 = new ArrayList<>();
        for (Employee e : employeeList) {
            if (e.getEmployeeCertifications() != null) {
                employeeList1.add(e);
            }

        }
        return employeeList1;
    }

    public List<Employee> getPreviousEmployeeMentDetails() {
        List<Employee> employeeList = employeeService.findAll();
        List<Employee> employeeList1 = new ArrayList<>();
        for (Employee e : employeeList) {
            if (e.getPreviousEmployement() != null) {
                employeeList1.add(e);
            }

        }
        return employeeList1;
    }
}
