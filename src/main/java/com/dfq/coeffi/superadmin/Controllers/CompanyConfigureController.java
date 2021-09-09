package com.dfq.coeffi.superadmin.Controllers;

import com.dfq.coeffi.controller.BaseController;
import com.dfq.coeffi.entity.hr.Department;
import com.dfq.coeffi.entity.hr.employee.Employee;
import com.dfq.coeffi.entity.hr.employee.EmployeeType;
import com.dfq.coeffi.i18n.LanguageDto;
import com.dfq.coeffi.i18n.LanguageRepository;
import com.dfq.coeffi.service.hr.DepartmentService;
import com.dfq.coeffi.service.hr.EmployeeService;
import com.dfq.coeffi.superadmin.Entity.CompanyConfigure;
import com.dfq.coeffi.superadmin.Entity.DailyReportDto;
import com.dfq.coeffi.superadmin.Entity.ReportDto;
import com.dfq.coeffi.superadmin.Services.CompanyConfigureService;
import com.dfq.coeffi.util.DateUtil;
import org.hibernate.mapping.Map;
import org.omg.CosNaming.NamingContextPackage.NotFound;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import javax.validation.constraints.Null;
import java.io.NotActiveException;
import java.util.*;

@RestController
public class  CompanyConfigureController extends BaseController {
    @Autowired
    CompanyConfigureService companyConfigureService;
    @Autowired
    EmployeeService employeeService;
    @Autowired
    DepartmentService departmentService;
    @Autowired
    LanguageRepository languageRepository;

    @PostMapping("super-admin-save")
    public ResponseEntity<CompanyConfigure> saveCompany(@RequestBody CompanyConfigure companyConfigure) {
        CompanyConfigure configure = companyConfigureService.saveCompany(companyConfigure);
        return new ResponseEntity<>(configure, HttpStatus.CREATED);
    }

    @GetMapping("super-admin-view")
    public ResponseEntity<List<CompanyConfigure>> getCompany() {
        List<CompanyConfigure> configureList = companyConfigureService.getCompany();
        return new ResponseEntity<>(configureList, HttpStatus.OK);

    }

    @DeleteMapping("super-admin-delete/{id}")
    public void deleteCompany(@PathVariable long id) {
        companyConfigureService.deleteCompany(id);
    }

    @GetMapping("configure-sunday-leave/{id}/{sunday}")
    public ResponseEntity<CompanyConfigure> setSundayLeave(@PathVariable("id") long id, @PathVariable("sunday") boolean s) {
        CompanyConfigure configure = companyConfigureService.getCompanyById(id);
        configure.setLeaveSunday(s);
        companyConfigureService.saveCompany(configure);

        return new ResponseEntity<>(configure, HttpStatus.OK);
    }

    @GetMapping("configure-saturday-leave/{id}/{saturday}")
    public ResponseEntity<CompanyConfigure> setSaturdayLeave(@PathVariable("id") long id, @PathVariable("saturday") boolean saturday) {
        CompanyConfigure configure = companyConfigureService.getCompanyById(id);
        configure.setLeaveSaturday(saturday);
        companyConfigureService.saveCompany(configure);

        return new ResponseEntity<>(configure, HttpStatus.OK);
    }

    @GetMapping("configure-reporting-managers/{id}/{manager}")
    public ResponseEntity<CompanyConfigure> setReportingManager(@PathVariable("id") long id, @PathVariable("manager") long mgrId) {
        CompanyConfigure configure = companyConfigureService.getCompanyById(id);
        configure.setNumberOfManagers(mgrId);
        companyConfigureService.saveCompany(configure);

        return new ResponseEntity<>(configure, HttpStatus.OK);
    }

    @GetMapping("configure-attendence-punch/{id}/{attendence}")
    public ResponseEntity<CompanyConfigure> setAttendenceType(@PathVariable("id") long id, @PathVariable("attendence") String attendence) {
        CompanyConfigure configure = companyConfigureService.getCompanyById(id);
        configure.setTypeOfAttendence(attendence);
        companyConfigureService.saveCompany(configure);

        return new ResponseEntity<>(configure, HttpStatus.OK);
    }

    @GetMapping("configure-company-name/{id}/{cName}")
    public ResponseEntity<CompanyConfigure> setCompanyName(@PathVariable("id") long id, @PathVariable("cName") String cName) {
        CompanyConfigure configure = companyConfigureService.getCompanyById(id);
        configure.setCompanyName(cName);
        companyConfigureService.saveCompany(configure);

        return new ResponseEntity<>(configure, HttpStatus.OK);
    }

    @GetMapping("configure-currency/{id}/{currency}")
    public ResponseEntity<CompanyConfigure> setCurrency(@PathVariable("id") long id, @PathVariable("currency") String currency) {
        CompanyConfigure configure = companyConfigureService.getCompanyById(id);
        configure.setCurrency(currency);
        companyConfigureService.saveCompany(configure);

        return new ResponseEntity<>(configure, HttpStatus.OK);
    }

    @GetMapping("configure-gatepass/{id}/{gatePass}")
    public ResponseEntity<CompanyConfigure> setGatePass(@PathVariable("id") long id, @PathVariable("gatepass") String gatePass) {
        CompanyConfigure configure = companyConfigureService.getCompanyById(id);
        configure.setGatePass(gatePass);
        companyConfigureService.saveCompany(configure);

        return new ResponseEntity<>(configure, HttpStatus.OK);

    }

    @GetMapping("configure-working-day/{id}/{workingDays}")
    public ResponseEntity<CompanyConfigure> setWorkingDays(@PathVariable("id") long id, @PathVariable("workingDays") long workingDays) {
        CompanyConfigure configure = companyConfigureService.getCompanyById(id);
        configure.setWorkingDays(workingDays);
        companyConfigureService.saveCompany(configure);

        return new ResponseEntity<>(configure, HttpStatus.OK);
    }

    @GetMapping("configure-number-of-shifts/{id}/{numberOfDays}")
    public ResponseEntity<CompanyConfigure> setNumberOfShifts(@PathVariable("id") long id, @PathVariable("numberOfDays") long numberOfDays) {
        CompanyConfigure configure = companyConfigureService.getCompanyById(id);
        configure.setNumberOfShifts(numberOfDays);
        companyConfigureService.saveCompany(configure);

        return new ResponseEntity<>(configure, HttpStatus.OK);
    }

    @GetMapping("configure-date-format/{id}/{dateFormat}")
    public ResponseEntity<CompanyConfigure> setDateFormat(@PathVariable("id") long id, @PathVariable("dateFormat") String dateFormat) {
        CompanyConfigure configure = companyConfigureService.getCompanyById(id);
        configure.setDateFormat(dateFormat);
        companyConfigureService.saveCompany(configure);

        return new ResponseEntity<>(configure, HttpStatus.OK);
    }

    @GetMapping("configure-leaves/{id}/{leaveConfig}")
    public ResponseEntity<CompanyConfigure> setConfigLeaves(@PathVariable("id") long id, @PathVariable("leaveConfig") String leaveConfig) {
        CompanyConfigure configure = companyConfigureService.getCompanyById(id);
        configure.setLeaveConfig(leaveConfig);
        companyConfigureService.saveCompany(configure);

        return new ResponseEntity<>(configure, HttpStatus.OK);
    }

    @GetMapping("configure-mdapproval/{id}/{md}")
    public ResponseEntity<CompanyConfigure> setMdApproval(@PathVariable("id") long id, @PathVariable("md") String md) {
        CompanyConfigure configure = companyConfigureService.getCompanyById(id);
        configure.setMdApproval(md);
        companyConfigureService.saveCompany(configure);

        return new ResponseEntity<>(configure, HttpStatus.OK);
    }

    @GetMapping("configure-salary-apprisal/{id}/{salaryApprisal}")
    public ResponseEntity<CompanyConfigure> setSalaryApprisal(@PathVariable("id") long id, @PathVariable("salaryApprisal") String salaryApprisal) {
        CompanyConfigure configure = companyConfigureService.getCompanyById(id);
        configure.setSalaryApprisal(salaryApprisal);
        companyConfigureService.saveCompany(configure);

        return new ResponseEntity<>(configure, HttpStatus.OK);
    }

    @GetMapping("configure-salary-approval/{id}/{salaryApproval}")
    public ResponseEntity<CompanyConfigure> setSalaryApproval(@PathVariable("id") long id, @PathVariable("salaryApproval") String salaryApproval) {
        CompanyConfigure configure = companyConfigureService.getCompanyById(id);
        configure.setSalaryGeneration(salaryApproval);
        companyConfigureService.saveCompany(configure);

        return new ResponseEntity<>(configure, HttpStatus.OK);
    }

    @PostMapping("configure-language")
    public ResponseEntity<CompanyConfigure> setLanguagesBySuperAdmin(@RequestBody LanguageDto languageDto) {
        CompanyConfigure configure = companyConfigureService.getCompanyById(languageDto.getId());
        configure.setLanguages(languageDto.getLanguages());
        configure.setDefaultLanguage(languageDto.getDefaultLanguage());
        companyConfigureService.saveCompany(configure);
        return new ResponseEntity<>(configure, HttpStatus.OK);
    }

    @GetMapping("configure-language")
    public ResponseEntity<LanguageDto> getLanguagesSetBySuperAdmin() {
        List<CompanyConfigure> companyConfigure = companyConfigureService.getCompany();
        Collections.reverse(companyConfigure);
        CompanyConfigure configure = null;
        if (!companyConfigure.isEmpty()) {
            configure = companyConfigure.get(0);
        }
        LanguageDto languageDto = new LanguageDto();
        languageDto.setDefaultLanguage(configure.getDefaultLanguage());
        languageDto.setLanguages(configure.getLanguages());
        return new ResponseEntity<>(languageDto, HttpStatus.OK);
    }


    @GetMapping("company-employee-type/{id}/{et}")
    public ResponseEntity<CompanyConfigure> setEmployeeType(@PathVariable("id") long id, @PathVariable("et") String et) {
        CompanyConfigure configure = companyConfigureService.getCompanyById(id);
        configure.setEmployeeType(et);
        companyConfigureService.saveCompany(configure);
        return new ResponseEntity<>(configure, HttpStatus.OK);
    }

    @GetMapping("company-config/employee-type")
    public ResponseEntity<HashMap<String, String>> getEmployeeType() {
        List<CompanyConfigure> configure = companyConfigureService.getCompany();
        Collections.reverse(configure);
        CompanyConfigure companyConfigure = configure.get(0);
        HashMap<String, String> map = new HashMap<>();
        map.put("PERMANENT_STAFF", "PERMANENT_STAFF");
        map.put("PERMAMENT_WORKER", "PERMAMENT_WORKER");
        map.put("PERMANENT", "PERMAMENT");
        map.put("PERMANENT_CONTRACT", companyConfigure.getEmployeeType());
        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    @GetMapping("company-employee-types/{id}/{status}")
    public ResponseEntity<CompanyConfigure> sync(@PathVariable("id") long id, @PathVariable("status") boolean status) {
        CompanyConfigure configure = companyConfigureService.getCompanyById(id);
        configure.setSyncWithAllModules(status);
        companyConfigureService.saveCompany(configure);
        return new ResponseEntity<>(configure, HttpStatus.OK);
    }

    @GetMapping("company-employee-type/{value}")
    public ResponseEntity<Boolean> checkEmplooyeeType(@PathVariable("value") String value) {
        CompanyConfigure configure = companyConfigureService.getCompanyById(1);
        boolean b = false;
        if (value.equalsIgnoreCase("CONTRACT")) {
            b = configure.isContract();
        } else if (value.equalsIgnoreCase("PERMANENT_CONTRACT")) {
            b = configure.isPermanentContract();
        }
        return new ResponseEntity<>(b, HttpStatus.OK);
    }

    @PostMapping("contract-employee-type/{id}/{status}")
    public ResponseEntity<CompanyConfigure> setContract(@PathVariable("id") long id, @PathVariable("status") boolean status) {
        CompanyConfigure configure = companyConfigureService.getCompanyById(id);
        configure.setContract(status);
        companyConfigureService.saveCompany(configure);
        return new ResponseEntity<>(configure, HttpStatus.OK);
    }

    @PostMapping("permanent-contract-employee-type/{id}/{status}")
    public ResponseEntity<CompanyConfigure> setPerContract(@PathVariable("id") long id, @PathVariable("status") boolean status) {
        CompanyConfigure configure = companyConfigureService.getCompanyById(id);
        configure.setPermanentContract(status);
        companyConfigureService.saveCompany(configure);
        return new ResponseEntity<>(configure, HttpStatus.OK);
    }


    //28-05-2020-manju
    @PostMapping("elearning-certificate-type/{id}/{status}")
    public ResponseEntity<CompanyConfigure> setStdOrCustom(@PathVariable("id") long id, @PathVariable("status") boolean status) {
        CompanyConfigure configure = companyConfigureService.getCompanyById(id);
        configure.setStdCertificate(status);
        companyConfigureService.saveCompany(configure);
        return new ResponseEntity<>(configure, HttpStatus.OK);
    }

    @PostMapping("set-body-temperature/{id}/{bTemp}")
    public ResponseEntity<CompanyConfigure> setBodyTemp(@PathVariable("id") long id, @PathVariable("bTemp") boolean bTemp) {
        CompanyConfigure configure = companyConfigureService.getCompanyById(id);
        configure.setBodyTemp(bTemp);
        companyConfigureService.saveCompany(configure);
        return new ResponseEntity<>(configure, HttpStatus.OK);
    }

    @PostMapping("set-mask-detection/{id}/{mDete}")
    public ResponseEntity<CompanyConfigure> setMaskDete(@PathVariable("id") long id, @PathVariable("mDete") boolean mDete) {
        CompanyConfigure configure = companyConfigureService.getCompanyById(id);
        configure.setMaskDetection(mDete);
        companyConfigureService.saveCompany(configure);
        return new ResponseEntity<>(configure, HttpStatus.OK);
    }

    @PostMapping("set-email-approval/{id}/{email}")
    public ResponseEntity<CompanyConfigure> setEmailAttachment(@PathVariable("id") long id, @PathVariable("email") boolean email) {
        CompanyConfigure configure = companyConfigureService.getCompanyById(id);
        configure.setEmailApproval(email);
        companyConfigureService.saveCompany(configure);
        return new ResponseEntity<>(configure, HttpStatus.OK);
    }

    @PostMapping("set-denial-list/{id}/{dlist}")
    public ResponseEntity<CompanyConfigure> setDenialCompany(@PathVariable("id") long id, @PathVariable("dlist") boolean dlist) {
        CompanyConfigure configure = companyConfigureService.getCompanyById(id);
        configure.setDenialList(dlist);
        companyConfigureService.saveCompany(configure);
        return new ResponseEntity<>(configure, HttpStatus.OK);
    }

    @PostMapping("set-pass-tracking/{id}/{pass}")
    public ResponseEntity<CompanyConfigure> setPass(@PathVariable("id") long id, @PathVariable("pass") String pass) {
        CompanyConfigure configure = companyConfigureService.getCompanyById(id);
        configure.setPassTracking(pass);
        companyConfigureService.saveCompany(configure);
        return new ResponseEntity<>(configure, HttpStatus.OK);
    }


    @PostMapping("set-visitor-payment/{id}/{vPayment}")
    public ResponseEntity<CompanyConfigure> visitorPayment(@PathVariable("id") long id, @PathVariable("vPayment") boolean vPayment) {
        CompanyConfigure configure = companyConfigureService.getCompanyById(id);
        configure.setVisitorPayment(vPayment);
        companyConfigureService.saveCompany(configure);
        return new ResponseEntity<>(configure, HttpStatus.OK);
    }

    /**
     * VIVO-INPLANT
     *
     * @param id
     * @param typeOfOperation
     * @return
     */
    @PostMapping("set-type-of-operation/{id}/{typeOfOperation}")
    public ResponseEntity<CompanyConfigure> setTypeOfOperation(@PathVariable("id") long id, @PathVariable("typeOfOperation") String typeOfOperation) {
        CompanyConfigure configure = companyConfigureService.getCompanyById(id);
        configure.setTypeOfOperation(typeOfOperation);
        companyConfigureService.saveCompany(configure);
        return new ResponseEntity<>(configure, HttpStatus.OK);
    }

    @PostMapping("compay-config/vivo-denial-list/{id}/{dlist}")
    public ResponseEntity<CompanyConfigure> setVIVOEnableAndVerify(@PathVariable("id") long id, @PathVariable("dlist") boolean dlist) {
        CompanyConfigure configure = companyConfigureService.getCompanyById(id);
        configure.setVivoDenial(dlist);
        companyConfigureService.saveCompany(configure);
        return new ResponseEntity<>(configure, HttpStatus.OK);
    }

    @PostMapping("compay-config/type-Of-In-PlantLogistics/{id}/{typeOfInPlantLogistics}")
    public ResponseEntity<CompanyConfigure> setTypeOfInPlantLogistics(@PathVariable("id") long id, @PathVariable("typeOfInPlantLogistics") String typeOfInPlantLogistics) {
        CompanyConfigure configure = companyConfigureService.getCompanyById(id);
        configure.setTypeOfInPlantLogistics(typeOfInPlantLogistics);
        companyConfigureService.saveCompany(configure);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("company-config/enable-gross-tare/{id}/{status}")
    public ResponseEntity<CompanyConfigure> enableGrossAndTareWeight(@PathVariable("id") long id, @PathVariable("status") boolean status) {
        CompanyConfigure configure = companyConfigureService.getCompanyById(id);
        configure.setGrossAndTare(status);
        companyConfigureService.saveCompany(configure);
        return new ResponseEntity<>(configure, HttpStatus.OK);
    }

    @PostMapping("company-config/enable-descriptions-of-items/{id}/{status}")
    public ResponseEntity<CompanyConfigure> enableDescriptionsOfItems(@PathVariable("id") long id, @PathVariable("status") boolean status) {
        CompanyConfigure configure = companyConfigureService.getCompanyById(id);
        configure.setDescriptionsOfItems(status);
        companyConfigureService.saveCompany(configure);
        return new ResponseEntity<>(configure, HttpStatus.OK);
    }

    @PostMapping("compay-config/caption-of-module/{id}/{captionOfTheModule}")
    public ResponseEntity<CompanyConfigure> setCaptionOfTheModule(@PathVariable("id") long id, @PathVariable("captionOfTheModule") String captionOfTheModule) {
        CompanyConfigure configure = companyConfigureService.getCompanyById(id);
        configure.setCaptionOfTheModule(captionOfTheModule);
        companyConfigureService.saveCompany(configure);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }


    /**
     * elearning level or random config
     *
     * @param id
     * @param status
     * @return
     */
    @PostMapping("company-config/elearning-level-random-test/{id}/{status}")
    public ResponseEntity<CompanyConfigure> takeTestLevelByLevelOrRandom(@PathVariable("id") long id, @PathVariable("status") boolean status) {
        CompanyConfigure configure = companyConfigureService.getCompanyById(id);
        configure.setLevelByLevelOrRandom(status);
        companyConfigureService.saveCompany(configure);
        return new ResponseEntity<>(configure, HttpStatus.OK);
    }

    /*
        SAM configuration
     */

    @PostMapping("company-config/sam-employeeModule/{id}/{status}")
    public ResponseEntity<CompanyConfigure> employeeModule(@PathVariable("id") long id, @PathVariable("status") boolean status) {
        CompanyConfigure configure = companyConfigureService.getCompanyById(id);
        configure.setEmployeeModule(status);
        companyConfigureService.saveCompany(configure);
        return new ResponseEntity<>(configure, HttpStatus.OK);
    }

    @PostMapping("company-config/sam-departmentModule/{id}/{status}")
    public ResponseEntity<CompanyConfigure> departmentModule(@PathVariable("id") long id, @PathVariable("status") boolean status) {
        CompanyConfigure configure = companyConfigureService.getCompanyById(id);
        configure.setDepartmentModule(status);
        companyConfigureService.saveCompany(configure);
        return new ResponseEntity<>(configure, HttpStatus.OK);
    }

    @PostMapping("company-config/sam-communicationModule/{id}/{status}")
    public ResponseEntity<CompanyConfigure> communicationModule(@PathVariable("id") long id, @PathVariable("status") boolean status) {
        CompanyConfigure configure = companyConfigureService.getCompanyById(id);
        configure.setCommunicationModule(status);
        companyConfigureService.saveCompany(configure);
        return new ResponseEntity<>(configure, HttpStatus.OK);
    }

    @PostMapping("company-config/sam-permanentContractModule/{id}/{status}")
    public ResponseEntity<CompanyConfigure> permanentContractModule(@PathVariable("id") long id, @PathVariable("status") boolean status) {
        CompanyConfigure configure = companyConfigureService.getCompanyById(id);
        configure.setPermanentContractModule(status);
        companyConfigureService.saveCompany(configure);
        return new ResponseEntity<>(configure, HttpStatus.OK);
    }

    @PostMapping("company-config/sam-holidayModule/{id}/{status}")
    public ResponseEntity<CompanyConfigure> holidayModule(@PathVariable("id") long id, @PathVariable("status") boolean status) {
        CompanyConfigure configure = companyConfigureService.getCompanyById(id);
        configure.setHolidayModule(status);
        companyConfigureService.saveCompany(configure);
        return new ResponseEntity<>(configure, HttpStatus.OK);
    }

    @PostMapping("company-config/sam-reportsModule/{id}/{status}")
    public ResponseEntity<CompanyConfigure> reportsModule(@PathVariable("id") long id, @PathVariable("status") boolean status) {
        CompanyConfigure configure = companyConfigureService.getCompanyById(id);
        configure.setReportsModule(status);
        companyConfigureService.saveCompany(configure);
        return new ResponseEntity<>(configure, HttpStatus.OK);
    }

    @PostMapping("company-config/sam-shiftMoudule/{id}/{status}")
    public ResponseEntity<CompanyConfigure> shiftMoudule(@PathVariable("id") long id, @PathVariable("status") boolean status) {
        CompanyConfigure configure = companyConfigureService.getCompanyById(id);
        configure.setShiftMoudule(status);
        companyConfigureService.saveCompany(configure);
        return new ResponseEntity<>(configure, HttpStatus.OK);
    }

    @PostMapping("company-config/sam-leaveManagementModule/{id}/{status}")
    public ResponseEntity<CompanyConfigure> leaveManagementModule(@PathVariable("id") long id, @PathVariable("status") boolean status) {
        CompanyConfigure configure = companyConfigureService.getCompanyById(id);
        configure.setLeaveManagementModule(status);
        companyConfigureService.saveCompany(configure);
        return new ResponseEntity<>(configure, HttpStatus.OK);
    }

    @PostMapping("company-config/sam-emailSettingModule/{id}/{status}")
    public ResponseEntity<CompanyConfigure> emailSettingModule(@PathVariable("id") long id, @PathVariable("status") boolean status) {
        CompanyConfigure configure = companyConfigureService.getCompanyById(id);
        configure.setEmailSettingModule(status);
        companyConfigureService.saveCompany(configure);
        return new ResponseEntity<>(configure, HttpStatus.OK);
    }

    @PostMapping("company-config/sam-groupModule/{id}/{status}")
    public ResponseEntity<CompanyConfigure> groupModule(@PathVariable("id") long id, @PathVariable("status") boolean status) {
        CompanyConfigure configure = companyConfigureService.getCompanyById(id);
        configure.setGroupModule(status);
        companyConfigureService.saveCompany(configure);
        return new ResponseEntity<>(configure, HttpStatus.OK);
    }

    @PostMapping("company-config/sam-elearningModule/{id}/{status}")
    public ResponseEntity<CompanyConfigure> elearningModule(@PathVariable("id") long id, @PathVariable("status") boolean status) {
        CompanyConfigure configure = companyConfigureService.getCompanyById(id);
        configure.setElearningModule(status);
        companyConfigureService.saveCompany(configure);
        return new ResponseEntity<>(configure, HttpStatus.OK);
    }

    @PostMapping("company-config/sam-gatePassModule/{id}/{status}")
    public ResponseEntity<CompanyConfigure> gatePassModule(@PathVariable("id") long id, @PathVariable("status") boolean status) {
        CompanyConfigure configure = companyConfigureService.getCompanyById(id);
        configure.setGatePassModule(status);
        companyConfigureService.saveCompany(configure);
        return new ResponseEntity<>(configure, HttpStatus.OK);
    }

    @PostMapping("company-config/sam-companyPolicyModule/{id}/{status}")
    public ResponseEntity<CompanyConfigure> companyPolicy(@PathVariable("id") long id, @PathVariable("status") boolean status) {
        CompanyConfigure configure = companyConfigureService.getCompanyById(id);
        configure.setCompanyPolicyModule(status);
        companyConfigureService.saveCompany(configure);
        return new ResponseEntity<>(configure, HttpStatus.OK);
    }

    @PostMapping("company-config/sam-performanceAndExitModule/{id}/{status}")
    public ResponseEntity<CompanyConfigure> performanceAndExitModule(@PathVariable("id") long id, @PathVariable("status") boolean status) {
        CompanyConfigure configure = companyConfigureService.getCompanyById(id);
        configure.setPerformanceAndExitModule(status);
        companyConfigureService.saveCompany(configure);
        return new ResponseEntity<>(configure, HttpStatus.OK);
    }

    @PostMapping("company-config/sam-digitalSopModule/{id}/{status}")
    public ResponseEntity<CompanyConfigure> digitalSopModule(@PathVariable("id") long id, @PathVariable("status") boolean status) {
        CompanyConfigure configure = companyConfigureService.getCompanyById(id);
        configure.setDigitalSopModule(status);
        companyConfigureService.saveCompany(configure);
        return new ResponseEntity<>(configure, HttpStatus.OK);
    }

    @PostMapping("company-config/sam-attendanceModule/{id}/{status}")
    public ResponseEntity<CompanyConfigure> attendanceModule(@PathVariable("id") long id, @PathVariable("status") boolean status) {
        CompanyConfigure configure = companyConfigureService.getCompanyById(id);
        configure.setAttendanceModule(status);
        companyConfigureService.saveCompany(configure);
        return new ResponseEntity<>(configure, HttpStatus.OK);
    }

    @PostMapping("company-config/sam-vehicleIOModule/{id}/{status}")
    public ResponseEntity<CompanyConfigure> vehicleIOModule(@PathVariable("id") long id, @PathVariable("status") boolean status) {
        CompanyConfigure configure = companyConfigureService.getCompanyById(id);
        configure.setVehicleIOModule(status);
        companyConfigureService.saveCompany(configure);
        return new ResponseEntity<>(configure, HttpStatus.OK);
    }

    @PostMapping("company-config/sam-visitorIOModule/{id}/{status}")
    public ResponseEntity<CompanyConfigure> visitorIOModule(@PathVariable("id") long id, @PathVariable("status") boolean status) {
        CompanyConfigure configure = companyConfigureService.getCompanyById(id);
        configure.setVisitorIOModule(status);
        companyConfigureService.saveCompany(configure);
        return new ResponseEntity<>(configure, HttpStatus.OK);
    }

    @PostMapping("company-config/sam-payrollModule/{id}/{status}")
    public ResponseEntity<CompanyConfigure> payrollModule(@PathVariable("id") long id, @PathVariable("status") boolean status) {
        CompanyConfigure configure = companyConfigureService.getCompanyById(id);
        configure.setPayrollModule(status);
        companyConfigureService.saveCompany(configure);
        return new ResponseEntity<>(configure, HttpStatus.OK);
    }

    @PostMapping("company-config/sam-expenseModule/{id}/{status}")
    public ResponseEntity<CompanyConfigure> expenseModule(@PathVariable("id") long id, @PathVariable("status") boolean status) {
        CompanyConfigure configure = companyConfigureService.getCompanyById(id);
        configure.setExpenseModule(status);
        companyConfigureService.saveCompany(configure);
        return new ResponseEntity<>(configure, HttpStatus.OK);
    }

    @PostMapping("company-config/sam-userConfigModule/{id}/{status}")
    public ResponseEntity<CompanyConfigure> userConfigModule(@PathVariable("id") long id, @PathVariable("status") boolean status) {
        CompanyConfigure configure = companyConfigureService.getCompanyById(id);
        configure.setUserConfigModule(status);
        companyConfigureService.saveCompany(configure);
        return new ResponseEntity<>(configure, HttpStatus.OK);
    }

    @PostMapping("company-config/sam-gateConfigModule/{id}/{status}")
    public ResponseEntity<CompanyConfigure> gateConfigModule(@PathVariable("id") long id, @PathVariable("status") boolean status) {
        CompanyConfigure configure = companyConfigureService.getCompanyById(id);
        configure.setGateConfigModule(status);
        companyConfigureService.saveCompany(configure);
        return new ResponseEntity<>(configure, HttpStatus.OK);
    }

    @PostMapping("company-config/sam-elogAndLossAnalyticsModule/{id}/{status}")
    public ResponseEntity<CompanyConfigure> elogAndLossAnalyticsModule(@PathVariable("id") long id, @PathVariable("status") boolean status) {
        CompanyConfigure configure = companyConfigureService.getCompanyById(id);
        configure.setElogAndLossAnalyticsModule(status);
        companyConfigureService.saveCompany(configure);
        return new ResponseEntity<>(configure, HttpStatus.OK);
    }

    @PostMapping("company-config/sam-adminConfigModule/{id}/{status}")
    public ResponseEntity<CompanyConfigure> adminConfigModule(@PathVariable("id") long id, @PathVariable("status") boolean status) {
        CompanyConfigure configure = companyConfigureService.getCompanyById(id);
        configure.setAdminConfigModule(status);
        companyConfigureService.saveCompany(configure);
        return new ResponseEntity<>(configure, HttpStatus.OK);
    }

    //------Photo

    @PostMapping("company-config/sam-permanentContractPic/{id}/{status}")
    public ResponseEntity<CompanyConfigure> permanentContractPic(@PathVariable("id") long id, @PathVariable("status") boolean status) {
        CompanyConfigure configure = companyConfigureService.getCompanyById(id);
        configure.setPermanentContractPic(status);
        companyConfigureService.saveCompany(configure);
        return new ResponseEntity<>(configure, HttpStatus.OK);
    }

    @PostMapping("company-config/sam-visitorPic/{id}/{status}")
    public ResponseEntity<CompanyConfigure> visitorPic(@PathVariable("id") long id, @PathVariable("status") boolean status) {
        CompanyConfigure configure = companyConfigureService.getCompanyById(id);
        configure.setVisitorPic(status);
        companyConfigureService.saveCompany(configure);
        return new ResponseEntity<>(configure, HttpStatus.OK);
    }


    @PostMapping("company-config/vivoPic/{id}/{status}")
    public ResponseEntity<CompanyConfigure> vivoPic(@PathVariable("id") long id, @PathVariable("status") boolean status) {
        CompanyConfigure configure = companyConfigureService.getCompanyById(id);
        configure.setVivoPic(status);
        companyConfigureService.saveCompany(configure);
        return new ResponseEntity<>(configure, HttpStatus.OK);
    }


    /*
    Arun permanent
     */
    @PostMapping("permanent-body-temperature/{id}/{bTemp}")
    public ResponseEntity<CompanyConfigure> setPerBodyTemp(@PathVariable("id") long id, @PathVariable("bTemp") boolean bTemp) {
        CompanyConfigure configure = companyConfigureService.getCompanyById(id);
        configure.setPerBodyTemp(bTemp);
        companyConfigureService.saveCompany(configure);

        return new ResponseEntity<>(configure, HttpStatus.OK);
    }

    @PostMapping("permanent-mask-detection/{id}/{mDete}")
    public ResponseEntity<CompanyConfigure> setPermanentMask(@PathVariable("id") long id, @PathVariable("mDete") boolean mDete) {
        CompanyConfigure configure = companyConfigureService.getCompanyById(id);
        configure.setPerMask(mDete);
        companyConfigureService.saveCompany(configure);

        return new ResponseEntity<>(configure, HttpStatus.OK);
    }

    /*
    Arun permanent contract
   */

    @PostMapping("permanent/contract-body-temperature/{id}/{bTemp}")
    public ResponseEntity<CompanyConfigure> setPerContBodyTemp(@PathVariable("id") long id, @PathVariable("bTemp") boolean bTemp) {
        CompanyConfigure configure = companyConfigureService.getCompanyById(id);
        configure.setPerContBodyTemp(bTemp);
        companyConfigureService.saveCompany(configure);

        return new ResponseEntity<>(configure, HttpStatus.OK);
    }

    @PostMapping("permanent/contract-mask-detection/{id}/{mDete}")
    public ResponseEntity<CompanyConfigure> setPermanentContractMask(@PathVariable("id") long id, @PathVariable("mDete") boolean mDete) {
        CompanyConfigure configure = companyConfigureService.getCompanyById(id);
        configure.setPerContMask(mDete);
        companyConfigureService.saveCompany(configure);

        return new ResponseEntity<>(configure, HttpStatus.OK);
    }

    @PostMapping("permanent/contract-pass-tracking/{id}/{pass}")
    public ResponseEntity<CompanyConfigure> setPerCPass(@PathVariable("id") long id, @PathVariable("pass") String pass) {
        CompanyConfigure configure = companyConfigureService.getCompanyById(id);
        configure.setPerContPassTracking(pass);
        companyConfigureService.saveCompany(configure);
        return new ResponseEntity<>(configure, HttpStatus.OK);
    }

    /*
       Arun Contract
     */
    @PostMapping("contract-body-temperature/{id}/{bTemp}")
    public ResponseEntity<CompanyConfigure> setContBodyTemp(@PathVariable("id") long id, @PathVariable("bTemp") boolean bTemp) {
        CompanyConfigure configure = companyConfigureService.getCompanyById(id);
        configure.setContBodyTemp(bTemp);
        companyConfigureService.saveCompany(configure);

        return new ResponseEntity<>(configure, HttpStatus.OK);
    }

    @PostMapping("contract-mask-detection/{id}/{mDete}")
    public ResponseEntity<CompanyConfigure> setContractMask(@PathVariable("id") long id, @PathVariable("mDete") boolean mDete) {
        CompanyConfigure configure = companyConfigureService.getCompanyById(id);
        configure.setContMask(mDete);
        companyConfigureService.saveCompany(configure);

        return new ResponseEntity<>(configure, HttpStatus.OK);
    }

    @PostMapping("contract-pass-tracking/{id}/{pass}")
    public ResponseEntity<CompanyConfigure> setContPass(@PathVariable("id") long id, @PathVariable("pass") String pass) {
        CompanyConfigure configure = companyConfigureService.getCompanyById(id);
        configure.setContPassTracking(pass);
        companyConfigureService.saveCompany(configure);
        return new ResponseEntity<>(configure, HttpStatus.OK);
    }

    /*
        Arun quarantine
     */
    @PostMapping("set-threshould-temp/{id}/{temp}")
    public ResponseEntity<CompanyConfigure> setThreshouldTemp(@PathVariable("id") long id, @PathVariable("temp") double temp) {
        CompanyConfigure configure = companyConfigureService.getCompanyById(id);
        configure.setThreshouldTemp(temp);
        companyConfigureService.saveCompany(configure);
        return new ResponseEntity<>(configure, HttpStatus.OK);
    }

    @PostMapping("set-threshould-day-temp/{id}/{day}")
    public ResponseEntity<CompanyConfigure> setThreshouldDayTempChk(@PathVariable("id") long id, @PathVariable("day") double day) {
        CompanyConfigure configure = companyConfigureService.getCompanyById(id);
        configure.setThreshouldDayTempChk(day);
        companyConfigureService.saveCompany(configure);
        return new ResponseEntity<>(configure, HttpStatus.OK);
    }


    @PostMapping("set-quarntine-peroid/{id}/{peroid}")
    public ResponseEntity<CompanyConfigure> setQuarntinePeroid(@PathVariable("id") long id, @PathVariable("peroid") double peroid) {
        CompanyConfigure configure = companyConfigureService.getCompanyById(id);
        configure.setQurantinePeriod(peroid);
        companyConfigureService.saveCompany(configure);
        return new ResponseEntity<>(configure, HttpStatus.OK);
    }

    /*
        Arun dailyreport email config
     */

    @PostMapping("company-config/dialy-report-email/{id}")
    public ResponseEntity<CompanyConfigure> setDialyReport(@RequestBody DailyReportDto dialyReportDto, @PathVariable("id") long id) {
        CompanyConfigure configure = companyConfigureService.getCompanyById(id);
        configure.setDailyReport(dialyReportDto.isDailyReport());
        Optional<Department> department = departmentService.getDepartment(dialyReportDto.getDailyReportDepartmentId());
        configure.setDailyReportDepartment(department.get());
        configure.setDailyReportEmail(dialyReportDto.getDailyReportEmail());
        configure.setDailyReportEmployeeName(dialyReportDto.getDailyReportEmployeeName());
        configure.setDailyReportVisitor(dialyReportDto.isDailyReportVisitor());
        configure.setDailyReportVehicle(dialyReportDto.isDailyReportVehicle());
        configure.setDailyReportEmployee(dialyReportDto.isDailyReportEmployee());
        configure.setDailyReportPermanentContract(dialyReportDto.isDailyReportPermanentContract());
        configure.setDailyReportContract(configure.isContract());
        companyConfigureService.saveCompany(configure);
        return new ResponseEntity<>(configure, HttpStatus.OK);
    }

    @PostMapping("company-config/late-exit-report-email/{id}")
    public ResponseEntity<CompanyConfigure> setLateExitReportConfig(@RequestBody DailyReportDto dialyReportDto, @PathVariable("id") long id) {
        CompanyConfigure configure = companyConfigureService.getCompanyById(id);
        configure.setLateExitReport(dialyReportDto.isLateExitReport());
        Optional<Department> department = departmentService.getDepartment(dialyReportDto.getLateExitReportDepartmentId());
        configure.setLateExitReportDepartment(department.get());
        configure.setLateExitReportEmail(dialyReportDto.getLateExitReportEmail());
        configure.setLateExitReportEmployeeName(dialyReportDto.getLateExitReportEmployeeName());
        configure.setLateExitReportVisitor(dialyReportDto.isLateExitReportVisitor());
        configure.setLateExitReportVehicle(dialyReportDto.isLateExitReportVehicle());
        configure.setLateExitReportEmployee(dialyReportDto.isLateExitReportEmployee());
        configure.setLateExitReportPermanentContract(dialyReportDto.isLateExitReportPermanentContract());
        configure.setLateExitReportContract(configure.isLateExitReportContract());
        configure.setLateExitTime(dialyReportDto.getLateExitTime());
        companyConfigureService.saveCompany(configure);
        return new ResponseEntity<>(configure, HttpStatus.OK);
    }

    /**
     * Configure Trial and check for configured days
     */

    @PostMapping("company-config/trial/{id}")
    public ResponseEntity<CompanyConfigure> setTrialDateAndDays(@RequestBody DailyReportDto dialyReportDto, @PathVariable("id") long id) {
        CompanyConfigure configure = companyConfigureService.getCompanyById(id);
        configure.setTrialDate(dialyReportDto.getTrialDate());
        configure.setTrialDays(dialyReportDto.getTrialDays());
        configure.setCustomerName(dialyReportDto.getCustomerName());
        configure.setNumberOfPersons(dialyReportDto.getNumberOfPersons());
        companyConfigureService.saveCompany(configure);
        return new ResponseEntity<>(configure, HttpStatus.OK);
    }

    @PostMapping("company-config/commercial/{id}")
    public ResponseEntity<CompanyConfigure> setComercialDateAndDays(@RequestBody DailyReportDto dialyReportDto, @PathVariable("id") long id) {
        CompanyConfigure configure = companyConfigureService.getCompanyById(id);
        configure.setTrialDate(dialyReportDto.getTrialDate());
        configure.setTrialDays(365);
        configure.setCommercialEmail(dialyReportDto.getCommercialEmail());
        companyConfigureService.saveCompany(configure);
        DateUtil.sendEmai(dialyReportDto.getCommercialEmail(), "CO_EFFI", "Your licence updated");
        return new ResponseEntity<>(configure, HttpStatus.OK);
    }


    @GetMapping("company-config/trial")
    public void CheckTrialDays() {
        CompanyConfigure companyConfigure = companyConfigureService.getCompanyById(1);
        long actualTrialDays = DateUtil.getDifferenceDays(companyConfigure.getTrialDate(), new Date());
        if (actualTrialDays > companyConfigure.getTrialDays()) {
            throw new EntityNotFoundException("Your trial period has been expired");
        }
    }

    @PostMapping("company-configure/vivo-payment/{id}/{status}")
    public ResponseEntity<CompanyConfigure> setVivoPayment(@PathVariable("id") long id, @PathVariable("status") boolean status) {
        CompanyConfigure configure = companyConfigureService.getCompanyById(id);
        configure.setVivoPayment(status);
        companyConfigureService.saveCompany(configure);
        return new ResponseEntity<>(configure, HttpStatus.OK);
    }

    @PostMapping("company-configure/vivo-super-dashboardType/{id}/{status}")
    public ResponseEntity<CompanyConfigure> dashboardType(@PathVariable("id") long id, @PathVariable("status") boolean status) {
        CompanyConfigure configure = companyConfigureService.getCompanyById(id);
        configure.setDashboardTypeVivo(status);
        companyConfigureService.saveCompany(configure);
        return new ResponseEntity<>(configure, HttpStatus.OK);
    }

    @PostMapping("company-configure/vivo-rfidpass/{id}/{status}")
    public ResponseEntity<CompanyConfigure> vivoRfid(@PathVariable("id") long id, @PathVariable("status") boolean status) {
        CompanyConfigure configure = companyConfigureService.getCompanyById(id);
        configure.setVivoRfid(status);
        companyConfigureService.saveCompany(configure);
        return new ResponseEntity<>(configure, HttpStatus.OK);
    }

    @PostMapping("vivo/check-in-out/{id}/{status}")
    public ResponseEntity<CompanyConfigure> vivoCheck(@PathVariable("id") long id, @PathVariable("status") boolean status) {
        CompanyConfigure configure = companyConfigureService.getCompanyById(id);
        configure.setVivoCheckIO(status);
        companyConfigureService.saveCompany(configure);
        return new ResponseEntity<>(configure, HttpStatus.OK);
    }


    @PostMapping("form-config/{id}/{status}")
    public ResponseEntity<CompanyConfigure> formConfig(@PathVariable("id") long id, @PathVariable("status") boolean status) {
        CompanyConfigure configure = companyConfigureService.getCompanyById(id);
        configure.setFormConfig(status);
        ;
        companyConfigureService.saveCompany(configure);
        return new ResponseEntity<>(configure, HttpStatus.OK);
    }

    /**
     * SAM store
     *
     * @param id
     * @param status
     * @return
     */

    @PostMapping("company-config/sam-store-module/{id}/{status}")
    public ResponseEntity<CompanyConfigure> storeModule(@PathVariable("id") long id, @PathVariable("status") boolean status) {
        CompanyConfigure configure = companyConfigureService.getCompanyById(id);
        configure.setStoreModule(status);
        companyConfigureService.saveCompany(configure);
        return new ResponseEntity<>(configure, HttpStatus.OK);
    }


    /**
     * SAM mobileapp
     *
     * @param id
     * @param status
     * @return
     */
    @PostMapping("company-config/sam-mobileapp-module/{id}/{status}")
    public ResponseEntity<CompanyConfigure> mobileappModule(@PathVariable("id") long id, @PathVariable("status") boolean status) {
        CompanyConfigure configure = companyConfigureService.getCompanyById(id);
        configure.setMobileAppModule(status);
        companyConfigureService.saveCompany(configure);
        return new ResponseEntity<>(configure, HttpStatus.OK);
    }

    /**
     * SAM compoff
     *
     * @param id
     * @param status
     * @return
     */
    @PostMapping("company-config/sam-compoff-module/{id}/{status}")
    public ResponseEntity<CompanyConfigure> compOffModule(@PathVariable("id") long id, @PathVariable("status") boolean status) {
        CompanyConfigure configure = companyConfigureService.getCompanyById(id);
        configure.setCompOffModule(status);
        companyConfigureService.saveCompany(configure);
        return new ResponseEntity<>(configure, HttpStatus.OK);
    }


    @PostMapping("company-configure/ctc-settings")
    public ResponseEntity<CompanyConfigure> setCtc(@RequestBody CompanyConfigure companyConfigure) {
        CompanyConfigure companyConfigure1 = companyConfigureService.getCompanyById(companyConfigure.getId());
        if (companyConfigure != null) {
            companyConfigure1.setBasicSalary(companyConfigure.isBasicSalary());
            companyConfigure1.setVariableDearnessAllowance(companyConfigure.isVariableDearnessAllowance());
            companyConfigure1.setConveyanceAllowance(companyConfigure.isVariableDearnessAllowance());
        }
        return null;
    }

    @PostMapping("company-settings/{id}/{location}/{employees}")
    public ResponseEntity<CompanyConfigure> saveCompanySettings(@PathVariable("id") long id, @PathVariable("location") long location, @PathVariable("employees") long employees) {
        CompanyConfigure configure = companyConfigureService.getCompanyById(id);
        configure.setNoOfLocation(location);
        configure.setNoOfEmployee(employees);
        companyConfigureService.saveCompany(configure);
        return new ResponseEntity<>(configure, HttpStatus.OK);
    }

    @PostMapping("company-config/sam-foodManagementModule/{id}/{status}")
    public ResponseEntity<CompanyConfigure> foodManagementModule(@PathVariable("id") long id, @PathVariable("status") boolean status) {
        CompanyConfigure configure = companyConfigureService.getCompanyById(id);
        configure.setFoodManagementModule(status);
        companyConfigureService.saveCompany(configure);
        return new ResponseEntity<>(configure, HttpStatus.OK);
    }

    @PostMapping("report-settings/{id}")
    public ResponseEntity<CompanyConfigure> saveReportSettings(@PathVariable("id") long id, @RequestBody ReportDto reportDto) {
        CompanyConfigure configure = companyConfigureService.getCompanyById(id);

        configure.setAttendanceReport(reportDto.isAttendanceReport());
        configure.setAbsentReport(reportDto.isAbsentReport());
        configure.setLateEntryReport(reportDto.isLateEntryReport());
        configure.setEarlyCheckOutReport(reportDto.isEarlyCheckOutReport());
        configure.setOverTimeReport(reportDto.isOverTimeReport());
        configure.setNewJoiningsReport(reportDto.isNewJoiningsReport());
        configure.setExEmployeesReport(reportDto.isExEmployeesReport());
        configure.setMonthlyLeaveRegister(reportDto.isMonthlyLeaveRegister());
        configure.setMonthlyEpfoReport12A(reportDto.isMonthlyEpfoReport12A());
        configure.setMonthlyEsicReport(reportDto.isMonthlyEsicReport());
        configure.setProfessionalTaxReport(reportDto.isProfessionalTaxReport());
        configure.setRegisterOfAdultWorkers(reportDto.isRegisterOfAdultWorkers());
        configure.setMonthlySalaryRegister(reportDto.isMonthlySalaryRegister());
        configure.setForm22Report(reportDto.isForm22Report());
        configure.setInOutReport(reportDto.isInOutReport());
        configure.setTotalVehicles(reportDto.isTotalVehicles());
        configure.setVehicleTypeWise(reportDto.isVehicleTypeWise());
        configure.setExitWithInTime(reportDto.isExitWithInTime());
        configure.setExtraTimeExit(reportDto.isExtraTimeExit());
        configure.setBayWise(reportDto.isBayWise());
        configure.setPassExpire(reportDto.isPassExpire());
        configure.setDepartmentWise(reportDto.isDepartmentWise());
        configure.setVisitTypeWise(reportDto.isVisitTypeWise());
        configure.setVisitorExtraTimeExit(reportDto.isVisitorExtraTimeExit());
        configure.setExtraTimePayment(reportDto.isExtraTimePayment());
        configure.setTotalVisit(reportDto.isTotalVisit());
        configure.setDeniedCompany(reportDto.isDeniedCompany());
        configure.setDeniedVisitor(reportDto.isDeniedVisitor());

        companyConfigureService.saveCompany(configure);

        return new ResponseEntity<>(configure, HttpStatus.CREATED);
    }

    @GetMapping("get-report-settings")
    public ResponseEntity<List<ReportDto>> getReportSettings() {
        List<CompanyConfigure> companyConfigureList = companyConfigureService.getCompany();
        List<ReportDto> reportDtoList = new ArrayList<>();
        for (CompanyConfigure c : companyConfigureList) {
            ReportDto reportDto = new ReportDto();
            reportDto.setAttendanceReport(c.isAttendanceReport());
            reportDto.setAbsentReport(c.isAbsentReport());
            reportDto.setLateEntryReport(c.isLateEntryReport());
            reportDto.setEarlyCheckOutReport(c.isEarlyCheckOutReport());
            reportDto.setOverTimeReport(c.isOverTimeReport());
            reportDto.setNewJoiningsReport(c.isNewJoiningsReport());
            reportDto.setExEmployeesReport(c.isExEmployeesReport());
            reportDto.setMonthlyLeaveRegister(c.isMonthlyLeaveRegister());
            reportDto.setMonthlyEpfoReport12A(c.isMonthlyEpfoReport12A());
            reportDto.setMonthlyEsicReport(c.isMonthlyEsicReport());
            reportDto.setProfessionalTaxReport(c.isProfessionalTaxReport());
            reportDto.setRegisterOfAdultWorkers(c.isRegisterOfAdultWorkers());
            reportDto.setMonthlySalaryRegister(c.isMonthlySalaryRegister());
            reportDto.setForm22Report(c.isForm22Report());
            reportDto.setTotalVehicles(c.isTotalVehicles());
            reportDto.setVehicleTypeWise(c.isVehicleTypeWise());
            reportDto.setExitWithInTime(c.isExitWithInTime());
            reportDto.setExtraTimeExit(c.isExtraTimeExit());
            reportDto.setBayWise(c.isBayWise());
            reportDto.setPassExpire(c.isPassExpire());
            reportDto.setDepartmentWise(c.isDepartmentWise());
            reportDto.setVisitTypeWise(c.isVisitTypeWise());
            reportDto.setVisitorExtraTimeExit(c.isVisitorExtraTimeExit());
            reportDto.setExtraTimePayment(c.isExtraTimePayment());
            reportDto.setTotalVisit(c.isTotalVisit());
            reportDto.setDeniedCompany(c.isDeniedCompany());
            reportDto.setDeniedVisitor(c.isDeniedVisitor());
            reportDto.setInOutReport(c.isInOutReport());
            reportDtoList.add(reportDto);
        }
        return new ResponseEntity<>(reportDtoList, HttpStatus.OK);
    }

    @PostMapping("company-config/paramount-build/{id}/{status}")
    public ResponseEntity<CompanyConfigure> paramount(@PathVariable("id") long id, @PathVariable("status") boolean status) {
        CompanyConfigure configure = companyConfigureService.getCompanyById(id);
        configure.setParamountBuild(status);
        companyConfigureService.saveCompany(configure);
        return new ResponseEntity<>(configure, HttpStatus.OK);
    }

    @PostMapping("company-config/ot-ps/{id}/{status}")
    public ResponseEntity<CompanyConfigure> otps(@PathVariable("id") long id, @PathVariable("status") boolean status) {
        CompanyConfigure configure = companyConfigureService.getCompanyById(id);
        configure.setOtForPermanentStaff(status);
        companyConfigureService.saveCompany(configure);
        setOtForEmployee(EmployeeType.PERMANENT, status);
        return new ResponseEntity<>(configure, HttpStatus.OK);
    }

    @PostMapping("company-config/ot-pw/{id}/{status}")
    public ResponseEntity<CompanyConfigure> otpw(@PathVariable("id") long id, @PathVariable("status") boolean status) {
        CompanyConfigure configure = companyConfigureService.getCompanyById(id);
        configure.setOtForPermanentWorker(status);
        companyConfigureService.saveCompany(configure);
        setOtForEmployee(EmployeeType.PERMANENT_WORKER, status);
        return new ResponseEntity<>(configure, HttpStatus.OK);
    }

    @PostMapping("company-config/ot-tc/{id}/{status}")
    public ResponseEntity<CompanyConfigure> ottc(@PathVariable("id") long id, @PathVariable("status") boolean status) {
        CompanyConfigure configure = companyConfigureService.getCompanyById(id);
        configure.setOtForTemporaryContract(status);
        companyConfigureService.saveCompany(configure);
        setOtForEmployee(EmployeeType.CONTRACT, status);
        return new ResponseEntity<>(configure, HttpStatus.OK);
    }

    @PostMapping("company-config/ot-pc/{id}/{status}")
    public ResponseEntity<CompanyConfigure> otpc(@PathVariable("id") long id, @PathVariable("status") boolean status) {
        CompanyConfigure configure = companyConfigureService.getCompanyById(id);
        configure.setOtForPermanentContract(status);
        companyConfigureService.saveCompany(configure);
        setOtForEmployee(EmployeeType.PERMANENT_CONTRACT, status);
        return new ResponseEntity<>(configure, HttpStatus.OK);
    }

    public void setOtForEmployee(EmployeeType employeeType, boolean status) {
        List<Employee> employees = employeeService.getEmployeeByType(employeeType, true);
        for (Employee employee : employees) {
            employee.setOtRequired(status);
            employeeService.save(employee);
        }
    }

    @PostMapping("company-config/gebe-build/{id}/{status}")
    public ResponseEntity<CompanyConfigure> gebe(@PathVariable("id") long id, @PathVariable("status") boolean status) {
        CompanyConfigure configure = companyConfigureService.getCompanyById(id);
        configure.setGeBuild(status);
        companyConfigureService.saveCompany(configure);
        return new ResponseEntity<>(configure, HttpStatus.OK);
    }

    @PostMapping("company-config/ot-factor/{id}/{factor}")
    public ResponseEntity<CompanyConfigure> otFactor(@PathVariable("id") long id, @PathVariable("factor") double factor) {
        CompanyConfigure configure = companyConfigureService.getCompanyById(id);
        configure.setOtFactor(factor);
        companyConfigureService.saveCompany(configure);
        return new ResponseEntity<>(configure, HttpStatus.OK);
    }

    @PostMapping("company-config/ot-grace/{id}/{grace}")
    public ResponseEntity<CompanyConfigure> otGrace(@PathVariable("id") long id, @PathVariable("grace") long grace) {
        CompanyConfigure configure = companyConfigureService.getCompanyById(id);
        configure.setOtGraceTime(grace);
        companyConfigureService.saveCompany(configure);
        return new ResponseEntity<>(configure, HttpStatus.OK);
    }

    @PostMapping("company-config/ot-factor/ot-grace/{id}/{factor}/{grace}/{otPercentage}")
    public ResponseEntity<CompanyConfigure> otGraceAndFactor(@PathVariable("id") long id, @PathVariable("factor") double factor, @PathVariable("grace") long grace, @PathVariable("otPercentage") long otPercentage) {
        CompanyConfigure configure = companyConfigureService.getCompanyById(id);
        configure.setOtGraceTime(grace);
        configure.setOtFactor(factor);
        configure.setOtPercenatge(otPercentage);
        configure.setOtTimes((otPercentage / 100));
        companyConfigureService.saveCompany(configure);
        return new ResponseEntity<>(configure, HttpStatus.OK);
    }

    @PostMapping("company-config/early-out-threshold/{id}/{grace}")
    public ResponseEntity<CompanyConfigure> earlyOutThreshold(@PathVariable("id") long id, @PathVariable("grace") long grace) {
        CompanyConfigure configure = companyConfigureService.getCompanyById(id);
        configure.setEarlyOutThresholdInMins(grace);
        companyConfigureService.saveCompany(configure);
        return new ResponseEntity<>(configure, HttpStatus.OK);
    }

    @PostMapping("company-config/attendance-settings")
    public void attendanceSettings(@RequestBody CompanyConfigDto companyConfigDto) {

        CompanyConfigure configure = companyConfigureService.getCompanyById(companyConfigDto.getId());
        configure.setAttendanceBonus(companyConfigDto.isAttendanceBonus());
        configure.setBonusAmount(companyConfigDto.getBonusAmount());
        configure.setLateEntryLoss(companyConfigDto.isLateEntryLoss());
        configure.setEarlyOutLoss(companyConfigDto.isEarlyOutLoss());
        configure.setSalaryRegenerate(companyConfigDto.isSalaryRegenerate());
        companyConfigureService.saveCompany(configure);
    }

    @PostMapping("company-config/sam-advancedFoodManagement/{id}/{status}")
    public ResponseEntity<CompanyConfigure> advancedFoodManagementModule(@PathVariable("id") long id, @PathVariable("status") boolean status) {
        CompanyConfigure configure = companyConfigureService.getCompanyById(id);
        configure.setAdvancedFoodManagementModule(status);
        companyConfigureService.saveCompany(configure);
        return new ResponseEntity<>(configure, HttpStatus.OK);
    }

    @PostMapping("company-config/sam-dailyMenuSettings/{id}/{status}")
    public ResponseEntity<CompanyConfigure> dailyMenuSettingsModule(@PathVariable("id") long id, @PathVariable("status") boolean status) {
        CompanyConfigure configure = companyConfigureService.getCompanyById(id);
        configure.setDailyMenuSettingsModule(status);
        companyConfigureService.saveCompany(configure);
        return new ResponseEntity<>(configure, HttpStatus.OK);
    }

    @PostMapping("company-config/sam-currencyAndRecharge/{id}/{status}")
    public ResponseEntity<CompanyConfigure> currencyAndRechargeModule(@PathVariable("id") long id, @PathVariable("status") boolean status) {
        CompanyConfigure configure = companyConfigureService.getCompanyById(id);
        configure.setCurrencyAndRechargeModule(status);
        companyConfigureService.saveCompany(configure);
        return new ResponseEntity<>(configure, HttpStatus.OK);
    }

    @PostMapping("company-config/sam-feedbackManagement/{id}/{status}")
    public ResponseEntity<CompanyConfigure> feedbackManagementModule(@PathVariable("id") long id, @PathVariable("status") boolean status) {
        CompanyConfigure configure = companyConfigureService.getCompanyById(id);
        configure.setFeedbackManagementModule(status);
        companyConfigureService.saveCompany(configure);
        return new ResponseEntity<>(configure, HttpStatus.OK);
    }

    @PostMapping("company-config/sop-name/{id}/{status}")
    public ResponseEntity<CompanyConfigure> sopName(@PathVariable("id") long id, @PathVariable("status") boolean status) {
        CompanyConfigure configure = companyConfigureService.getCompanyById(id);
        configure.setDigitalSopMaintenance(status);
        companyConfigureService.saveCompany(configure);
        return new ResponseEntity<>(configure, HttpStatus.OK);
    }

    @PostMapping("company-config/elearning-product-header/{id}/{productNameHeader}")
    public ResponseEntity<CompanyConfigure> elearningelearningheader(@PathVariable("id") long id,@PathVariable("productNameHeader") String productNameHeader) {
        CompanyConfigure configure = companyConfigureService.getCompanyById(id);
        configure.setProductNameHeader(productNameHeader);
        companyConfigureService.saveCompany(configure);
        return new ResponseEntity<>(configure, HttpStatus.OK);
    }

    @PostMapping("company-config/elearning-file-limit/{id}/{fileLimit}")
    public ResponseEntity<CompanyConfigure> fileLimit(@PathVariable("id") long id,@PathVariable("fileLimit") long fileLimit) {
        CompanyConfigure configure = companyConfigureService.getCompanyById(id);
        configure.setFileLimit(fileLimit);
        companyConfigureService.saveCompany(configure);
        return new ResponseEntity<>(configure, HttpStatus.OK);
    }


    @PostMapping("company-config/sam-visitor-dashboard/{id}/{status}")
    public ResponseEntity<CompanyConfigure> visitorDash(@PathVariable("id") long id, @PathVariable("status") boolean status) {
        CompanyConfigure configure = companyConfigureService.getCompanyById(id);
        configure.setVisitorDashboard(status);
        companyConfigureService.saveCompany(configure);
        return new ResponseEntity<>(configure, HttpStatus.OK);
    }


    @PostMapping("company-config/sam-employee-dashboard/{id}/{status}")
    public ResponseEntity<CompanyConfigure> empDash(@PathVariable("id") long id, @PathVariable("status") boolean status) {
        CompanyConfigure configure = companyConfigureService.getCompanyById(id);
        configure.setEmployeeDashboard(status);
        companyConfigureService.saveCompany(configure);
        return new ResponseEntity<>(configure, HttpStatus.OK);
    }

    @PostMapping("company-config/elearning-test-count/{id}/{count}")
    public ResponseEntity<CompanyConfigure> testCount(@PathVariable("id") long id, @PathVariable("count") long count) {
        CompanyConfigure configure = companyConfigureService.getCompanyById(id);
        configure.setTestCount(count);
        companyConfigureService.saveCompany(configure);
        return new ResponseEntity<>(configure, HttpStatus.OK);
    }

    @PostMapping("company-config/employee-releave/{id}/{status}")
    public ResponseEntity<CompanyConfigure> employeeReleaveWithExit(@PathVariable("id") long id, @PathVariable("status") boolean status) {
        CompanyConfigure configure = companyConfigureService.getCompanyById(id);
        configure.setReleaveWithExitProcess(status);
        companyConfigureService.saveCompany(configure);
        return new ResponseEntity<>(configure, HttpStatus.OK);
    }

    @PostMapping("company-config/attendance-with-headcount/{id}/{status}")
    public ResponseEntity<CompanyConfigure> employeeAttendanceWithHeadcount(@PathVariable("id") long id, @PathVariable("status") boolean status) {
        CompanyConfigure configure = companyConfigureService.getCompanyById(id);
        configure.setAttendanceWithHeadCount(status);
        companyConfigureService.saveCompany(configure);
        return new ResponseEntity<>(configure, HttpStatus.OK);
    }



}
