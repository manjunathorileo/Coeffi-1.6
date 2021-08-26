package com.dfq.coeffi.preventiveMaintenance;

import com.dfq.coeffi.SOPDetails.SOPCategory.SopCategoryService;
import com.dfq.coeffi.SOPDetails.SOPType.SopTypeService;
import com.dfq.coeffi.entity.hr.employee.Employee;
import com.dfq.coeffi.entity.user.Role;
import com.dfq.coeffi.entity.user.User;
import com.dfq.coeffi.preventiveMaintenance.admin.entity.PreventiveMaintenanceMaster;
import com.dfq.coeffi.preventiveMaintenance.admin.entity.SopStepsMaster;
import com.dfq.coeffi.preventiveMaintenance.admin.service.PreventiveMaintenanceMasterService;
import com.dfq.coeffi.preventiveMaintenance.admin.service.SopStepsMasterService;
import com.dfq.coeffi.preventiveMaintenance.durationType.DurationType;
import com.dfq.coeffi.preventiveMaintenance.durationType.DurationTypeService;
import com.dfq.coeffi.preventiveMaintenance.user.entity.PreventiveMaintenance;
import com.dfq.coeffi.preventiveMaintenance.user.entity.SopStepsAssigned;
import com.dfq.coeffi.preventiveMaintenance.user.service.PreventiveMaintenanceService;
import com.dfq.coeffi.preventiveMaintenance.user.service.SopStepsAssignedService;
import com.dfq.coeffi.repository.hr.EmployeeRepository;
import com.dfq.coeffi.service.UserService;
import com.dfq.coeffi.service.hr.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Configuration
@EnableScheduling
@Service
public class PreventiveMaintenanceScheduler extends Thread {

    private final SopTypeService sopTypeService;
    private final SopCategoryService SOPCategoryService;
    private final DurationTypeService durationTypeService;
    private final SopStepsMasterService sopStepsMasterService;
    private final SopStepsAssignedService sopStepsAssignedService;
    private final PreventiveMaintenanceMasterService preventiveMaintenanceMasterService;
    private final PreventiveMaintenanceService preventiveMaintenanceService;
    private final EmployeeService employeeService;
    private final EmployeeRepository employeeRepository;
    private final UserService userService;

    long dayValue = 0;

    @Autowired
    public PreventiveMaintenanceScheduler(SopTypeService sopTypeService, SopCategoryService SOPCategoryService, DurationTypeService durationTypeService, SopStepsMasterService sopStepsMasterService, SopStepsAssignedService sopStepsAssignedService, PreventiveMaintenanceMasterService preventiveMaintenanceMasterService, PreventiveMaintenanceService preventiveMaintenanceService, EmployeeService employeeService, EmployeeRepository employeeRepository, UserService userService) {
        this.sopTypeService = sopTypeService;
        this.SOPCategoryService = SOPCategoryService;
        this.durationTypeService = durationTypeService;
        this.sopStepsMasterService = sopStepsMasterService;
        this.sopStepsAssignedService = sopStepsAssignedService;
        this.preventiveMaintenanceMasterService = preventiveMaintenanceMasterService;
        this.preventiveMaintenanceService = preventiveMaintenanceService;
        this.employeeService = employeeService;
        this.employeeRepository = employeeRepository;
        this.userService = userService;
    }

    @Scheduled(cron = "0 0 0 * * *")
    //@Scheduled(cron = "0/1 * * * * *")
    public void runTimeCalculation() {
        Date today = new Date();
        long year = 0;
        long fromMonth = 0;
        long toMonth = 0;
        String yearFormat = "yyyy";
        String monthFormat = "MM";
        String dayFormat = "dd";
        SimpleDateFormat currentYearFormat = new SimpleDateFormat(yearFormat);
        SimpleDateFormat currentMonthFormat = new SimpleDateFormat(monthFormat);
        SimpleDateFormat currentDayFormat = new SimpleDateFormat(dayFormat);
        long currentYear = Long.parseLong(currentYearFormat.format(today));
        long currentMonth = Long.parseLong(currentMonthFormat.format(today));
        long currentDay = Long.parseLong(currentDayFormat.format(today));

        DurationType durationType = new DurationType();
        List<DurationType> durationTypeList = durationTypeService.getAllDurationType();
        List<PreventiveMaintenanceMaster> preventiveMaintenanceMasters = preventiveMaintenanceMasterService.getAllPreventiveMaintenanceMaster();
        for (PreventiveMaintenanceMaster preventiveMaintenanceMasterObj : preventiveMaintenanceMasters) {
            PreventiveMaintenance preventiveMaintenance = new PreventiveMaintenance();
            List<PreventiveMaintenance> preventiveMaintenanceList = preventiveMaintenanceService.getPreventiveMaintenanceByAssemblyLineByStagesByDurationTypeByDurationValue(preventiveMaintenanceMasterObj.getSopType().getId(), preventiveMaintenanceMasterObj.getSopCategory().getId(), preventiveMaintenanceMasterObj.getDurationType().getId(), preventiveMaintenanceMasterObj.getDurationValue());
            for (PreventiveMaintenance preventiveMaintenanceObj : preventiveMaintenanceList) {
                preventiveMaintenance = preventiveMaintenanceObj;
            }
            if (preventiveMaintenanceList.isEmpty()) {
                bindAllCheckList(preventiveMaintenanceMasterObj);
            } else {
                if (preventiveMaintenanceMasterObj.getDurationType().getDurationType().equals("Day")) {
                    long assignedOn = Long.parseLong(currentDayFormat.format(preventiveMaintenance.getAssignedOn()));
                    if ((currentDay - assignedOn) == preventiveMaintenanceMasterObj.getDurationValue()) {
                        if (preventiveMaintenance.getSubmitedBy() == null) {
                            submitAllCheckList(preventiveMaintenance);
                        }
                        bindAllCheckList(preventiveMaintenanceMasterObj);
                    }
                }
                if (preventiveMaintenanceMasterObj.getDurationType().getDurationType().equals("Week")) {
                    int currentWeek = weekOfTheMonth(today);
                    int assignedWeek = weekOfTheMonth(preventiveMaintenance.getAssignedOn());
                    if ((currentWeek - assignedWeek) == preventiveMaintenanceMasterObj.getDurationValue()) {
                        if (preventiveMaintenance.getSubmitedBy() == null) {
                            submitAllCheckList(preventiveMaintenance);
                        }
                        bindAllCheckList(preventiveMaintenanceMasterObj);
                    }
                }
                if (preventiveMaintenanceMasterObj.getDurationType().getDurationType().equals("Month")) {
                    long assignedMonth = Long.parseLong(currentMonthFormat.format(preventiveMaintenance.getAssignedOn()));
                    if ((currentMonth - assignedMonth) == preventiveMaintenanceMasterObj.getDurationValue()) {
                        if (preventiveMaintenance.getSubmitedBy() == null) {
                            submitAllCheckList(preventiveMaintenance);
                        }
                        bindAllCheckList(preventiveMaintenanceMasterObj);
                    }
                }
                if (preventiveMaintenanceMasterObj.getDurationType().getDurationType().equals("Year")) {
                    long assignedYear = Long.parseLong(currentYearFormat.format(preventiveMaintenance.getAssignedOn()));
                    if ((currentYear - assignedYear) == preventiveMaintenanceMasterObj.getDurationValue()) {
                        if (preventiveMaintenance.getSubmitedBy() == null) {
                            submitAllCheckList(preventiveMaintenance);
                        }
                        bindAllCheckList(preventiveMaintenanceMasterObj);
                    }
                }
            }
        }
    }

    public void bindAllCheckList(PreventiveMaintenanceMaster preventiveMaintenanceMasterObj) {
        Date today = new Date();
        String yearFormat = "yyyy";
        String monthFormat = "MM";
        SimpleDateFormat currentYearFormat = new SimpleDateFormat(yearFormat);
        SimpleDateFormat currentMonthFormat = new SimpleDateFormat(monthFormat);
        long currentYear = Long.parseLong(currentYearFormat.format(today));
        long currentMonth = Long.parseLong(currentMonthFormat.format(today));

        //List<PreventiveMaintenanceMaster> preventiveMaintenanceMasters = preventiveMaintenanceMasterService.getAllPreventiveMaintenanceMaster();
        //for (PreventiveMaintenanceMaster preventiveMaintenanceMasterObj : preventiveMaintenanceMasters) {
            List<SopStepsMaster> sopStepsMasters = preventiveMaintenanceMasterObj.getSopStepsMasters();
            List<SopStepsAssigned> sopStepsAssigneds = new ArrayList<>();
            PreventiveMaintenance preventiveMaintenance = new PreventiveMaintenance();
            preventiveMaintenance.setSopType(preventiveMaintenanceMasterObj.getSopType());
            preventiveMaintenance.setSopCategory(preventiveMaintenanceMasterObj.getSopCategory());
            preventiveMaintenance.setDurationType(preventiveMaintenanceMasterObj.getDurationType());
            preventiveMaintenance.setDurationValue(preventiveMaintenanceMasterObj.getDurationValue());
            //preventiveMaintenance.setDepartment(preventiveMaintenanceMasterObj.getDepartment());
            preventiveMaintenance.setEmployee(preventiveMaintenanceMasterObj.getEmployee());
            preventiveMaintenance.setSubmitedYear(currentYear);
            for (SopStepsMaster sopStepsMasterObj : sopStepsMasters) {
                SopStepsAssigned sopStepsAssigned = new SopStepsAssigned();
                sopStepsAssigned.setCheckPart(sopStepsMasterObj.getCheckPart());
                sopStepsAssigned.setCheckPoint(sopStepsMasterObj.getCheckPoint());
                sopStepsAssigned.setDescription(sopStepsMasterObj.getDescription());
                sopStepsAssigned.setStandardValue(sopStepsMasterObj.getStandardValue());
                sopStepsAssigned.setStatus(true);
                SopStepsAssigned sopStepsAssignedSave = sopStepsAssignedService.createCheckListAssigned(sopStepsAssigned);
                sopStepsAssigneds.add(sopStepsAssignedSave);
            }
            preventiveMaintenance.setSopStepsAssigned(sopStepsAssigneds);
            PreventiveMaintenance preventiveMaintenanceObj = preventiveMaintenanceService.createPreventiveMaintenance(preventiveMaintenance);
        //}
    }

    public void submitAllCheckList(PreventiveMaintenance preventiveMaintenance) {
        Date today = new Date();
        String yearFormat = "yyyy";
        String monthFormat = "MM";
        SimpleDateFormat currentYearFormat = new SimpleDateFormat(yearFormat);
        SimpleDateFormat currentMonthFormat = new SimpleDateFormat(monthFormat);
        long currentYear = Long.parseLong(currentYearFormat.format(today));
        long currentMonth = Long.parseLong(currentMonthFormat.format(today));
        long totalSubmitedCheckPoint = 0;
        preventiveMaintenance.setAllCheckListCompleted(true);
        preventiveMaintenance.setSubmitedOn(today);
        preventiveMaintenance.setIsAutoSubmit(true);
        preventiveMaintenance.setSubmitedBy("Auto_Submited");
        List<SopStepsAssigned> sopStepsAssigneds = preventiveMaintenance.getSopStepsAssigned();
        System.out.println("********************sopStepsAssigneds**********"+sopStepsAssigneds.size());
        for (SopStepsAssigned sopStepsAssignedObj : sopStepsAssigneds) {
            if (sopStepsAssignedObj.getCheckPointStatus() == null) {
                totalSubmitedCheckPoint++;
            }
        }
        PreventiveMaintenance preventiveMaintenanceUpdate = preventiveMaintenanceService.createPreventiveMaintenance(preventiveMaintenance);
    }

    public static int weekOfTheMonth(Date date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int weekNumber = cal.get(Calendar.WEEK_OF_MONTH);
        return weekNumber;
    }
}