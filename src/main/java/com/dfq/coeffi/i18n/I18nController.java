package com.dfq.coeffi.i18n;

import com.dfq.coeffi.entity.hr.employee.Employee;
import com.dfq.coeffi.service.hr.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@Slf4j
public class I18nController {

    @Autowired
    MessageSource messageSource;
    @Autowired
    EmployeeService employeeService;


    @GetMapping("i18n/createEmployee/{id}")
    public Map<String, String> messageSource(@PathVariable long id) {
        Optional<Employee> employee = employeeService.getEmployee(id);
        Employee employee1 = employee.get();
        String lang = employee1.getLanguage().getLanguage();
        String country = employee1.getLanguage().getCountry();
        String baseName = "Hr-Employee-CreateEmployee";
        Locale locale = new Locale(lang, country);
        ResourceBundle resourceBundle = ResourceBundle.getBundle(baseName, locale);
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename(baseName);
        Map bundleToMap = resourceBundleToMap(resourceBundle);
        Set<String> keys = bundleToMap.keySet();
        Map m = resourceMessageSourceBundleToMap(messageSource, keys, locale);
        return m;
    }

    @GetMapping("i18n/admin-visitor/{id}")
    public Map<String, String> visitorCreate(@PathVariable long id) {
        Optional<Employee> employee = employeeService.getEmployee(id);
        Employee employee1 = employee.get();
        String lang = employee1.getLanguage().getLanguage();
        String country = employee1.getLanguage().getCountry();
        String baseName = "Admin-VisitorIO";
        Locale locale = new Locale(lang, country);
        ResourceBundle resourceBundle = ResourceBundle.getBundle(baseName, locale);
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename(baseName);
        Map bundleToMap = resourceBundleToMap(resourceBundle);
        Set<String> keys = bundleToMap.keySet();
        Map m = resourceMessageSourceBundleToMap(messageSource, keys, locale);
        return m;
    }

    @GetMapping("i18n/admin-vehicle/{id}")
    public Map<String, String> vehicleCreate(@PathVariable long id) {
        Optional<Employee> employee = employeeService.getEmployee(id);
        Employee employee1 = employee.get();
        String lang = employee1.getLanguage().getLanguage();
        String country = employee1.getLanguage().getCountry();
        String baseName = "Admin-VehicleIO";
        Locale locale = new Locale(lang, country);
        ResourceBundle resourceBundle = ResourceBundle.getBundle(baseName, locale);
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename(baseName);
        Map bundleToMap = resourceBundleToMap(resourceBundle);
        Set<String> keys = bundleToMap.keySet();
        Map m = resourceMessageSourceBundleToMap(messageSource, keys, locale);
        return m;
    }

    @GetMapping("i18n/elearning/{id}")
    public Map<String, String> createElearning(@PathVariable long id) {
        Optional<Employee> employee = employeeService.getEmployee(id);
        Employee employee1 = employee.get();
        String lang = employee1.getLanguage().getLanguage();
        String country = employee1.getLanguage().getCountry();
        String baseName = "E-Learning";
        Locale locale = new Locale(lang, country);
        ResourceBundle resourceBundle = ResourceBundle.getBundle(baseName, locale);
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename(baseName);
        Map bundleToMap = resourceBundleToMap(resourceBundle);
        Set<String> keys = bundleToMap.keySet();
        Map m = resourceMessageSourceBundleToMap(messageSource, keys, locale);
        return m;
    }

    @GetMapping("i18n/digitalsop/{id}")
    public Map<String, String> createdigitalsop(@PathVariable long id) {
        Optional<Employee> employee = employeeService.getEmployee(id);
        Employee employee1 = employee.get();
        String lang = employee1.getLanguage().getLanguage();
        String country = employee1.getLanguage().getCountry();
        String baseName = "Digital-SOP";
        Locale locale = new Locale(lang, country);
        ResourceBundle resourceBundle = ResourceBundle.getBundle(baseName, locale);
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename(baseName);
        Map bundleToMap = resourceBundleToMap(resourceBundle);
        Set<String> keys = bundleToMap.keySet();
        Map m = resourceMessageSourceBundleToMap(messageSource, keys, locale);
        return m;
    }

    @GetMapping("i18n/attendanceview/{id}")
    public Map<String, String> createattdanceview(@PathVariable long id) {
        Optional<Employee> employee = employeeService.getEmployee(id);
        Employee employee1 = employee.get();
        String lang = employee1.getLanguage().getLanguage();
        String country = employee1.getLanguage().getCountry();
        String baseName = "Attendance-View-Attendance";
        Locale locale = new Locale(lang, country);
        ResourceBundle resourceBundle = ResourceBundle.getBundle(baseName, locale);
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename(baseName);
        Map bundleToMap = resourceBundleToMap(resourceBundle);
        Set<String> keys = bundleToMap.keySet();
        Map m = resourceMessageSourceBundleToMap(messageSource, keys, locale);
        return m;
    }

    @GetMapping("i18n/user-visiotorio/{id}")
    public Map<String, String> uservisitor(@PathVariable long id) {
        Optional<Employee> employee = employeeService.getEmployee(id);
        Employee employee1 = employee.get();
        String lang = employee1.getLanguage().getLanguage();
        String country = employee1.getLanguage().getCountry();
        String baseName = "User-VisitorIO";
        Locale locale = new Locale(lang, country);
        ResourceBundle resourceBundle = ResourceBundle.getBundle(baseName, locale);
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename(baseName);
        Map bundleToMap = resourceBundleToMap(resourceBundle);
        Set<String> keys = bundleToMap.keySet();
        Map m = resourceMessageSourceBundleToMap(messageSource, keys, locale);
        return m;
    }

    @GetMapping("i18n/user-vehicleio/{id}")
    public Map<String, String> uservehicle(@PathVariable long id) {
        Optional<Employee> employee = employeeService.getEmployee(id);
        Employee employee1 = employee.get();
        String lang = employee1.getLanguage().getLanguage();
        String country = employee1.getLanguage().getCountry();
        String baseName = "User-VehicleIO";
        Locale locale = new Locale(lang, country);
        ResourceBundle resourceBundle = ResourceBundle.getBundle(baseName, locale);
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename(baseName);
        Map bundleToMap = resourceBundleToMap(resourceBundle);
        Set<String> keys = bundleToMap.keySet();
        Map m = resourceMessageSourceBundleToMap(messageSource, keys, locale);
        return m;
    }

    @GetMapping("i18n/user-digitalsop/{id}")
    public Map<String, String> userdigitalsop(@PathVariable long id) {
        Optional<Employee> employee = employeeService.getEmployee(id);
        Employee employee1 = employee.get();
        String lang = employee1.getLanguage().getLanguage();
        String country = employee1.getLanguage().getCountry();
        String baseName = "User-DigitalSOP";
        Locale locale = new Locale(lang, country);
        ResourceBundle resourceBundle = ResourceBundle.getBundle(baseName, locale);
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename(baseName);
        Map bundleToMap = resourceBundleToMap(resourceBundle);
        Set<String> keys = bundleToMap.keySet();
        Map m = resourceMessageSourceBundleToMap(messageSource, keys, locale);
        return m;
    }

    @GetMapping("i18n/user-elearning/{id}")
    public Map<String, String> userelearing(@PathVariable long id) {
        Optional<Employee> employee = employeeService.getEmployee(id);
        Employee employee1 = employee.get();
        String lang = employee1.getLanguage().getLanguage();
        String country = employee1.getLanguage().getCountry();
        String baseName = "User-ELearning";
        Locale locale = new Locale(lang, country);
        ResourceBundle resourceBundle = ResourceBundle.getBundle(baseName, locale);
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename(baseName);
        Map bundleToMap = resourceBundleToMap(resourceBundle);
        Set<String> keys = bundleToMap.keySet();
        Map m = resourceMessageSourceBundleToMap(messageSource, keys, locale);
        return m;
    }

    @GetMapping("i18n/facility-manager/{id}")
    public Map<String, String> userfacilitymanager(@PathVariable long id) {
        String lang = "ms";
        String country = "MY";
        String baseName = "Vehicle-FacilityManager";
        Locale locale = new Locale(lang, country);
        ResourceBundle resourceBundle = ResourceBundle.getBundle(baseName, locale);
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename(baseName);
        Map bundleToMap = resourceBundleToMap(resourceBundle);
        Set<String> keys = bundleToMap.keySet();
        Map m = resourceMessageSourceBundleToMap(messageSource, keys, locale);
        return m;
    }

    @GetMapping("i18n/security-manager/{id}")
    public Map<String, String> usersecuritymanager(@PathVariable long id) {
        Optional<Employee> employee = employeeService.getEmployee(id);
        Employee employee1 = employee.get();
        String lang = employee1.getLanguage().getLanguage();
        String country = employee1.getLanguage().getCountry();
        String baseName = "Visitor-SecurityManager";
        Locale locale = new Locale(lang, country);
        ResourceBundle resourceBundle = ResourceBundle.getBundle(baseName, locale);
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename(baseName);
        Map bundleToMap = resourceBundleToMap(resourceBundle);
        Set<String> keys = bundleToMap.keySet();
        Map m = resourceMessageSourceBundleToMap(messageSource, keys, locale);
        return m;
    }

    @GetMapping("i18n/payroll/{id}")
    public Map<String, String> userpayroll(@PathVariable long id) {
        Optional<Employee> employee = employeeService.getEmployee(id);
        Employee employee1 = employee.get();
        String lang = employee1.getLanguage().getLanguage();
        String country = employee1.getLanguage().getCountry();
        String baseName = "Payroll";
        Locale locale = new Locale(lang, country);
        ResourceBundle resourceBundle = ResourceBundle.getBundle(baseName, locale);
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename(baseName);
        Map bundleToMap = resourceBundleToMap(resourceBundle);
        Set<String> keys = bundleToMap.keySet();
        Map m = resourceMessageSourceBundleToMap(messageSource, keys, locale);
        return m;
    }

    @GetMapping("i18n/shift/{id}")
    public Map<String, String> usershift(@PathVariable long id) {
        Optional<Employee> employee = employeeService.getEmployee(id);
        Employee employee1 = employee.get();
        String lang = employee1.getLanguage().getLanguage();
        String country = employee1.getLanguage().getCountry();
        String baseName = "Shift";
        Locale locale = new Locale(lang, country);
        ResourceBundle resourceBundle = ResourceBundle.getBundle(baseName, locale);
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename(baseName);
        Map bundleToMap = resourceBundleToMap(resourceBundle);
        Set<String> keys = bundleToMap.keySet();
        Map m = resourceMessageSourceBundleToMap(messageSource, keys, locale);
        return m;
    }

    @GetMapping("i18n/leave-management/{id}")
    public Map<String, String> userleave(@PathVariable long id) {
        Optional<Employee> employee = employeeService.getEmployee(id);
        Employee employee1 = employee.get();
        String lang = employee1.getLanguage().getLanguage();
        String country = employee1.getLanguage().getCountry();
        String baseName = "Leave-Management";
        Locale locale = new Locale(lang, country);
        ResourceBundle resourceBundle = ResourceBundle.getBundle(baseName, locale);
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename(baseName);
        Map bundleToMap = resourceBundleToMap(resourceBundle);
        Set<String> keys = bundleToMap.keySet();
        Map m = resourceMessageSourceBundleToMap(messageSource, keys, locale);
        return m;
    }

    @GetMapping("i18n/holiday/{id}")
    public Map<String, String> userholiday(@PathVariable long id) {
        Optional<Employee> employee = employeeService.getEmployee(id);
        Employee employee1 = employee.get();
        String lang = employee1.getLanguage().getLanguage();
        String country = employee1.getLanguage().getCountry();
        String baseName = "Holiday";
        Locale locale = new Locale(lang, country);
        ResourceBundle resourceBundle = ResourceBundle.getBundle(baseName, locale);
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename(baseName);
        Map bundleToMap = resourceBundleToMap(resourceBundle);
        Set<String> keys = bundleToMap.keySet();
        Map m = resourceMessageSourceBundleToMap(messageSource, keys, locale);
        return m;
    }

    @GetMapping("i18n/gatepass/{id}")
    public Map<String, String> userpaygatepass(@PathVariable long id) {
        Optional<Employee> employee = employeeService.getEmployee(id);
        Employee employee1 = employee.get();
        String lang = employee1.getLanguage().getLanguage();
        String country = employee1.getLanguage().getCountry();
        String baseName = "GatePass";
        Locale locale = new Locale(lang, country);
        ResourceBundle resourceBundle = ResourceBundle.getBundle(baseName, locale);
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename(baseName);
        Map bundleToMap = resourceBundleToMap(resourceBundle);
        Set<String> keys = bundleToMap.keySet();
        Map m = resourceMessageSourceBundleToMap(messageSource, keys, locale);
        return m;
    }

    @GetMapping("i18n/emailsetting/{id}")
    public Map<String, String> useremailsetting(@PathVariable long id) {
        Optional<Employee> employee = employeeService.getEmployee(id);
        Employee employee1 = employee.get();
        String lang = employee1.getLanguage().getLanguage();
        String country = employee1.getLanguage().getCountry();
        String baseName = "EmailSetting";
        Locale locale = new Locale(lang, country);
        ResourceBundle resourceBundle = ResourceBundle.getBundle(baseName, locale);
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename(baseName);
        Map bundleToMap = resourceBundleToMap(resourceBundle);
        Set<String> keys = bundleToMap.keySet();
        Map m = resourceMessageSourceBundleToMap(messageSource, keys, locale);
        return m;
    }

    @GetMapping("i18n/employeeperformance/{id}")
    public Map<String, String> useremployee(@PathVariable long id) {
        Optional<Employee> employee = employeeService.getEmployee(id);
        Employee employee1 = employee.get();
        String lang = employee1.getLanguage().getLanguage();
        String country = employee1.getLanguage().getCountry();
        String baseName = "EmployeePerformanceExit";
        Locale locale = new Locale(lang, country);
        ResourceBundle resourceBundle = ResourceBundle.getBundle(baseName, locale);
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename(baseName);
        Map bundleToMap = resourceBundleToMap(resourceBundle);
        Set<String> keys = bundleToMap.keySet();
        Map m = resourceMessageSourceBundleToMap(messageSource, keys, locale);
        return m;
    }

    @GetMapping("i18n/department/{id}")
    public Map<String, String> userdepartment(@PathVariable long id) {
        Optional<Employee> employee = employeeService.getEmployee(id);
        Employee employee1 = employee.get();
        String lang = employee1.getLanguage().getLanguage();
        String country = employee1.getLanguage().getCountry();
        String baseName = "Department";
        Locale locale = new Locale(lang, country);
        ResourceBundle resourceBundle = ResourceBundle.getBundle(baseName, locale);
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename(baseName);
        Map bundleToMap = resourceBundleToMap(resourceBundle);
        Set<String> keys = bundleToMap.keySet();
        Map m = resourceMessageSourceBundleToMap(messageSource, keys, locale);
        return m;
    }

    @GetMapping("i18n/companypolicy/{id}")
    public Map<String, String> usercompany(@PathVariable long id) {
        Optional<Employee> employee = employeeService.getEmployee(id);
        Employee employee1 = employee.get();
        String lang = employee1.getLanguage().getLanguage();
        String country = employee1.getLanguage().getCountry();
        String baseName = "CompanyPolicy";
        Locale locale = new Locale(lang, country);
        ResourceBundle resourceBundle = ResourceBundle.getBundle(baseName, locale);
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename(baseName);
        Map bundleToMap = resourceBundleToMap(resourceBundle);
        Set<String> keys = bundleToMap.keySet();
        Map m = resourceMessageSourceBundleToMap(messageSource, keys, locale);
        return m;
    }

    @GetMapping("i18n/communication/{id}")
    public Map<String, String> usercommunication(@PathVariable long id) {
        Optional<Employee> employee = employeeService.getEmployee(id);
        Employee employee1 = employee.get();
        String lang = employee1.getLanguage().getLanguage();
        String country = employee1.getLanguage().getCountry();
        String baseName = "Communication";
        Locale locale = new Locale(lang, country);
        ResourceBundle resourceBundle = ResourceBundle.getBundle(baseName, locale);
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename(baseName);
        Map bundleToMap = resourceBundleToMap(resourceBundle);
        Set<String> keys = bundleToMap.keySet();
        Map m = resourceMessageSourceBundleToMap(messageSource, keys, locale);
        return m;
    }

    @GetMapping("i18n/group/{id}")
    public Map<String, String> usergroup(@PathVariable long id) {
        Optional<Employee> employee = employeeService.getEmployee(id);
        Employee employee1 = employee.get();
        String lang = employee1.getLanguage().getLanguage();
        String country = employee1.getLanguage().getCountry();
        String baseName = "Group";
        Locale locale = new Locale(lang, country);
        ResourceBundle resourceBundle = ResourceBundle.getBundle(baseName, locale);
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename(baseName);
        Map bundleToMap = resourceBundleToMap(resourceBundle);
        Set<String> keys = bundleToMap.keySet();
        Map m = resourceMessageSourceBundleToMap(messageSource, keys, locale);
        return m;
    }

    @GetMapping("i18n/report")
    public Map<String, String> userreport(@PathVariable long id) {
        Optional<Employee> employee = employeeService.getEmployee(id);
        Employee employee1 = employee.get();
        String lang = employee1.getLanguage().getLanguage();
        String country = employee1.getLanguage().getCountry();
        String baseName = "Report";
        Locale locale = new Locale(lang, country);
        ResourceBundle resourceBundle = ResourceBundle.getBundle(baseName, locale);
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename(baseName);
        Map bundleToMap = resourceBundleToMap(resourceBundle);
        Set<String> keys = bundleToMap.keySet();
        Map m = resourceMessageSourceBundleToMap(messageSource, keys, locale);
        return m;
    }

    @GetMapping("i18n/attendance/{id}")
    public Map<String, String> userattendance(@PathVariable long id) {
        Optional<Employee> employee = employeeService.getEmployee(id);
        Employee employee1 = employee.get();
        String lang = employee1.getLanguage().getLanguage();
        String country = employee1.getLanguage().getCountry();
        String baseName = "Attendance";
        Locale locale = new Locale(lang, country);
        ResourceBundle resourceBundle = ResourceBundle.getBundle(baseName, locale);
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename(baseName);
        Map bundleToMap = resourceBundleToMap(resourceBundle);
        Set<String> keys = bundleToMap.keySet();
        Map m = resourceMessageSourceBundleToMap(messageSource, keys, locale);
        return m;
    }

    @GetMapping("i18n/hr-employee-details/{id}")
    public Map<String, String> useremployeedetails(@PathVariable long id) {
        Optional<Employee> employee = employeeService.getEmployee(id);
        Employee employee1 = employee.get();
        String lang = employee1.getLanguage().getLanguage();
        String country = employee1.getLanguage().getCountry();
        String baseName = "Hr-Employee-EmployeeDetails";
        Locale locale = new Locale(lang, country);
        ResourceBundle resourceBundle = ResourceBundle.getBundle(baseName, locale);
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename(baseName);
        Map bundleToMap = resourceBundleToMap(resourceBundle);
        Set<String> keys = bundleToMap.keySet();
        Map m = resourceMessageSourceBundleToMap(messageSource, keys, locale);
        return m;
    }


    private static Map<String, String> resourceBundleToMap(final ResourceBundle bundle) {
        final Map<String, String> bundleMap = new HashMap<>();
        for (String key : bundle.keySet()) {
            final String value = bundle.getString(key);
            bundleMap.put(key, value);
        }
        return bundleMap;
    }

    private static Map<String, String> resourceMessageSourceBundleToMap(ResourceBundleMessageSource messageSourcebundle, Set<String> keys, Locale l) {
        final Map<String, String> bundleMap = new HashMap<>();
        for (String key : keys) {
            final String value = messageSourcebundle.getMessage(key, null, l);
            bundleMap.put(key, value);
        }
        return bundleMap;
    }


    @PostMapping("i18n-employee-language-setting/{empId}")
    public void setLanguageForEmployee(@RequestBody LanguageDto languageForEmployee) {
        Optional<Employee> employee = employeeService.getEmployee(languageForEmployee.getEmpId());
        Employee employee1 = employee.get();
        employee1.setLanguage(languageForEmployee.getDefaultLanguage());
        employeeService.save(employee1);
    }


}
