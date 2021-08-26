package com.dfq.coeffi.evacuationApi;

import com.dfq.coeffi.controller.BaseController;
import com.dfq.coeffi.employeePermanentContract.entities.EmpPermanentContract;
import com.dfq.coeffi.employeePermanentContract.entities.PermanentContractAttendance;
import com.dfq.coeffi.employeePermanentContract.repositories.PermanentContractAttendanceRepo;
import com.dfq.coeffi.employeePermanentContract.services.PermanentContractService;
import com.dfq.coeffi.entity.hr.employee.AttendanceStatus;
import com.dfq.coeffi.entity.hr.employee.EmployeeType;
import com.dfq.coeffi.service.payroll.EmployeeAttendanceService;
import com.dfq.coeffi.visitor.Entities.Visitor;
import com.dfq.coeffi.visitor.Services.VisitorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@Slf4j
@EnableScheduling
public class ExeController extends BaseController {

    @Autowired
    PermanentContractAttendanceRepo permanentContractAttendanceRepo;
    @Autowired
    EmployeeAttendanceService employeeAttendanceService;
    @Autowired
    VisitorService visitorService;
    @Autowired
    PermanentContractService permanentContractService;


    @GetMapping("executable/read-csv")
//    @Scheduled(initialDelay = 1105, fixedRate = 2010)
    public void readCsvIn() throws Exception {
        String line = "";
        String splitBy = ",";
        try {
//            BufferedReader br = new BufferedReader(new FileReader("C:\\Users\\502548453\\Desktop\\EMR\\IN.csv"));
            BufferedReader br = new BufferedReader(new FileReader("C:\\Users\\502548453\\Desktop\\EMRcoeffi\\IN.csv"));
//            BufferedReader br = new BufferedReader(new FileReader("E:\\Customer_Builds\\GEBE\\Spectra_Headcount\\IN.csv"));
            while ((line = br.readLine()) != null) {
                String[] employee = line.split(splitBy);
                if (employee.length > 2) {
//                    System.out.println("employee[2]" + employee[2] + "employee[4] " + employee[4]);
                    if (employee[2].equalsIgnoreCase("Temp") || employee[2].equalsIgnoreCase("Visitor")) {
//                        System.out.println("READING-inside");
                        // TODO Visitor entry
                        String name = employee[0];
                        String mobileId = employee[1];
                        String type = employee[2];
                        //----------------------------------
                        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                        Date entryDate = df.parse(employee[4]);
                        entryDate = mySqlFormatDate(entryDate);
                        //----------------------------------

//                        System.out.println("entry: " + entryDate + " mobile " + mobileId);
                        Visitor visitor = visitorService.getByEmpIdAndDateAndInTime(mobileId, entryDate);
                        if (visitor == null) {
                            visitor = new Visitor();
                            SimpleDateFormat sdfHr = new SimpleDateFormat("HH.mm");
                            String checkInTime = sdfHr.format(entryDate);
                            visitor.setLoggedOn(entryDate);
                            visitor.setCheckInTime(checkInTime);
                            visitor.setMobileNumber(mobileId);
                            visitor.setFirstName(name);
                            visitor.setEmail("");
                            visitor.setVisitType(employee[2]);
                            visitor.setPersonToVisit("");
                            visitor.setEntryBodyTemperature(0);
                            visitor.setMaskWearing(false);
                            visitor.setTimeSlot(0);
                            visitor.setEntryGateNumber("01");
                            visitor.setInTime(entryDate);
                            visitorService.saveVisitor(visitor);
                        }
//                        System.out.println("1");
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

                        PermanentContractAttendance newEmployeeAttendance = permanentContractAttendanceRepo.findByEmployeeCodeAndInTime(employeeCode, entryDate);
                        if (newEmployeeAttendance == null) {
                            newEmployeeAttendance = new PermanentContractAttendance();
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
//                            System.out.println("GREAT");
//                            System.out.println("22222222222222222222222222222222222222222222222222");
                        }
//                        System.out.println("GREAT");
//                        System.out.println("22222222222222222222222222222222222222222222222222");

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
                        PermanentContractAttendance newEmployeeAttendance = permanentContractAttendanceRepo.findByEmployeeCodeAndInTime(employeeCode, entryDate);
                        if (newEmployeeAttendance == null) {
                            newEmployeeAttendance = new PermanentContractAttendance();
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
                        }
//                        System.out.println("GREAT");
//                        System.out.println("33333333333333333333333333333333333333333333333333333333");

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
//    public void readCsvOut() throws Exception {
//        String line = "";
//        String splitBy = ",";
//        try {
//            //GEBE PATH |
////            BufferedReader br = new BufferedReader(new FileReader("C:\\Users\\502548453\\Desktop\\EMR\\OUT.csv"));
//            BufferedReader br = new BufferedReader(new FileReader("C:\\Users\\502548453\\Desktop\\EMRcoeffi\\OUT.csv"));
////            BufferedReader br = new BufferedReader(new FileReader("E:\\Customer_Builds\\GEBE\\Spectra_Headcount\\OUT.csv"));
//            while ((line = br.readLine()) != null) {
//                String[] employee = line.split(splitBy);
//                if (employee.length > 2) {
//                    if (employee[2].equalsIgnoreCase("Temp") || employee[2].equalsIgnoreCase("Visitor")) {
//                        // TODO Visitor entry
//                        String name = employee[0];
//                        String mobileId = employee[1];
//                        String type = employee[2];
//                        //----------------------------------
//                        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
//                        Date entryDate = df.parse(employee[4]);
//                        entryDate = mySqlFormatDate(entryDate);
//                        //----------------------------------
//                        List<Visitor> visitorBymobNos = visitorService.getByMobileNumberAndDateGEBE(entryDate, mobileId);
//                        boolean setFlag = false;
//                        for (Visitor visitorBymobNo : visitorBymobNos) {
//                            if (visitorBymobNo != null && visitorBymobNo.getOutTime() == null) {
//
//                                if (entryDate.after(visitorBymobNo.getInTime())) {
//                                    Visitor visitor = visitorBymobNo;
//                                    SimpleDateFormat sdfHr = new SimpleDateFormat("HH.mm");
//                                    String checkInTime = sdfHr.format(entryDate);
//                                    visitor.setCheckOutTime(checkInTime);
//                                    visitor.setOutTime(entryDate);
//                                    visitorService.saveVisitor(visitor);
//                                }
//
//                            } else if (visitorBymobNo != null && visitorBymobNo.getOutTime() != null) {
//                                if (entryDate.before(visitorBymobNo.getOutTime()) && entryDate.after(visitorBymobNo.getInTime())) {
//                                    Visitor visitor = visitorBymobNo;
//                                    SimpleDateFormat sdfHr = new SimpleDateFormat("HH.mm");
//                                    String checkInTime = sdfHr.format(entryDate);
//                                    visitor.setCheckOutTime(checkInTime);
//                                    visitor.setOutTime(entryDate);
//                                    visitorService.saveVisitor(visitor);
//                                }
//                            }
//                        }
////                        System.out.println("1");
//                    } else if (employee[2].equalsIgnoreCase("Employee")) {
//                        //TODO employeeEntry
//                        String name = employee[0];
//                        String employeeCode = employee[1];
//                        String type = employee[2];
//                        //----------------------------------
//                        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
//                        Date entryDate = df.parse(employee[4]);
//                        entryDate = mySqlFormatDate(entryDate);
//                        //----------------------------------
//                        EmpPermanentContract empPermanentContract = permanentContractService.get(employeeCode);
//                        if (empPermanentContract != null) {
//                            List<PermanentContractAttendance> employeeAttendances = permanentContractAttendanceRepo.getEmployeeAttendanceByEmployeeIdGEBE(entryDate, empPermanentContract.getId());
//                            boolean setFlag = false;
//                            for (PermanentContractAttendance employeeAttendance : employeeAttendances) {
//                                if (setFlag == false) {
//                                    if (employeeAttendance != null && employeeAttendance.getInTime() != null && employeeAttendance.getOutTime() == null && employeeAttendance.getAttendanceStatus().equals(AttendanceStatus.PRESENT)) {
//                                        if (entryDate.after(employeeAttendance.getInTime())) {
//                                            employeeAttendance.setOutTime(entryDate);
//                                            employeeAttendance.setRecordedTime(entryDate);
//                                            //TODO for dubai
////                                          calculateTotalStayTime(employeeAttendance);
//                                            PermanentContractAttendance permanentContractAttendance = permanentContractAttendanceRepo.findByEmployeeCodeAndOutTime(employeeCode, entryDate);
//                                            if (permanentContractAttendance == null) {
//                                                permanentContractAttendanceRepo.save(employeeAttendance);
//                                            }
//                                        } else {
//                                            Date yesterdayDate = yesterday(entryDate);
//                                            getYesterdaysInData(yesterdayDate, empPermanentContract.getId(), entryDate);
//                                        }
//                                    } else if (employeeAttendance != null && employeeAttendance.getInTime() != null && employeeAttendance.getOutTime() != null) {
//                                        if (entryDate.after(employeeAttendance.getInTime()) && entryDate.before(employeeAttendance.getOutTime())) {
//                                            employeeAttendance.setOutTime(entryDate);
//                                            employeeAttendance.setRecordedTime(entryDate);
//                                            PermanentContractAttendance permanentContractAttendance = permanentContractAttendanceRepo.findByEmployeeCodeAndOutTime(employeeCode, entryDate);
//                                            if (permanentContractAttendance == null) {
//                                                permanentContractAttendanceRepo.save(employeeAttendance);
//                                            }
//                                        }
//                                    }
//                                }
//                            }
//                            if (employeeAttendances.isEmpty()) {
//                                Date yesterdayDate = yesterday(entryDate);
//                                getYesterdaysInData(yesterdayDate, empPermanentContract.getId(), entryDate);
//                            }
////                            System.out.println("2");
//                        }
//
//                    } else if (employee[2].equalsIgnoreCase("Contractor")) {
//                        //TODO contract entry
//                        String name = employee[0];
//                        String employeeCode = employee[1];
//                        String type = employee[2];
//                        //----------------------------------
//                        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
//                        Date entryDate = df.parse(employee[4]);
//                        entryDate = mySqlFormatDate(entryDate);
//                        //----------------------------------
//                        EmpPermanentContract empPermanentContract = permanentContractService.get(employeeCode);
//                        if (empPermanentContract != null) {
//                            List<PermanentContractAttendance> employeeAttendances = permanentContractAttendanceRepo.getEmployeeAttendanceByEmployeeIdGEBE(entryDate, empPermanentContract.getId());
//                            boolean setFlag = false;
//                            for (PermanentContractAttendance employeeAttendance : employeeAttendances) {
//                                if (setFlag == false) {
//                                    if (employeeAttendance != null && employeeAttendance.getInTime() != null && employeeAttendance.getOutTime() == null && employeeAttendance.getAttendanceStatus().equals(AttendanceStatus.PRESENT)) {
//                                        if (entryDate.after(employeeAttendance.getInTime())) {
//                                            employeeAttendance.setOutTime(entryDate);
//                                            employeeAttendance.setRecordedTime(entryDate);
//                                            //TODO for dubai
////                                       calculateTotalStayTime(employeeAttendance);
//                                            PermanentContractAttendance permanentContractAttendance = permanentContractAttendanceRepo.findByEmployeeCodeAndOutTime(employeeCode, entryDate);
//                                            if (permanentContractAttendance == null) {
//                                                permanentContractAttendanceRepo.save(employeeAttendance);
//                                            }
//                                        } else {
//                                            Date yesterdayDate = yesterday(entryDate);
//                                            getYesterdaysInData(yesterdayDate, empPermanentContract.getId(), entryDate);
//                                        }
//                                    } else if (employeeAttendance != null && employeeAttendance.getInTime() != null && employeeAttendance.getOutTime() != null) {
//                                        if (entryDate.after(employeeAttendance.getInTime()) && entryDate.before(employeeAttendance.getOutTime())) {
//                                            employeeAttendance.setOutTime(entryDate);
//                                            employeeAttendance.setRecordedTime(entryDate);
//                                            PermanentContractAttendance permanentContractAttendance = permanentContractAttendanceRepo.findByEmployeeCodeAndOutTime(employeeCode, entryDate);
//                                            if (permanentContractAttendance == null) {
//                                                permanentContractAttendanceRepo.save(employeeAttendance);
//                                            }
//                                        }
//                                    }
//                                }
//                            }
//                            if (employeeAttendances.isEmpty()) {
//                                Date yesterdayDate = yesterday(entryDate);
//                                getYesterdaysInData(yesterdayDate, empPermanentContract.getId(), entryDate);
//                            }
//                        }
////                        System.out.println("3");
//                    }
//                }
//            }
//        } catch (IOException | ParseException e) {
////            System.out.println("*");
////            e.printStackTrace();
//        }
//    }


    @GetMapping("exe-click")
    @Scheduled(initialDelay = 2015, fixedRate = 30000)
    public void exeClick() {
        try {
            // Command to create an external process
            String command = "D:\\EMR DEC\\GEBEEMR.exe";
            // Running the above command
            Runtime run = Runtime.getRuntime();
            Process proc = run.exec(command, null, new File("D:\\EMR DEC\\"));
        } catch (IOException e) {
//            System.out.println("wrong path");
        }
    }

    //    @Scheduled(initialDelay = 2018, fixedRate = 30000)
    public void exeClickNotePad() {
        try {
            // Command to create an external process
            String command = "E:\\Tools\\nginx-1.16.1\\b.txt";
            // Running the above command
            Runtime run = Runtime.getRuntime();
            Process proc = run.exec(command, null, new File("E:\\Tools\\nginx-1.16.1\\"));
            System.out.println("triggered");
        } catch (IOException e) {
            System.out.println("wrong path" + e);
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


    @GetMapping("executable/read-inside-csv")
//    @Scheduled(initialDelay = 2115, fixedRate = 1090)
    public void readCsvInside() throws Exception {

        String line = "";
        String splitBy = ",";
        try {
            //GEBE PATH |
            BufferedReader br = new BufferedReader(new FileReader("C:\\Users\\502548453\\Desktop\\EMRcoeffi\\INSIDE.csv"));
//            BufferedReader br = new BufferedReader(new FileReader("C:\\Users\\502548453\\Desktop\\EMR\\INSIDE.csv"));
//            BufferedReader br = new BufferedReader(new FileReader("E:\\Customer_Builds\\GEBE\\Spectra_Headcount\\OUT.csv"));
            List<EvacuationTracker> evacuationTrackers = evacuationTrackerRepository.findAll();
            EvacuationTracker evacuationTracker = new EvacuationTracker();
            if (!evacuationTrackers.isEmpty()) {
                Collections.reverse(evacuationTrackers);
                evacuationTracker = evacuationTrackers.get(0);
                evacuationTracker.setVisitorCount(0);
                evacuationTracker.setPermanentCount(0);
                evacuationTracker.setContractCount(0);
                evacuationTracker.setTempCount(0);
            } else {
                evacuationTracker = new EvacuationTracker();
                evacuationTracker.setVisitorCount(0);
                evacuationTracker.setPermanentCount(0);
                evacuationTracker.setContractCount(0);
                evacuationTracker.setTempCount(0);
            }
            while ((line = br.readLine()) != null) {
                String[] employee = line.split(splitBy);
                if (true) {
                    if (employee.length > 1) {
                        if (employee[1].equalsIgnoreCase("Visitor")) {
                            // TODO Visitor entry
//                            System.out.println("visitorCount " + employee[1]);
                            evacuationTracker = evacuationTracker;
                            long visitorCount = evacuationTracker.getVisitorCount();
                            visitorCount = visitorCount + 1;
                            evacuationTracker.setVisitorCount(visitorCount);
                            evacuationTracker.setRecordedOn(new Date());
//                        evacuationTrackerRepository.save(evacuationTracker);

                        } else if (employee[1].equalsIgnoreCase("Temp")) {
                            // TODO Visitor entry
//                            System.out.println("visitorCount " + employee[1]);
                            evacuationTracker = evacuationTracker;
                            long visitorCount = evacuationTracker.getTempCount();
                            visitorCount = visitorCount + 1;
                            evacuationTracker.setTempCount(visitorCount);
                            evacuationTracker.setRecordedOn(new Date());
//                        evacuationTrackerRepository.save(evacuationTracker);

                        } else if (employee[1].equalsIgnoreCase("Employee")) {
                            //TODO employeeEntry
                            evacuationTracker = evacuationTracker;
                            long employeeCount = evacuationTracker.getPermanentCount();
                            employeeCount = employeeCount + 1;
                            evacuationTracker.setPermanentCount(employeeCount);
                            evacuationTracker.setRecordedOn(new Date());
//                        evacuationTrackerRepository.save(evacuationTracker);

//                            System.out.println("Employee " + employee[1]);
//                            System.out.println("2");

                        } else if (employee[1].equalsIgnoreCase("Contractor")) {
                            //TODO contract entry
                            evacuationTracker = evacuationTracker;
                            long employeeCount = evacuationTracker.getContractCount();
                            employeeCount = employeeCount + 1;
                            evacuationTracker.setContractCount(employeeCount);
                            evacuationTracker.setRecordedOn(new Date());
//                            System.out.println("Contractor " + employee[1]);
//                            System.out.println("3");
                        }
                    }
                }
            }
            evacuationTrackerRepository.save(evacuationTracker);
        } catch (IOException e) {
//            System.out.println("*");
//            e.printStackTrace();
        }
    }

    public List<PermanentContractAttendance> getYesterdaysInData(Date yest, long id, Date todayDate) {
        Date yesterday = yest;
        List<PermanentContractAttendance> permanentContractAttendances = new ArrayList<>();
        List<PermanentContractAttendance> permanentContractAttendanceListYesterday = permanentContractAttendanceRepo.getEmployeeAttendanceByEmployeeIdGEBE(yesterday, id);
        for (PermanentContractAttendance permanentContractAttendance : permanentContractAttendanceListYesterday) {
            if (permanentContractAttendance.getOutTime() == null) {
                if (todayDate.after(permanentContractAttendance.getInTime())) {
                    permanentContractAttendance.setOutTime(todayDate);
                    permanentContractAttendanceRepo.save(permanentContractAttendance);
                }
            }
        }
        return permanentContractAttendances;

    }

    private Date yesterday(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, -1);
        return cal.getTime();
    }



}
