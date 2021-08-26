package com.dfq.coeffi.headCountGE;

import com.dfq.coeffi.employeePermanentContract.entities.EmpPermanentContract;
import com.dfq.coeffi.employeePermanentContract.entities.PermanentContractAttendance;
import com.dfq.coeffi.employeePermanentContract.repositories.PermanentContractAttendanceRepo;
import com.dfq.coeffi.employeePermanentContract.services.PermanentContractService;
import com.dfq.coeffi.entity.hr.employee.AttendanceStatus;
import com.dfq.coeffi.entity.hr.employee.EmployeeType;
import com.dfq.coeffi.evacuationApi.EvacuationTracker;
import com.dfq.coeffi.evacuationApi.EvacuationTrackerRepository;
import com.dfq.coeffi.service.payroll.EmployeeAttendanceService;
import com.dfq.coeffi.visitor.Entities.Visitor;
import com.dfq.coeffi.visitor.Services.VisitorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;


public class ExecutableController {
    @Autowired
    PermanentContractAttendanceRepo permanentContractAttendanceRepo;
    @Autowired
    EmployeeAttendanceService employeeAttendanceService;
    @Autowired
    VisitorService visitorService;
    @Autowired
    PermanentContractService permanentContractService;


//    @GetMapping("executable/read-csv")
//    @Scheduled(initialDelay = 1105, fixedRate = 2010)
    public void readCsvIn() throws Exception {
        String line = "";
        String splitBy = ",";
        try {
            BufferedReader br = new BufferedReader(new FileReader("C:\\Users\\502548453\\Desktop\\EMR\\IN.csv"));
//            BufferedReader br = new BufferedReader(new FileReader("E:\\Customer_Builds\\GEBE\\Spectra_Headcount\\IN.csv"));
            while ((line = br.readLine()) != null) {
                String[] employee = line.split(splitBy);
                if (true) {
                    System.out.println("employee[2]" + employee[2] + "employee[4] " + employee[4]);
                    if (employee[2].equalsIgnoreCase("Temp") || employee[2].equalsIgnoreCase("Visitor")) {
                        System.out.println("READING-inside");
                        // TODO Visitor entry
                        String name = employee[0];
                        String mobileId = employee[1];
                        String type = employee[2];
                        //----------------------------------
                        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                        Date entryDate = df.parse(employee[4]);
                        entryDate = mySqlFormatDate(entryDate);
                        //----------------------------------

                        System.out.println("entry: " + entryDate + " mobile " + mobileId);
                        Visitor visitorBymobNo = visitorService.getByMobileNumberAndDate(entryDate, mobileId);
                        System.out.println(" vis " + visitorBymobNo);
                        if (visitorBymobNo != null) {
                            System.out.println("Done");
                        } else {
                            Visitor visitor = new Visitor();
                            SimpleDateFormat sdfHr = new SimpleDateFormat("HH.mm");
                            String checkInTime = sdfHr.format(entryDate);
                            visitor.setLoggedOn(entryDate);
                            visitor.setCheckInTime(checkInTime);
                            visitor.setMobileNumber(mobileId);
                            visitor.setFirstName(name);
                            visitor.setEmail("");
                            visitor.setVisitType("Official");
                            visitor.setPersonToVisit("");
                            visitor.setEntryBodyTemperature(0);
                            visitor.setMaskWearing(false);
                            visitor.setTimeSlot(0);
                            visitor.setEntryGateNumber("01");
                            visitorService.saveVisitor(visitor);
                        }
                        System.out.println("1");
                    } else if (employee[2].equalsIgnoreCase("Employee")) {
                        //TODO employeeEntry
                        String name = employee[0];
                        String employeeCode = employee[1];
                        String type = employee[2];
                        //----------------------------------
                        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                        Date entryDate = df.parse(employee[4]);
                        entryDate = mySqlFormatDate(entryDate);
                        //----------------------------------
                        EmpPermanentContract empPermanentContract = permanentContractService.get(employeeCode);
                        if (empPermanentContract == null) {
                            empPermanentContract = new EmpPermanentContract();
                            empPermanentContract.setCreatedOn(new Date());
                            empPermanentContract.setEmployeeCode(employeeCode);
                            empPermanentContract.setEmployeeType(EmployeeType.PERMANENT_CONTRACT);
                            empPermanentContract.setFirstName(name);
                            empPermanentContract.setLastName("");
                            empPermanentContract.setCardId(employee[3]);
                            empPermanentContract = permanentContractService.save(empPermanentContract);
                        } else {
//                            System.out.println("registered already");
                        }
                        PermanentContractAttendance employeeAttendance = permanentContractAttendanceRepo.
                                getEmployeeAttendanceByEmployeeId(entryDate, empPermanentContract.getId());
                        if (employeeAttendance == null) {
                            PermanentContractAttendance newEmployeeAttendance = new PermanentContractAttendance();
                            newEmployeeAttendance.setAttendanceStatus(AttendanceStatus.PRESENT);
                            newEmployeeAttendance.setEmpId(empPermanentContract.getId());
                            newEmployeeAttendance.setEmployeeCode(empPermanentContract.getEmployeeCode());
                            newEmployeeAttendance.setInTime(entryDate);
                            newEmployeeAttendance.setMaskWearing(false);
                            newEmployeeAttendance.setEntryBodyTemperature(0);
                            newEmployeeAttendance.setEntryGateNumber("01");
                            newEmployeeAttendance.setMarkedOn(entryDate);
                            newEmployeeAttendance.setRecordedTime(entryDate);
                            permanentContractAttendanceRepo.save(newEmployeeAttendance);
                            System.out.println("GREAT");
                        } else if (employeeAttendance != null && employeeAttendance.getInTime() != null && employeeAttendance.getOutTime() == null
                                && employeeAttendance.getAttendanceStatus().equals(AttendanceStatus.PRESENT)) {
//                            System.out.println("In Time Already Marked");
                        } else {
//                            System.out.println("In Time Already Marked For Today");
                        }
                        System.out.println("2");

                    } else if (employee[2].equalsIgnoreCase("Contractor")) {
                        //TODO contract entry
                        String name = employee[0];
                        String employeeCode = employee[1];
                        String type = employee[2];
                        //----------------------------------
                        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                        Date entryDate = df.parse(employee[4]);
                        entryDate = mySqlFormatDate(entryDate);
                        //----------------------------------
                        EmpPermanentContract empPermanentContract = permanentContractService.get(employeeCode);
                        if (empPermanentContract == null) {
                            empPermanentContract = new EmpPermanentContract();
                            empPermanentContract.setCreatedOn(new Date());
                            empPermanentContract.setEmployeeCode(employeeCode);
                            empPermanentContract.setEmployeeType(EmployeeType.CONTRACT);
                            empPermanentContract.setFirstName(name);
                            empPermanentContract.setLastName("");
                            empPermanentContract.setCardId(employee[3]);
                            empPermanentContract = permanentContractService.save(empPermanentContract);
                        } else {
//                            System.out.println("registered already");
                        }
                        PermanentContractAttendance employeeAttendance = permanentContractAttendanceRepo.
                                getEmployeeAttendanceByEmployeeId(entryDate, empPermanentContract.getId());
                        if (employeeAttendance == null) {
                            PermanentContractAttendance newEmployeeAttendance = new PermanentContractAttendance();
                            newEmployeeAttendance.setAttendanceStatus(AttendanceStatus.PRESENT);
                            newEmployeeAttendance.setEmpId(empPermanentContract.getId());
                            newEmployeeAttendance.setEmployeeCode(empPermanentContract.getEmployeeCode());
                            newEmployeeAttendance.setInTime(entryDate);
                            newEmployeeAttendance.setMaskWearing(false);
                            newEmployeeAttendance.setEntryBodyTemperature(0);
                            newEmployeeAttendance.setEntryGateNumber("01");
                            newEmployeeAttendance.setMarkedOn(entryDate);
                            newEmployeeAttendance.setRecordedTime(entryDate);
                            permanentContractAttendanceRepo.save(newEmployeeAttendance);
                            System.out.println("GREAT");
                        } else if (employeeAttendance != null && employeeAttendance.getInTime() != null && employeeAttendance.getOutTime() == null
                                && employeeAttendance.getAttendanceStatus().equals(AttendanceStatus.PRESENT)) {
//                            System.out.println("In Time Already Marked");
                        } else {
//                            System.out.println("In Time Already Marked For Today");
                        }
                        System.out.println("3");

                    }
                }
            }
        } catch (IOException | ParseException e) {
//            System.out.println("*");
//            e.printStackTrace();
        }
    }


//    @GetMapping("executable/read-out-csv")
//    @Scheduled(initialDelay = 4005, fixedRate = 1090)
    public void readCsvOut() throws Exception {
        String line = "";
        String splitBy = ",";
        try {
            //GEBE PATH |
            BufferedReader br = new BufferedReader(new FileReader("C:\\Users\\502548453\\Desktop\\EMR\\OUT.csv"));
//            BufferedReader br = new BufferedReader(new FileReader("E:\\Customer_Builds\\GEBE\\Spectra_Headcount\\OUT.csv"));
            while ((line = br.readLine()) != null) {
                String[] employee = line.split(splitBy);    // use comma as separator
//                System.out.println("[ Name=" + employee[0] + ", SSO=" + employee[1] + ", TYPE=" + employee[2] + ", BID=" + employee[3] +
//                ", ACCESS_DATE= " + employee[4] + ", READER= " + employee[5] + "]");
                //13-01-2020 14:25
//                SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
//                Date entryDate = df.parse(employee[4]);
                if (true) {
                    if (employee[2].equalsIgnoreCase("Temp") || employee[2].equalsIgnoreCase("Visitor")) {
                        // TODO Visitor entry
                        String name = employee[0];
                        String mobileId = employee[1];
                        String type = employee[2];
                        //----------------------------------
                        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                        Date entryDate = df.parse(employee[4]);
                        entryDate = mySqlFormatDate(entryDate);
                        //----------------------------------
                        Visitor visitorBymobNo = visitorService.getByMobileNumberAndDate(entryDate, mobileId);
                        if (visitorBymobNo == null) {
//                            System.out.println("Check-In first");
                        }
                        if (visitorBymobNo != null && visitorBymobNo.getCheckOutTime() == null) {
                            Visitor visitor = visitorBymobNo;
                            SimpleDateFormat sdfHr = new SimpleDateFormat("HH.mm");
                            String checkInTime = sdfHr.format(entryDate);
                            visitor.setCheckOutTime(checkInTime);
                            visitorService.saveVisitor(visitor);
                        } else {
//                            System.out.println("Checked Already first");
                        }
                        System.out.println("1");
                    } else if (employee[2].equalsIgnoreCase("Employee")) {
                        //TODO employeeEntry
                        String name = employee[0];
                        String employeeCode = employee[1];
                        String type = employee[2];
                        //----------------------------------
                        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                        Date entryDate = df.parse(employee[4]);
                        entryDate = mySqlFormatDate(entryDate);
                        //----------------------------------
                        EmpPermanentContract empPermanentContract = permanentContractService.get(employeeCode);
                        if (empPermanentContract != null) {
                            PermanentContractAttendance employeeAttendance = permanentContractAttendanceRepo.
                                    getEmployeeAttendanceByEmployeeId(entryDate, empPermanentContract.getId());
                            if (employeeAttendance == null) {
                                System.out.println("Mark In First");
                            }
                            if (employeeAttendance != null && employeeAttendance.getInTime() != null && employeeAttendance.getOutTime() == null
                                    && employeeAttendance.getAttendanceStatus().equals(AttendanceStatus.PRESENT)) {
                                employeeAttendance.setOutTime(entryDate);
                                employeeAttendance.setRecordedTime(entryDate);
                                //TODO for dubai
//                              calculateTotalStayTime(employeeAttendance);
                                permanentContractAttendanceRepo.save(employeeAttendance);
                            } else {
//                                System.out.println("Out Time Already Marked");
                            }
                        } else {
//                            System.out.println("reg agilla");
                        }
                        System.out.println("2");

                    } else if (employee[2].equalsIgnoreCase("Contractor")) {
                        //TODO contract entry
                        String name = employee[0];
                        String employeeCode = employee[1];
                        String type = employee[2];
                        //----------------------------------
                        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                        Date entryDate = df.parse(employee[4]);
                        entryDate = mySqlFormatDate(entryDate);
                        //----------------------------------
                        EmpPermanentContract empPermanentContract = permanentContractService.get(employeeCode);
                        if (empPermanentContract != null) {
                            PermanentContractAttendance employeeAttendance = permanentContractAttendanceRepo.
                                    getEmployeeAttendanceByEmployeeId(entryDate, empPermanentContract.getId());
                            if (employeeAttendance == null) {
//                                System.out.println("Mark In First");
                            }
                            if (employeeAttendance != null && employeeAttendance.getInTime() != null && employeeAttendance.getOutTime() == null
                                    && employeeAttendance.getAttendanceStatus().equals(AttendanceStatus.PRESENT)) {
                                employeeAttendance.setOutTime(entryDate);
                                employeeAttendance.setRecordedTime(entryDate);
                                //TODO for dubai
//                           calculateTotalStayTime(employeeAttendance);
                                permanentContractAttendanceRepo.save(employeeAttendance);
                            } else {
//                                System.out.println("Out Time Already Marked");
                            }
                        }
                        System.out.println("3");
                    }
                }
            }
        } catch (IOException | ParseException e) {
//            System.out.println("*");
//            e.printStackTrace();
        }
    }

//    @GetMapping("exe-click")
//    @Scheduled(initialDelay = 2015, fixedRate = 30000)
    public void exeClick() {
        try {
            // Command to create an external process
            String command = "D:\\EMR DEC\\GEBEEMR.exe";
            // Running the above command
            Runtime run = Runtime.getRuntime();
            Process proc = run.exec(command, null, new File("D:\\EMR DEC\\"));
        } catch (IOException e) {
            System.out.println("wrong path");
        }
    }

    public static Date mySqlFormatDate(Date date) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            date = df.parse(df.format(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    @Autowired
    private EvacuationTrackerRepository evacuationTrackerRepository;


//    @GetMapping("executable/read-inside-csv")
//    @Scheduled(initialDelay = 2115, fixedRate = 1090)
    public void readCsvInside() throws Exception {
        String line = "";
        String splitBy = ",";
        try {
            //GEBE PATH |
            BufferedReader br = new BufferedReader(new FileReader("C:\\Users\\502548453\\Desktop\\EMR\\INSIDE.csv"));
//            BufferedReader br = new BufferedReader(new FileReader("E:\\Customer_Builds\\GEBE\\Spectra_Headcount\\OUT.csv"));
            while ((line = br.readLine()) != null) {
                String[] employee = line.split(splitBy);
                if (true) {
                    if (employee[1].equalsIgnoreCase("Temp") || employee[2].equalsIgnoreCase("Visitor")) {
                        // TODO Visitor entry
                        System.out.println("visitorCount " + employee[1]);
                        List<EvacuationTracker> evacuationTrackers = evacuationTrackerRepository.findAll();
                        EvacuationTracker evacuationTracker = new EvacuationTracker();

                        if (!evacuationTrackers.isEmpty()) {
                            Collections.reverse(evacuationTrackers);
                            evacuationTracker = evacuationTrackers.get(0);
                            long visitorCount = evacuationTracker.getVisitorCount();
                            visitorCount = visitorCount + 1;
                            evacuationTracker.setVisitorCount(visitorCount);
                            evacuationTracker.setRecordedOn(new Date());
                            evacuationTrackerRepository.save(evacuationTracker);
                        } else {
                            evacuationTracker = new EvacuationTracker();
                            evacuationTracker.setVisitorCount(1);
                            evacuationTracker.setRecordedOn(new Date());
                            evacuationTrackerRepository.save(evacuationTracker);
                        }


                    } else if (employee[1].equalsIgnoreCase("Employee")) {
                        //TODO employeeEntry

                        List<EvacuationTracker> evacuationTrackers = evacuationTrackerRepository.findAll();
                        EvacuationTracker evacuationTracker;

                        if (!evacuationTrackers.isEmpty()) {
                            Collections.reverse(evacuationTrackers);
                            evacuationTracker = evacuationTrackers.get(0);
                            long employeeCount = evacuationTracker.getPermanentCount();
                            employeeCount = employeeCount + 1;
                            evacuationTracker.setPermanentCount(employeeCount);
                            evacuationTracker.setRecordedOn(new Date());
                            evacuationTrackerRepository.save(evacuationTracker);
                        } else {
                            evacuationTracker = new EvacuationTracker();
                            evacuationTracker.setPermanentCount(1);
                            evacuationTracker.setRecordedOn(new Date());
                            evacuationTrackerRepository.save(evacuationTracker);
                        }
                        System.out.println("Employee " + employee[1]);
                        System.out.println("2");

                    } else if (employee[1].equalsIgnoreCase("Contractor")) {
                        //TODO contract entry
                        List<EvacuationTracker> evacuationTrackers = evacuationTrackerRepository.findAll();
                        EvacuationTracker evacuationTracker;

                        if (!evacuationTrackers.isEmpty()) {
                            Collections.reverse(evacuationTrackers);
                            evacuationTracker = evacuationTrackers.get(0);
                            long employeeCount = evacuationTracker.getContractCount();
                            employeeCount = employeeCount + 1;
                            evacuationTracker.setContractCount(employeeCount);
                            evacuationTracker.setRecordedOn(new Date());
                            evacuationTrackerRepository.save(evacuationTracker);
                        } else {
                            evacuationTracker = new EvacuationTracker();
                            evacuationTracker.setContractCount(1);
                            evacuationTracker.setRecordedOn(new Date());
                            evacuationTrackerRepository.save(evacuationTracker);
                        }
                        System.out.println("Contractor " + employee[1]);
                        System.out.println("3");
                    }
                }
            }
        } catch (IOException e) {
//            System.out.println("*");
//            e.printStackTrace();
        }
    }
}
