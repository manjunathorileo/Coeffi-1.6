package com.dfq.coeffi.superadmin.Entity;

import com.dfq.coeffi.entity.hr.Department;
import com.dfq.coeffi.entity.hr.employee.EmployeeType;
import com.dfq.coeffi.i18n.Language;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
public class CompanyConfigure {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String companyName;

    private long logoId;

    private String companyDesc;

    private String email;

    private String password;

    private boolean leaveSunday;

    private boolean leaveSaturday;

    private String typeOfAttendence;

    private String currency;

    private long numberOfManagers;

    private String gatePass;

    private long workingDays;

    private long numberOfShifts;

    private String dateFormat;

    private String leaveConfig;

    private String mdApproval;

    private String salaryApprisal;

    private String salaryGeneration;

    private String fontStyle;

    @OneToMany
    private List<Language> languages;

    @OneToOne
    private Language defaultLanguage;

    private String employeeType;

    private boolean contract;

    private boolean permanentContract;

    private boolean stdCertificate;

    private boolean syncWithAllModules;

    @Lob
    private byte[] data;

    private boolean bayExists;

    private boolean durationWise;

    private boolean bodyTemp;

    private boolean maskDetection;

    private boolean emailApproval;

    private boolean denialList;//Visitor

    private boolean vivoDenial;//VIVO

    private String passTracking;

    private boolean visitorPayment;

    private String typeOfOperation;

    private String typeOfInPlantLogistics;

    private boolean grossAndTare;

    private boolean descriptionsOfItems;//VIVO

    private String captionOfTheModule;//VIVO

    private boolean levelByLevelOrRandom;//e-learning

    /*
    SAM modules
     */
    private boolean employeeModule;

    private boolean departmentModule;

    private boolean communicationModule;

    private boolean permanentContractModule;

    private boolean holidayModule;

    private boolean reportsModule;

    private boolean shiftMoudule;

    private boolean leaveManagementModule;

    private boolean emailSettingModule;

    private boolean groupModule;

    private boolean elearningModule;

    private boolean gatePassModule;

    private boolean companyPolicyModule;

    private boolean performanceAndExitModule;

    private boolean digitalSopModule;

    private boolean attendanceModule;

    private boolean vehicleIOModule;

    private boolean visitorIOModule;

    private boolean payrollModule;

    private boolean expenseModule;

    private boolean userConfigModule;

    private boolean gateConfigModule;

    private boolean elogAndLossAnalyticsModule;

    private boolean adminConfigModule;

    private boolean compOffModule;

    private boolean storeModule;

    private boolean mobileAppModule;

    private boolean foodManagementModule;

    private boolean advancedFoodManagementModule;
    private boolean dailyMenuSettingsModule;
    private boolean currencyAndRechargeModule;
    private boolean feedbackManagementModule;

    private boolean dashboardTypeVivo;

    private boolean visitorDashboard;
    private boolean employeeDashboard;

    //--Photo

    private boolean permanentContractPic;

    private boolean visitorPic;

    private boolean vivoPic;

    //Arun permanent_contract
    private boolean perContBodyTemp;

    private boolean contBodyTemp;

    private boolean perBodyTemp;

    private boolean perContMask;

    private boolean contMask;

    private boolean perMask;

    private String perContPassTracking;

    private String contPassTracking;

    //Arun quarantine
    private double threshouldTemp;

    private double threshouldDayTempChk;

    private double qurantinePeriod;

    //Arun email config
    private boolean dailyReport;

    @OneToOne
    private Department dailyReportDepartment;

    private String dailyReportEmail;

    private String dailyReportEmployeeName;

    private boolean dailyReportVisitor;

    private boolean dailyReportVehicle;

    private boolean dailyReportEmployee;

    private boolean dailyReportPermanentContract;

    private boolean dailyReportContract;

    //Manju emial config
    private boolean lateExitReport;

    @OneToOne
    private Department lateExitReportDepartment;

    private String lateExitReportEmail;

    private String lateExitReportEmployeeName;

    private boolean lateExitReportVisitor;

    private boolean lateExitReportVehicle;

    private boolean lateExitReportEmployee;

    private boolean lateExitReportPermanentContract;

    private boolean lateExitReportContract;

    private String lateExitTime;


    private long trialDays;

    private String customerName;

    private long numberOfPersons;

    private Date trialDate;

    private String commercialEmail;

    private boolean vivoPayment;


    //-----RFID----
    private boolean vivoRfid;

    private boolean visitorRfid;

    //---Driver io---
    private boolean vivoCheckIO;

    //---Form 1or2---
    private boolean formConfig;

    //----CTC-----------------
    private boolean basicSalary;
    private boolean variableDearnessAllowance;
    private boolean conveyanceAllowance;
    private boolean houseRentAllowance;
    private boolean educationalAllowance;
    private boolean mealsAllowance;
    private boolean washingAllowance;
    private boolean otherAllowance;
    private boolean miscellaneousAllowance;
    private boolean mobileAllowance;
    private boolean rla;
    private boolean tpt;
    private boolean uniformAllowance;
    private boolean shoeAllowance;
    private boolean epfContribution;
    private boolean bonus;
    private boolean gratuity;
    private boolean medicalPolicy;
    private boolean medicalReimbursement;
    private boolean leaveTravelAllowance;
    private boolean royalty;
    private boolean employeeEsicContribution;
    private boolean employerContributionESIC;
    private boolean employeeContributionESIC;
    private boolean additional1;
    private boolean additional2;
    private boolean additional3;
    private boolean additional4;
    private boolean additional5;
    //----CTC----------------------
    private long noOfLocation;
    private long noOfEmployee;
    //---------report----------
    private boolean attendanceReport;

    private boolean absentReport;

    private boolean lateEntryReport;

    private boolean earlyCheckOutReport;

    private boolean overTimeReport;

    private boolean newJoiningsReport;

    private boolean exEmployeesReport;

    private boolean monthlyLeaveRegister;

    private boolean monthlyEpfoReport12A;

    private boolean monthlyEsicReport;

    private boolean professionalTaxReport;

    private boolean registerOfAdultWorkers;

    private boolean monthlySalaryRegister;

    private boolean form22Report;

    private boolean inOutReport;

    private boolean totalVehicles;

    private boolean vehicleTypeWise;

    private boolean exitWithInTime;

    private boolean extraTimeExit;

    private boolean bayWise;

    private boolean passExpire;

    private boolean departmentWise;

    private boolean visitTypeWise;

    private boolean visitorExtraTimeExit;

    private boolean extraTimePayment;

    private boolean totalVisit;

    private boolean deniedCompany;

    private boolean deniedVisitor;

    private boolean adultsWorkers;
    //---------report----------
    private boolean paramountBuild;
    private boolean geBuild;

    private boolean otForPermanentStaff;
    private boolean otForPermanentWorker;
    private boolean otForTemporaryContract;
    private boolean otForPermanentContract;
    //----------OT-settings-----------
    private long otGraceTime;
    private long otPercenatge;
    private double otTimes;
    private double otFactor;
    private long otThresholdInMins;
    private long lateEntryThresholdInMins;
    private long earlyOutThresholdInMins;
    //---------ATTENDANCE settings-------
    private boolean attendanceBonus;
    private double bonusAmount;
    private boolean lateEntryLoss;
    private boolean earlyOutLoss;
    private boolean salaryRegenerate;

    //----SOP------
    private boolean digitalSopMaintenance;
    //----SOP------
    //-----Elearning----
    private String productNameHeader;
    private long fileLimit;
    private long testCount;

    private boolean releaveWithExitProcess;
    private boolean attendanceWithHeadCount;

    //-------Gate assignment---
//    private EmployeeType gateAssignEmployeeType;
//    @ManyToMany
//    private List<Long> inGateIds;
//    @ManyToMany
//    private List<Long> outGateIds;
}
