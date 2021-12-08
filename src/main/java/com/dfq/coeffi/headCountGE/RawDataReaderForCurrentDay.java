package com.dfq.coeffi.headCountGE;

import com.dfq.coeffi.employeePermanentContract.entities.EmpPermanentContract;
import com.dfq.coeffi.employeePermanentContract.entities.PermanentContractAttendance;
import com.dfq.coeffi.employeePermanentContract.repositories.PermanentContractAttendanceRepo;
import com.dfq.coeffi.employeePermanentContract.services.PermanentContractService;
import com.dfq.coeffi.entity.hr.employee.AttendanceStatus;
import com.dfq.coeffi.entity.hr.employee.EmployeeType;
import com.dfq.coeffi.evacuationApi.*;
import com.dfq.coeffi.service.payroll.EmployeeAttendanceService;
import com.dfq.coeffi.util.DateUtil;
import com.dfq.coeffi.visitor.Entities.Visitor;
import com.dfq.coeffi.visitor.Services.VisitorService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;


@EnableScheduling
@Slf4j
@RestController
@Configuration
public class RawDataReaderForCurrentDay {

    @Autowired
    PermanentContractAttendanceRepo permanentContractAttendanceRepo;
    @Autowired
    VisitorService visitorService;
    @Autowired
    PermanentContractService permanentContractService;
    @Autowired
    InCsvRawRepository inCsvRawRepository;
    @Autowired
    OutCsvRawRepository outCsvRawRepository;
    @Autowired
    InsideCsvRawRepository insideCsvRawRepository;
    @Autowired
    private EvacuationTrackerRepository evacuationTrackerRepository;
    @Autowired
    private RawDataReader rawDataReader;

//    Logger logger = LoggerFactory.getLogger(RawDataReaderForCurrentDay.class);

    @Scheduled(initialDelay = 1000, fixedRate = 90000)
    @GetMapping("raw-read-in-csv-ui")
    public void readCsvInForCurrentDay() throws Exception {
//        logger.info("-------Start--------");
        System.out.println("-------------Start-----------");
        System.out.println("Reading In");
        SimpleDateFormat dft = new SimpleDateFormat("yyyy-MM-dd");
        Date today = new Date();
        Date todayDate = rawDataReader.mySqlFormatDate(today);
        List<InCsvRaw> inCsvRaws = inCsvRawRepository.findByTodayDate(DateUtil.getTodayDate());
        System.out.println("In list :" + inCsvRaws.size());
        for (InCsvRaw employee : inCsvRaws) {
            employee.setProcessed(true);
            inCsvRawRepository.save(employee);
            System.out.println("READING-in-Current ------" + employee.getSsoId() + " " + employee.getLogId());
            if (employee.getType().equalsIgnoreCase("Temp") || employee.getType().equalsIgnoreCase("Visitor")) {
                // TODO Visitor entry
                String name = employee.getName();
                String mobileId = String.valueOf(employee.getSsoId());
                String type = employee.getType();
                //----------------------------------
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                Date entryDate = employee.getAccessDate();
                entryDate = rawDataReader.mySqlFormatDate(entryDate);
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
                    visitor.setVisitType(employee.getType());
                    visitor.setPersonToVisit("");
                    visitor.setEntryBodyTemperature(0);
                    visitor.setMaskWearing(false);
                    visitor.setTimeSlot(0);
                    visitor.setEntryGateNumber(employee.getCId());
                    visitor.setInTime(entryDate);
                    visitor.setFirstName(name);
                    visitorService.saveVisitor(visitor);
                }

//                        System.out.println("1");
            } else if (employee.getType().equalsIgnoreCase("Employee")) {
//                System.out.println("Employee");
                //TODO employeeEntry
                String name = employee.getName();
                String employeeCode = employee.getSsoId();
                String type = employee.getType();
                //----------------------------------
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                Date entryDate = employee.getAccessDate();
                entryDate = rawDataReader.mySqlFormatDate(entryDate);
                //----------------------------------
                EmpPermanentContract empPermanentContract = permanentContractService.get(employeeCode);
                if (empPermanentContract == null) {
                    empPermanentContract = new EmpPermanentContract();
                    empPermanentContract.setCreatedOn(new Date());
                    empPermanentContract.setEmployeeCode(employeeCode);
                    empPermanentContract.setEmployeeType(EmployeeType.PERMANENT_CONTRACT);
                    empPermanentContract.setFirstName(name);
                    empPermanentContract.setLastName("");
                    empPermanentContract.setCardId(employee.getBId());
                    empPermanentContract = permanentContractService.save(empPermanentContract);
                } else {
//                            System.out.println("registered already");
                }

                PermanentContractAttendance newEmployeeAttendance = permanentContractAttendanceRepo.findByEmployeeCodeAndInTime(employeeCode, entryDate);
//                System.out.println("new eeeeee "+newEmployeeAttendance.getEmployeeCode() + employeeCode);
                if (newEmployeeAttendance == null) {
                    System.out.println("Employee in");
                    newEmployeeAttendance = new PermanentContractAttendance();
                    newEmployeeAttendance.setAttendanceStatus(AttendanceStatus.PRESENT);
                    newEmployeeAttendance.setEmpId(empPermanentContract.getId());
                    newEmployeeAttendance.setEmployeeCode(empPermanentContract.getEmployeeCode());
                    newEmployeeAttendance.setInTime(entryDate);
                    newEmployeeAttendance.setMaskWearing(false);
                    newEmployeeAttendance.setEntryBodyTemperature(0);
                    newEmployeeAttendance.setEntryGateNumber(employee.getCId());
                    newEmployeeAttendance.setMarkedOn(entryDate);
                    newEmployeeAttendance.setRecordedTime(entryDate);
                    newEmployeeAttendance.setEmployeeName(name);
                    employee.setProcessed(true);
                    permanentContractAttendanceRepo.save(newEmployeeAttendance);
                    inCsvRawRepository.save(employee);
//                            System.out.println("GREAT");
//                    System.out.println("22222222222222222222222222222222222222222222222222");
                }
//                        System.out.println("GREAT");
//                        System.out.println("22222222222222222222222222222222222222222222222222");

            } else if (employee.getType().equalsIgnoreCase("Contractor")) {
                //TODO contract entry
                String name = employee.getName();
                String employeeCode = employee.getSsoId();
                String type = employee.getType();
                //----------------------------------
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                Date entryDate = employee.getAccessDate();
                entryDate = rawDataReader.mySqlFormatDate(entryDate);
                //----------------------------------
                EmpPermanentContract empPermanentContract = permanentContractService.get(employeeCode);
                if (empPermanentContract == null) {
                    empPermanentContract = new EmpPermanentContract();
                    empPermanentContract.setCreatedOn(new Date());
                    empPermanentContract.setEmployeeCode(employeeCode);
                    empPermanentContract.setEmployeeType(EmployeeType.CONTRACT);
                    empPermanentContract.setFirstName(name);
                    empPermanentContract.setLastName("");
                    empPermanentContract.setCardId(employee.getBId());
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
                    newEmployeeAttendance.setEntryGateNumber(employee.getCId());
                    newEmployeeAttendance.setMarkedOn(entryDate);
                    newEmployeeAttendance.setRecordedTime(entryDate);
                    newEmployeeAttendance.setEmployeeName(name);
                    employee.setProcessed(true);
                    permanentContractAttendanceRepo.save(newEmployeeAttendance);
                    inCsvRawRepository.save(employee);
                }
//                        System.out.println("GREAT");
//                        System.out.println("33333333333333333333333333333333333333333333333333333333");

            }
            getInsideEmployeesData();
        }
        readOutCsv();
        getInsideEmployeesData();
        System.out.println("-------------End-----------");
    }

    //Only for current day
    @GetMapping("raw-read-out-csv-ui")
//    @Scheduled(initialDelay = 1002, fixedRate = 20000)
    public void readOutCsv() throws Exception {
        System.out.println("reading out");
        List<OutCsvRaw> outCsvRaws = outCsvRawRepository.findByTodayDate(DateUtil.getTodayDate());
        for (OutCsvRaw employee : outCsvRaws) {
            if (employee.getType().equalsIgnoreCase("Temp") || employee.getType().equalsIgnoreCase("Visitor")) {
                // TODO Visitor entry
                String name = employee.getName();
                String mobileId = employee.getSsoId();
                String type = employee.getType();
                //----------------------------------
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                Date entryDate = employee.getAccessDate();
                entryDate = rawDataReader.mySqlFormatDate(entryDate);
                //----------------------------------
                List<Visitor> visitorBymobNos = visitorService.getByMobileNumberAndDateGEBE(entryDate, mobileId);
                boolean setFlag = false;
                System.out.println("visitorBymobNos :" + visitorBymobNos.size());
                for (Visitor visitorBymobNo : visitorBymobNos) {
                    if (visitorBymobNo != null && visitorBymobNo.getOutTime() == null) {

                        if (entryDate.after(visitorBymobNo.getInTime())) {
                            Visitor visitor = visitorBymobNo;
                            SimpleDateFormat sdfHr = new SimpleDateFormat("HH.mm");
                            String checkInTime = sdfHr.format(entryDate);
                            visitor.setCheckOutTime(checkInTime);
                            visitor.setOutTime(entryDate);
                            visitor.setExitGateNumber(employee.getCId());
                            visitorService.saveVisitor(visitor);
                        }

                    } else if (visitorBymobNo != null && visitorBymobNo.getOutTime() != null) {
                        if (entryDate.before(visitorBymobNo.getOutTime()) && entryDate.after(visitorBymobNo.getInTime())) {
                            Visitor visitor = visitorBymobNo;
                            SimpleDateFormat sdfHr = new SimpleDateFormat("HH.mm");
                            String checkInTime = sdfHr.format(entryDate);
                            visitor.setCheckOutTime(checkInTime);
                            visitor.setOutTime(entryDate);
                            visitor.setExitGateNumber(employee.getCId());
                            visitorService.saveVisitor(visitor);
                        }
                    }
                }
//                        System.out.println("1");
            } else if (employee.getType().equalsIgnoreCase("Employee")) {
                //TODO employeeEntry
                String name = employee.getName();
                String employeeCode = employee.getSsoId();
                String type = employee.getType();
                //----------------------------------
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                Date entryDate = employee.getAccessDate();
                entryDate = rawDataReader.mySqlFormatDate(entryDate);
                //----------------------------------
                EmpPermanentContract empPermanentContract = permanentContractService.get(employeeCode);
                if (empPermanentContract != null) {
                    List<PermanentContractAttendance> employeeAttendances = permanentContractAttendanceRepo.getEmployeeAttendanceByEmployeeIdGEBE(entryDate, empPermanentContract.getId());
                    boolean setFlag = false;
                    for (PermanentContractAttendance employeeAttendance : employeeAttendances) {
                        if (setFlag == false) {
                            if (employeeAttendance != null && employeeAttendance.getInTime() != null && employeeAttendance.getOutTime() == null && employeeAttendance.getAttendanceStatus().equals(AttendanceStatus.PRESENT)) {
                                if (entryDate.after(employeeAttendance.getInTime())) {
                                    employeeAttendance.setOutTime(entryDate);
                                    employeeAttendance.setRecordedTime(entryDate);
                                    employeeAttendance.setExitGateNumber(employee.getCId());
                                    //TODO for dubai
//                                          calculateTotalStayTime(employeeAttendance);
                                    List<PermanentContractAttendance> permanentContractAttendancesOld = permanentContractAttendanceRepo.findByEmployeeCodeAndOutTime(employeeCode, entryDate);

                                    if (permanentContractAttendancesOld.isEmpty()) {
//                                        System.out.println("employeeCode " + employeeCode + " date " + entryDate);
                                        employee.setProcessed(true);
                                        permanentContractAttendanceRepo.save(employeeAttendance);
                                        outCsvRawRepository.save(employee);

                                    }
                                } else {
                                    Date yesterdayDate = rawDataReader.yesterday(entryDate);
                                    rawDataReader.getYesterdaysInData(yesterdayDate, empPermanentContract.getId(), entryDate, employeeCode, false);
                                }
                            } else if (employeeAttendance != null && employeeAttendance.getInTime() != null && employeeAttendance.getOutTime() != null) {
                                if (entryDate.after(employeeAttendance.getInTime()) && entryDate.before(employeeAttendance.getOutTime())) {
                                    employeeAttendance.setOutTime(entryDate);
                                    employeeAttendance.setRecordedTime(entryDate);
                                    employeeAttendance.setExitGateNumber(employee.getCId());
                                    List<PermanentContractAttendance> permanentContractAttendancesOld = permanentContractAttendanceRepo.findByEmployeeCodeAndOutTime(employeeCode, entryDate);
                                    if (permanentContractAttendancesOld.isEmpty()) {
                                        employee.setProcessed(true);
                                        permanentContractAttendanceRepo.save(employeeAttendance);
                                        outCsvRawRepository.save(employee);
                                    }
                                }
                            }
                        }
                    }
                    if (employeeAttendances.isEmpty()) {
                        employee.setProcessed(true);
//                        System.out.println("Entered 1 " + entryDate);
                        Date yesterdayDate = rawDataReader.yesterday(entryDate);
                        rawDataReader.getYesterdaysInData(yesterdayDate, empPermanentContract.getId(), entryDate, employeeCode, true);
                    }
//                            System.out.println("2");
                }

            } else if (employee.getType().equalsIgnoreCase("Contractor")) {
                //TODO contract entry
                String name = employee.getName();
                String employeeCode = employee.getSsoId();
                String type = employee.getType();
                //----------------------------------
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                Date entryDate = employee.getAccessDate();
                entryDate = rawDataReader.mySqlFormatDate(entryDate);
                //----------------------------------
                EmpPermanentContract empPermanentContract = permanentContractService.get(employeeCode);
                if (empPermanentContract != null) {
                    List<PermanentContractAttendance> employeeAttendances = permanentContractAttendanceRepo.getEmployeeAttendanceByEmployeeIdGEBE(entryDate, empPermanentContract.getId());
                    boolean setFlag = false;
                    for (PermanentContractAttendance employeeAttendance : employeeAttendances) {
                        if (setFlag == false) {
                            if (employeeAttendance != null && employeeAttendance.getInTime() != null && employeeAttendance.getOutTime() == null && employeeAttendance.getAttendanceStatus().equals(AttendanceStatus.PRESENT)) {
                                if (entryDate.after(employeeAttendance.getInTime())) {
                                    employeeAttendance.setOutTime(entryDate);
                                    employeeAttendance.setRecordedTime(entryDate);
                                    employeeAttendance.setExitGateNumber(employee.getCId());
                                    //TODO for dubai
//                                       calculateTotalStayTime(employeeAttendance);
                                    List<PermanentContractAttendance> permanentContractAttendancesOld = permanentContractAttendanceRepo.findByEmployeeCodeAndOutTime(employeeCode, entryDate);
                                    if (permanentContractAttendancesOld.isEmpty()) {
                                        employee.setProcessed(true);
                                        outCsvRawRepository.save(employee);
                                        permanentContractAttendanceRepo.save(employeeAttendance);
                                    }
                                } else {
                                    Date yesterdayDate = rawDataReader.yesterday(entryDate);
                                    rawDataReader.getYesterdaysInData(yesterdayDate, empPermanentContract.getId(), entryDate, employeeCode, false);
                                }
                            } else if (employeeAttendance != null && employeeAttendance.getInTime() != null && employeeAttendance.getOutTime() != null) {
                                if (entryDate.after(employeeAttendance.getInTime()) && entryDate.before(employeeAttendance.getOutTime())) {
                                    employeeAttendance.setOutTime(entryDate);
                                    employeeAttendance.setRecordedTime(entryDate);
                                    employeeAttendance.setExitGateNumber(employee.getCId());
                                    List<PermanentContractAttendance> permanentContractAttendancesOld = permanentContractAttendanceRepo.findByEmployeeCodeAndOutTime(employeeCode, entryDate);
                                    if (permanentContractAttendancesOld.isEmpty()) {
                                        employee.setProcessed(true);
                                        outCsvRawRepository.save(employee);
                                        permanentContractAttendanceRepo.save(employeeAttendance);
                                    }
                                }
                            }
                        }
                    }
                    if (employeeAttendances.isEmpty()) {
                        Date yesterdayDate = rawDataReader.yesterday(entryDate);
                        rawDataReader.getYesterdaysInData(yesterdayDate, empPermanentContract.getId(), entryDate, employeeCode, true);
                    }
                }
//                        System.out.println("3");
            }
            getInsideEmployeesData();
        }
    }

    //TODO uncomment for ASTRA not for GE
//    @Scheduled(initialDelay = 5000, fixedRate = 60000)
    public void getInsideEmployeesData() throws Exception {
        if (false) { //False for GE , true for Others
            List<PermanentContractAttendance> permanentContractAttendances = permanentContractService.getTodayMarkedEmployeeAttendance(DateUtil.getTodayDate());

            System.out.println("Updating Inside");
//        Collections.reverse(permanentContractAttendances);
            List<Visitor> visitors = visitorService.getByMobileDate(DateUtil.getTodayDate());
//        Collections.reverse(visitors);
            if (permanentContractAttendances.isEmpty() && visitors.isEmpty()) {
//            System.out.println("Deleting inside raw");
                insideCsvRawRepository.deleteAll();
            }
            for (PermanentContractAttendance permanentContractAttendance : permanentContractAttendances) {
                System.out.println("Updating inside raw");
                if (permanentContractAttendance.getOutTime() == null) {
                    InsideCsvRaw insideCsvRawOld = insideCsvRawRepository.findBySsoId(permanentContractAttendance.getEmployeeCode());
                    if (insideCsvRawOld == null) {
                        InsideCsvRaw insideCsvRaw = new InsideCsvRaw();
                        insideCsvRaw.setName(permanentContractAttendance.getEmployeeName());
                        insideCsvRaw.setRecordedOn(permanentContractAttendance.getInTime());
                        insideCsvRaw.setSsoId(permanentContractAttendance.getEmployeeCode());
                        insideCsvRaw.setType("Contractor");
                        EmpPermanentContract empPermanentContract = permanentContractService.get(permanentContractAttendance.getEmpId());
                        if (empPermanentContract != null) {
                            insideCsvRaw.setName(empPermanentContract.getFirstName());
                            if (empPermanentContract.getEmployeeType().equals(EmployeeType.PERMANENT_CONTRACT)) {
                                insideCsvRaw.setType("Employee");
                            } else {
                                insideCsvRaw.setType("Contractor");
                            }
                            insideCsvRawRepository.save(insideCsvRaw);
                        }
                    }

                } else {
                    InsideCsvRaw insideCsvRawOld = insideCsvRawRepository.findBySsoId(permanentContractAttendance.getEmployeeCode());
                    if (insideCsvRawOld != null) {
                        insideCsvRawRepository.delete(insideCsvRawOld);
                    }
                }
            }

            rawDataReader.readCsvInside();
            for (Visitor visitor : visitors) {
                if (visitor.getOutTime() == null) {
                    InsideCsvRaw insideCsvRawOld = insideCsvRawRepository.findBySsoId(visitor.getMobileNumber());
                    if (insideCsvRawOld == null) {
                        InsideCsvRaw insideCsvRaw = new InsideCsvRaw();
                        insideCsvRaw.setName(visitor.getFirstName());
                        insideCsvRaw.setRecordedOn(new Date());
                        insideCsvRaw.setSsoId(visitor.getMobileNumber());
                        insideCsvRaw.setType(visitor.getVisitType());
                        insideCsvRawRepository.save(insideCsvRaw);
                    }

                } else {
                    InsideCsvRaw insideCsvRawOld = insideCsvRawRepository.findBySsoId(visitor.getMobileNumber());
                    if (insideCsvRawOld != null) {
                        insideCsvRawRepository.delete(insideCsvRawOld);
                    }
                }
            }

        }
        rawDataReader.readCsvInside();
    }
}
