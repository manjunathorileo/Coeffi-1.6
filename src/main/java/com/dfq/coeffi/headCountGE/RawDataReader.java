package com.dfq.coeffi.headCountGE;

import com.dfq.coeffi.employeePermanentContract.entities.EmpPermanentContract;
import com.dfq.coeffi.employeePermanentContract.entities.PermanentContractAttendance;
import com.dfq.coeffi.employeePermanentContract.repositories.PermanentContractAttendanceRepo;
import com.dfq.coeffi.employeePermanentContract.services.PermanentContractService;
import com.dfq.coeffi.entity.hr.employee.AttendanceStatus;
import com.dfq.coeffi.entity.hr.employee.EmployeeType;
import com.dfq.coeffi.entity.payroll.EmployeeAttendance;
import com.dfq.coeffi.evacuationApi.*;
import com.dfq.coeffi.service.payroll.EmployeeAttendanceService;
import com.dfq.coeffi.util.DateUtil;
import com.dfq.coeffi.visitor.Entities.Visitor;
import com.dfq.coeffi.visitor.Services.VisitorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@Slf4j
@EnableScheduling
@Configuration
public class RawDataReader {
    @Autowired
    PermanentContractAttendanceRepo permanentContractAttendanceRepo;
    @Autowired
    EmployeeAttendanceService employeeAttendanceService;
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


//    @Scheduled(initialDelay = 3600000, fixedRate = 3600000)
    public void readCsvIn() throws Exception {
        String line = "";
        String splitBy = ",";
        List<InCsvRaw> inCsvRaws = inCsvRawRepository.findAll();
        for (InCsvRaw employee : inCsvRaws) {
//            System.out.println("READING-in");
            if (employee.getType().equalsIgnoreCase("Temp") || employee.getType().equalsIgnoreCase("Visitor")) {
                // TODO Visitor entry
                String name = employee.getName();
                String mobileId = employee.getSsoId();
                String type = employee.getType();
                //----------------------------------
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                Date entryDate = employee.getAccessDate();
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
                    permanentContractAttendanceRepo.save(newEmployeeAttendance);
//                            System.out.println("GREAT");
//                            System.out.println("22222222222222222222222222222222222222222222222222");
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
                    permanentContractAttendanceRepo.save(newEmployeeAttendance);
                }
//                        System.out.println("GREAT");
//                        System.out.println("33333333333333333333333333333333333333333333333333333333");

            }

        }

        List<OutCsvRaw> outCsvRaws = outCsvRawRepository.findAll();
        for (OutCsvRaw employee : outCsvRaws) {
//            System.out.println("Reading out");
            if (employee.getType().equalsIgnoreCase("Temp") || employee.getType().equalsIgnoreCase("Visitor")) {
                // TODO Visitor entry
                String name = employee.getName();
                String mobileId = employee.getSsoId();
                String type = employee.getType();
                //----------------------------------
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                Date entryDate = employee.getAccessDate();
                entryDate = mySqlFormatDate(entryDate);
                //----------------------------------
                List<Visitor> visitorBymobNos = visitorService.getByMobileNumberAndDateGEBE(entryDate, mobileId);
                boolean setFlag = false;
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
                entryDate = mySqlFormatDate(entryDate);
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
                                        permanentContractAttendanceRepo.save(employeeAttendance);
                                    }
                                } else {
                                    Date yesterdayDate = yesterday(entryDate);
                                    getYesterdaysInData(yesterdayDate, empPermanentContract.getId(), entryDate, employeeCode, false);
                                }
                            } else if (employeeAttendance != null && employeeAttendance.getInTime() != null && employeeAttendance.getOutTime() != null) {
                                if (entryDate.after(employeeAttendance.getInTime()) && entryDate.before(employeeAttendance.getOutTime())) {
                                    employeeAttendance.setOutTime(entryDate);
                                    employeeAttendance.setRecordedTime(entryDate);
                                    employeeAttendance.setExitGateNumber(employee.getCId());
                                    List<PermanentContractAttendance> permanentContractAttendancesOld = permanentContractAttendanceRepo.findByEmployeeCodeAndOutTime(employeeCode, entryDate);
                                    if (permanentContractAttendancesOld.isEmpty()) {
                                        permanentContractAttendanceRepo.save(employeeAttendance);
                                    }
                                }
                            }
                        }
                    }
                    if (employeeAttendances.isEmpty()) {
//                        System.out.println("Entered 1 " + entryDate);
                        Date yesterdayDate = yesterday(entryDate);
                        getYesterdaysInData(yesterdayDate, empPermanentContract.getId(), entryDate, employeeCode, true);
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
                entryDate = mySqlFormatDate(entryDate);
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
                                        permanentContractAttendanceRepo.save(employeeAttendance);
                                    }
                                } else {
                                    Date yesterdayDate = yesterday(entryDate);
                                    getYesterdaysInData(yesterdayDate, empPermanentContract.getId(), entryDate, employeeCode, false);
                                }
                            } else if (employeeAttendance != null && employeeAttendance.getInTime() != null && employeeAttendance.getOutTime() != null) {
                                if (entryDate.after(employeeAttendance.getInTime()) && entryDate.before(employeeAttendance.getOutTime())) {
                                    employeeAttendance.setOutTime(entryDate);
                                    employeeAttendance.setRecordedTime(entryDate);
                                    employeeAttendance.setExitGateNumber(employee.getCId());
                                    List<PermanentContractAttendance> permanentContractAttendancesOld = permanentContractAttendanceRepo.findByEmployeeCodeAndOutTime(employeeCode, entryDate);
                                    if (permanentContractAttendancesOld.isEmpty()) {
                                        permanentContractAttendanceRepo.save(employeeAttendance);
                                    }
                                }
                            }
                        }
                    }
                    if (employeeAttendances.isEmpty()) {
                        Date yesterdayDate = yesterday(entryDate);
                        getYesterdaysInData(yesterdayDate, empPermanentContract.getId(), entryDate, employeeCode, true);
                    }
                }
//                        System.out.println("3");
            }
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


//    @Scheduled(initialDelay = 6000, fixedRate = 2000)
    public void readCsvInside() throws Exception {
        System.out.println("Reading Inside");
        List<InsideCsvRaw> insideCsvRaws = insideCsvRawRepository.findAll();
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
        for (InsideCsvRaw employee : insideCsvRaws) {
            System.out.println("Updating evacuation");
                if (employee.getType().equalsIgnoreCase("Visitor")) {
                    // TODO Visitor entry
                            System.out.println("vis " );
                    evacuationTracker = evacuationTracker;
                    long visitorCount = evacuationTracker.getVisitorCount();
                    visitorCount = visitorCount + 1;
                    evacuationTracker.setVisitorCount(visitorCount);
                    evacuationTracker.setRecordedOn(new Date());
//                        evacuationTrackerRepository.save(evacuationTracker);

                } else if (employee.getType().equalsIgnoreCase("Temp")) {
                    // TODO Visitor entry
                            System.out.println("Temp " );
                    evacuationTracker = evacuationTracker;
                    long visitorCount = evacuationTracker.getTempCount();
                    visitorCount = visitorCount + 1;
                    evacuationTracker.setTempCount(visitorCount);
                    evacuationTracker.setRecordedOn(new Date());
//                        evacuationTrackerRepository.save(evacuationTracker);

                } else if (employee.getType().equalsIgnoreCase("Employee")) {
                    //TODO employeeEntry
                    System.out.println("emp " );
                    evacuationTracker = evacuationTracker;
                    long employeeCount = evacuationTracker.getPermanentCount();
                    employeeCount = employeeCount + 1;
                    evacuationTracker.setPermanentCount(employeeCount);
                    evacuationTracker.setRecordedOn(new Date());
//                        evacuationTrackerRepository.save(evacuationTracker);

//                            System.out.println("Employee " + employee[1]);
//                            System.out.println("2");

                } else if (employee.getType().equalsIgnoreCase("Contractor")) {
                    //TODO contract entry
                    System.out.println("emp " );
                    evacuationTracker = evacuationTracker;
                    long employeeCount = evacuationTracker.getContractCount();
                    employeeCount = employeeCount + 1;
                    evacuationTracker.setContractCount(employeeCount);
                    evacuationTracker.setRecordedOn(new Date());
//                            System.out.println("Contractor " + employee[1]);
//                            System.out.println("3");
                }
            }
        evacuationTrackerRepository.save(evacuationTracker);
    }

    public List<PermanentContractAttendance> getYesterdaysInData(Date yest, long id, Date todayDate, String empCode, boolean checkForOnlyOut) {
        Date yesterday = yest;
//        System.out.println("Entered 2 " + todayDate);
        List<PermanentContractAttendance> permanentContractAttendances = new ArrayList<>();
        List<PermanentContractAttendance> permanentContractAttendanceListYesterday = permanentContractAttendanceRepo.getEmployeeAttendanceByEmployeeIdGEBE(yesterday, id);
        for (PermanentContractAttendance permanentContractAttendanceYest : permanentContractAttendanceListYesterday) {
            if (permanentContractAttendanceYest.getOutTime() == null) {
                if (permanentContractAttendanceYest.getInTime() != null) {
                    if (todayDate.after(permanentContractAttendanceYest.getInTime())) {
                        List<PermanentContractAttendance> permanentContractAttendancesOld = permanentContractAttendanceRepo.findByEmployeeCodeAndOutTime(empCode, todayDate);
                        if (permanentContractAttendancesOld.isEmpty()) {
                            permanentContractAttendanceYest.setOutTime(todayDate);
                            permanentContractAttendanceRepo.save(permanentContractAttendanceYest);
                        }
                    }
                }
            }
        }
        if (permanentContractAttendanceListYesterday.isEmpty()) {
//            System.out.println("Entered 2 " + todayDate);
            Date dayBefore = DateUtil.dayBeforeyesterday(todayDate);
            getDayBeforeYesterdaysInData(dayBefore, id, todayDate, empCode, checkForOnlyOut);
        }
        return permanentContractAttendances;

    }

    public List<PermanentContractAttendance> getDayBeforeYesterdaysInData(Date daybefore, long id, Date todayDate, String empCode, boolean checkForOnlyOut) {
//        System.out.println("Entered 3 " + todayDate);
        Date dayBeforeYesterday = daybefore;
        List<PermanentContractAttendance> permanentContractAttendances = new ArrayList<>();
        List<PermanentContractAttendance> permanentContractAttendanceListDayBeforeYesterday = permanentContractAttendanceRepo.getEmployeeAttendanceByEmployeeIdGEBE(dayBeforeYesterday, id);
        for (PermanentContractAttendance permanentContractAttendanceDayBeforeYest : permanentContractAttendanceListDayBeforeYesterday) {
            if (permanentContractAttendanceDayBeforeYest.getOutTime() == null) {
                if (permanentContractAttendanceDayBeforeYest.getInTime() != null) {
                    if (todayDate.after(permanentContractAttendanceDayBeforeYest.getInTime())) {
                        List<PermanentContractAttendance> permanentContractAttendancesOld = permanentContractAttendanceRepo.findByEmployeeCodeAndOutTime(empCode, todayDate);
                        if (permanentContractAttendancesOld.isEmpty()) {
                            permanentContractAttendanceDayBeforeYest.setOutTime(todayDate);
                            permanentContractAttendanceRepo.save(permanentContractAttendanceDayBeforeYest);
                        }
                    }
                }
            }
        }
        if (permanentContractAttendanceListDayBeforeYesterday.isEmpty() && checkForOnlyOut) {
//            System.out.println("Entered 4");
            List<PermanentContractAttendance> permanentContractAttendancesOld = permanentContractAttendanceRepo.findByEmployeeCodeAndOutTime(empCode, todayDate);
            if (false) {
                PermanentContractAttendance permanentContractAttendance = new PermanentContractAttendance();
                permanentContractAttendance.setOutTime(todayDate);
                permanentContractAttendance.setRecordedTime(todayDate);
                permanentContractAttendance.setEmployeeCode(empCode);
                permanentContractAttendance.setMarkedOn(todayDate);
                permanentContractAttendance.setAttendanceStatus(AttendanceStatus.PRESENT);
                permanentContractAttendance.setEmpId(id);
                permanentContractAttendance.setExitGateNumber("01");
                permanentContractAttendanceRepo.save(permanentContractAttendance);
            }

        }
        return permanentContractAttendances;

    }

    public Date yesterday(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, -1);
        return cal.getTime();
    }



}
