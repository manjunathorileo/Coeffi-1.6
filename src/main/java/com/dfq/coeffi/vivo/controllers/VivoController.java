package com.dfq.coeffi.vivo.controllers;

import com.dfq.coeffi.auditlog.log.ApplicationLogService;
import com.dfq.coeffi.controller.BaseController;
import com.dfq.coeffi.dto.DateDto;
import com.dfq.coeffi.entity.hr.employee.AttendanceStatus;
import com.dfq.coeffi.entity.hr.employee.Employee;
import com.dfq.coeffi.entity.payroll.EmployeeAttendance;
import com.dfq.coeffi.evacuationApi.EvacuationTracker;
import com.dfq.coeffi.evacuationApi.EvacuationTrackerRepository;
import com.dfq.coeffi.policy.document.Document;
import com.dfq.coeffi.policy.document.FileStorageService;
import com.dfq.coeffi.superadmin.Entity.CompanyConfigure;
import com.dfq.coeffi.superadmin.Services.CompanyConfigureService;
import com.dfq.coeffi.visitor.Entities.VisitorDto;
import com.dfq.coeffi.visitor.Entities.VisitorPass;
import com.dfq.coeffi.vivo.SimpleMessagingTemplate1;
import com.dfq.coeffi.vivo.entity.*;
import com.dfq.coeffi.vivo.repository.VivoPassRepo;
import com.dfq.coeffi.vivo.service.*;
import jxl.write.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.Boolean;
import java.security.Principal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Period;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static jxl.format.Alignment.*;


@RestController
@Slf4j
public class VivoController extends BaseController {

    @Autowired
    RecordingService recordingService;
    @Autowired
    TimeConfigService timeConfigService;
    @Autowired
    CompanyService companyService;
    @Autowired
    VivoInfoService vivoInfoService;
    @Autowired
    PaymentRulesService paymentRulesService;
    @Autowired
    TypeOfVehicleService typeOfVehicleService;
    @Autowired
    BayService bayService;
    @Autowired
    SlotService slotService;
    @Autowired
    CompanyConfigureService companyConfigureService;
    @Autowired
    VivoPassRepo vivoPassRepo;
    @Autowired
    private ApplicationLogService applicationLogService;

    @PostMapping("vivo/recording")
    public ResponseEntity<List<Recording>> save(@RequestBody List<Recording> recordingList, Principal principal) {
        for (Recording recording : recordingList) {
            recording.setStatus(true);
            recordingService.save(recording);
            //-----------log-----------------------
            applicationLogService.recordApplicationLog(principal.getName(), "" + "Type of Recording added", "Post", 0);
            //-----------log-----------------------
        }
        return new ResponseEntity<>(recordingList, HttpStatus.CREATED);
    }

    @GetMapping("vivo/recordings")
    public ResponseEntity<List<Recording>> getAllrecordings() {
        List<Recording> recordings = recordingService.getAll();
        List<Recording> recordingList = new ArrayList<>();
        for (Recording recording : recordings) {
            if (recording.isStatus()) {
                recordingList.add(recording);
            }
        }
        return new ResponseEntity<>(recordingList, HttpStatus.OK);
    }

    @GetMapping("vivo/recording/{id}")
    public ResponseEntity<Recording> getRecording(@PathVariable("id") long id) {
        Recording recording = recordingService.get(id);
        return new ResponseEntity<>(recording, HttpStatus.OK);
    }

    @GetMapping("vivo/recording-delete/{id}")
    public ResponseEntity<Recording> deleteRecording(@PathVariable("id") long id) {
        Recording recording = recordingService.get(id);
        recording.setStatus(false);
        recordingService.save(recording);
        return new ResponseEntity<>(recording, HttpStatus.OK);
    }


    @PostMapping("vivo/Time-config")
    public ResponseEntity<TimeConfig> save(@RequestBody TimeConfig timeConfig, Principal principal) {
        timeConfig.setStatus(true);
        TimeConfig timeConfig1 = timeConfigService.save(timeConfig);
        //-----------log-----------------------
        applicationLogService.recordApplicationLog(principal.getName(), "Vivo/Added time configuration", "Post", 0);
        //-----------log-----------------------
        return new ResponseEntity<>(timeConfig1, HttpStatus.OK);
    }


    @GetMapping("vivo/time-configs")
    public ResponseEntity<List<TimeConfig>> getAllTimeConfig() {
        List<TimeConfig> timeConfigList = timeConfigService.getAll();
        return new ResponseEntity<>(timeConfigList, HttpStatus.OK);
    }

    @GetMapping("vivo/time-config/{id}")
    public ResponseEntity<TimeConfig> get(long id) {
        TimeConfig timeConfig = timeConfigService.get(id);
        return new ResponseEntity<>(timeConfig, HttpStatus.OK);
    }

    @PostMapping("vivo/company")
    public ResponseEntity<List<Company>> saveCompany(@RequestBody List<Company> companyList, Principal principal) {
        for (Company company : companyList) {
            company.setStatus(true);
            company.setAvailable(true);
            Company company1 = companyService.save(company);
            //-----------log-----------------------
            applicationLogService.recordApplicationLog(principal.getName(), "Vivo/Company created", "Post", 0);
            //-----------log-----------------------
        }
        if (companyList.isEmpty()) {
            throw new EntityNotFoundException("No Companies");
        }
        return new ResponseEntity<>(companyList, HttpStatus.CREATED);
    }

    @GetMapping("vivo/approved-company")
    public ResponseEntity<List<Company>> getAll() {
        List<Company> company1 = companyService.getAll();
        List<Company> companies = new ArrayList<>();
        for (Company company : company1) {
            if (company.isAvailable()) {
                companies.add(company);
            }
        }
        if (companies.isEmpty()) {
            throw new EntityNotFoundException("No Approved Companies");
        }

        return new ResponseEntity<>(companies, HttpStatus.CREATED);
    }

    @GetMapping("company-delete/{id}")
    public void deleteCompany(@PathVariable("id") long id) {
        Company company = companyService.get(id);
        company.setAvailable(false);
        companyService.save(company);
    }

    //..............vivo user part..............

    @PostMapping("vivo/vivo-info")
    public ResponseEntity<VivoInfo> saveinfo(@RequestBody VivoInfoDto vivoInfoDto) throws Exception {

        Optional<TypeOfVehicle> typeOfVehicle = typeOfVehicleService.getVehicleById(vivoInfoDto.getTypeOfVehicleId());
        VivoInfo vivoInfo = new VivoInfo();
        vivoInfo.setPurpose(vivoInfoDto.getPurpose());
        vivoInfo.setNoOfPersons((vivoInfoDto.getNoOfPersons()));
        vivoInfo.setEntryTime(vivoInfoDto.getEntryTime());
        vivoInfo.setVehicleNumber(vivoInfoDto.getVehicleNumber());
        vivoInfo.setVehicleType(typeOfVehicle.get());
        vivoInfo.setDriverDetails(vivoInfoDto.getDriverDetails());
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("HH.mm");
        String s = sdf.format(date);
        vivoInfo.setEntryTime(s);
        vivoInfo.setDriverEntryTime(s);
        vivoInfo.setExtraTime(String.valueOf(0));
        vivoInfo.setDriverExtraTime(String.valueOf(0));
        vivoInfo.setWorkedHours(String.valueOf(0));
        vivoInfo.setStayTime(vivoInfoDto.getStayTime());
        vivoInfo.setRoute(vivoInfoDto.getRoute());
        vivoInfo.setCompanyName(vivoInfoDto.getCompanyName());
        vivoInfo.setCompanyType(vivoInfoDto.getCompanyType());
        vivoInfo.setLoggedOn(date);
        vivoInfo.setBayNumber(vivoInfoDto.getBayNumber());
        vivoInfo.setSlotNumber(vivoInfoDto.getSlotNumber());
        vivoInfo.setActive(true);
        VivoInfo vivoInfo1 = vivoInfoService.save(vivoInfo);
        return new ResponseEntity<>(vivoInfo, HttpStatus.CREATED);
    }

    @GetMapping("vivo/vivo-driver-checkIn/{vehicleNumber}")
    public ResponseEntity<VivoInfo> vehicleCheckOut(@PathVariable String vehicleNumber) throws Exception {
        VivoInfo vivoInfoToday = vivoInfoService.getByVehicleNumberAndLoggedOn(new Date(), vehicleNumber);
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("HH.mm");
        String s = sdf.format(date);
        if (vivoInfoToday != null) {
            vivoInfoToday.setDriverEntryTime(s);
        } else {
            throw new EntityNotFoundException("CheckIn vehicle first");
        }
        return new ResponseEntity<>(vivoInfoToday, HttpStatus.CREATED);
    }

    @PostMapping("vivo/vivo-checkIn/{vehicleNumber}")
    public ResponseEntity<VivoInfo> vehicleCheckIn(@PathVariable String vehicleNumber, @RequestBody VivoDto vivoDto) throws Exception {
        VivoInfo vivoInfoLatest = new VivoInfo();
        List<VivoInfo> vivoInfoList = vivoInfoService.getByVehicleNumber(vehicleNumber);
        Collections.reverse(vivoInfoList);
        if (!vivoInfoList.isEmpty()) {
            vivoInfoLatest = vivoInfoList.get(0);
            if (vivoInfoLatest.getCheckedOut() != null) {
                vivoInfoLatest = new VivoInfo();
            } else if (vivoInfoLatest.getCheckedOut() == null) {
                vivoInfoLatest = vivoInfoLatest;
            }
        }

        VivoInfo vivoInfoLat = vivoInfoLatest;
        if (vivoInfoLat.getCheckedIn() != null) {
            throw new EntityNotFoundException("Already checked-In before");
        }

        VivoInfo vivoInfo = vivoInfoLatest;
        VivoInfo vivoInfoToday = vivoInfoService.getByVehicleNumberAndLoggedOn(new Date(), vehicleNumber);
        if (vivoInfoToday != null) {
            throw new EntityNotFoundException("Already checkedIn");
        }
//        VivoInfo vivoInfo = new VivoInfo();

        VivoPass vivoInfoDto = vivoPassRepo.findByVehicleNumber(vehicleNumber);
        Date d = new Date();
        if (d.after(vivoInfoDto.getEndDate()) || d.before(vivoInfoDto.getStartDate())) {
            throw new EntityNotFoundException("No valid pass for today");
        }
        Optional<TypeOfVehicle> typeOfVehicle = typeOfVehicleService.getVehicleById(vivoInfoDto.getVehicleType().getId());
        vivoInfo.setPurpose(vivoInfoDto.getPurpose());
        vivoInfo.setNoOfPersons((vivoInfoDto.getNoOfPersons()));
        vivoInfo.setVehicleNumber(vivoInfoDto.getVehicleNumber());
        vivoInfo.setVehicleType(typeOfVehicle.get());
        vivoInfo.setDriverDetails(vivoInfoDto.getDriverDetails());
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("HH.mm");
        String s = sdf.format(date);
        vivoInfo.setEntryTime(s);
        vivoInfo.setDriverEntryTime(s);
        vivoInfo.setLoggedOn(date);
        vivoInfo.setCheckedIn(date);
        vivoInfo.setExtraTime(String.valueOf(0));
        vivoInfo.setDriverExtraTime(String.valueOf(0));
        vivoInfo.setWorkedHours(String.valueOf(0));
        vivoInfo.setStayTime(vivoInfoDto.getStayTime());
        vivoInfo.setRoute(vivoInfoDto.getRoute());
        vivoInfo.setCompanyName(vivoInfoDto.getCompanyName());
        vivoInfo.setCompanyType(vivoInfoDto.getCompanyType());
        vivoInfo.setGrossWeight(vivoDto.getGrossWeight());
        vivoInfo.setTareWeight(vivoDto.getTareWeight());
        vivoInfo.setDescription(vivoDto.getDescription());
        vivoInfo.setLoggedOn(date);
        VivoInfo vivoInfoBaySlot = null;
        if (vivoDto.getBayNum() != null && vivoDto.getSlotNum() != null) {
            vivoInfoBaySlot = vivoInfoService.getByAllocatedInfoByBayAndSlot(vivoDto.getBayNum(), vivoDto.getSlotNum());
        }
        if (vivoInfoBaySlot != null) {
            throw new Exception("Slot already allocated choose another slot");
        }
        vivoInfo.setBayNumber(vivoDto.getBayNum());
        vivoInfo.setSlotNumber(vivoDto.getSlotNum());
        vivoInfo.setActive(true);
        vivoInfo.setVivoPass(vivoInfoDto);
        //----GEBE---------
        vivoInfo.setEmpId(vivoInfoDto.getEmpId());
        vivoInfo.setEmpname(vivoInfoDto.getEmpname());
        vivoInfo.setCardId(vivoInfoDto.getCardId());
        vivoInfo.setEmployeeType(vivoInfoDto.getEmployeeType());
        vivoInfo.setEmppin(vivoInfoDto.getEmppin());
        vivoInfo.setSitecode(vivoInfoDto.getSitecode());
        vivoInfo.setCheckedIn(date);
        //-------GEBE---------
        VivoInfo vivoInfo1 = vivoInfoService.save(vivoInfo);
        return new ResponseEntity<>(vivoInfo, HttpStatus.CREATED);
    }

    /**
     * Register vehicle and pass
     * po
     *
     * @param vivoInfoDto
     * @return
     * @throws Exception
     */
    @PostMapping("vivo/vivo-pass")
    public ResponseEntity<VivoPass> savePass(@RequestBody VivoInfoDto vivoInfoDto) throws Exception {
        VivoPass vivoInfo = null;
        VivoPass vivoPassFound = vivoPassRepo.findByCardId(vivoInfoDto.getCardId());
        Optional<TypeOfVehicle> typeOfVehicle = typeOfVehicleService.getVehicleById(vivoInfoDto.getTypeOfVehicleId());
        if (vivoInfoDto.getId() == 0) {
            if (vivoPassFound != null) {
                throw new EntityExistsException("card id already registered");
            }
            vivoInfo = new VivoPass();
            vivoInfo.setPurpose(vivoInfoDto.getPurpose());
            vivoInfo.setNoOfPersons((vivoInfoDto.getNoOfPersons()));
            vivoInfo.setVehicleNumber(vivoInfoDto.getVehicleNumber());
            vivoInfo.setVehicleType(typeOfVehicle.get());
            vivoInfo.setDriverDetails(vivoInfoDto.getDriverDetails());
            vivoInfo.setCardId(vivoInfoDto.getCardId());
            Date date = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("HH.mm");
            String s = sdf.format(date);
            vivoInfo.setRoute(vivoInfoDto.getRoute());
            vivoInfo.setCompanyName(vivoInfoDto.getCompanyName());
            vivoInfo.setCompanyType(vivoInfoDto.getCompanyType());
            vivoInfo.setLoggedOn(date);
            vivoInfo.setStayTime(vivoInfoDto.getStayTime());
            vivoInfo.setStartDate(vivoInfoDto.getStartDate());
            vivoInfo.setEndDate(vivoInfoDto.getEndDate());
            vivoInfo.setBayNumber(vivoInfoDto.getBayNumber());
            vivoInfo.setSlotNumber(vivoInfoDto.getSlotNumber());
            vivoInfo.setActive(true);
            vivoInfo.setImgId(vivoInfoDto.getImgId());
            vivoInfo.setDocId(vivoInfoDto.getDocId());
            vivoInfo.setRfid(vivoInfoDto.getRfid());
            vivoInfo.setDlNumber(vivoInfoDto.getDlNumber());
            vivoInfo.setDlValidity(vivoInfoDto.getDlValidity());
            //----GEBE---------
            vivoInfo.setEmpId(vivoInfoDto.getEmpId());
            vivoInfo.setEmpname(vivoInfoDto.getEmpname());
            vivoInfo.setCardId(vivoInfoDto.getCardId());
            vivoInfo.setEmployeeType(vivoInfoDto.getEmployeeType());
            vivoInfo.setEmppin(vivoInfoDto.getEmppin());
            vivoInfo.setSitecode(vivoInfoDto.getSitecode());
            //-------GEBE---------
            vivoInfo.setAllowOrDeny(true);
            VivoPass vivoPass = vivoPassRepo.save(vivoInfo);
        } else if (vivoInfoDto.getId() > 0) {
            if (vivoPassFound != null && vivoInfoDto.getId() != vivoPassFound.getId()) {
                throw new EntityExistsException("card id already registered");
            }
            vivoInfo = vivoPassRepo.findOne(vivoInfoDto.getId());
            vivoInfo.setPurpose(vivoInfoDto.getPurpose());
            vivoInfo.setNoOfPersons((vivoInfoDto.getNoOfPersons()));
            vivoInfo.setVehicleNumber(vivoInfoDto.getVehicleNumber());
            vivoInfo.setVehicleType(typeOfVehicle.get());
            vivoInfo.setDriverDetails(vivoInfoDto.getDriverDetails());
            Date date = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("HH.mm");
            String s = sdf.format(date);
            vivoInfo.setRoute(vivoInfoDto.getRoute());
            vivoInfo.setCompanyName(vivoInfoDto.getCompanyName());
            vivoInfo.setCompanyType(vivoInfoDto.getCompanyType());
            vivoInfo.setLoggedOn(date);
            vivoInfo.setStartDate(vivoInfoDto.getStartDate());
            vivoInfo.setEndDate(vivoInfoDto.getEndDate());
            vivoInfo.setBayNumber(vivoInfoDto.getBayNumber());
            vivoInfo.setSlotNumber(vivoInfoDto.getSlotNumber());
            vivoInfo.setActive(true);
            vivoInfo.setAllowOrDeny(true);
            vivoInfo.setImgId(vivoInfoDto.getImgId());
            vivoInfo.setDocId(vivoInfoDto.getDocId());
            vivoInfo.setRfid(vivoInfoDto.getRfid());
            vivoInfo.setDlNumber(vivoInfoDto.getDlNumber());
            vivoInfo.setDlValidity(vivoInfoDto.getDlValidity());
            //----GEBE---------
            vivoInfo.setEmpId(vivoInfoDto.getEmpId());
            vivoInfo.setEmpname(vivoInfoDto.getEmpname());
            vivoInfo.setCardId(vivoInfoDto.getCardId());
            vivoInfo.setEmployeeType(vivoInfoDto.getEmployeeType());
            vivoInfo.setEmppin(vivoInfoDto.getEmppin());
            vivoInfo.setSitecode(vivoInfoDto.getSitecode());
            //-------GEBE---------
            VivoPass vivoPass = vivoPassRepo.save(vivoInfo);
        }
        return new ResponseEntity<>(vivoInfo, HttpStatus.CREATED);
    }

    /**
     * Allow or deny registered for inplant logistics vehicle
     *
     * @param id
     * @return
     */
    @PostMapping("vivo-denail/{id}")
    public ResponseEntity<VivoPass> allowOrDenyCompany(@PathVariable long id) {
        VivoPass vivoPass = vivoPassRepo.findOne(id);
        if (vivoPass.isAllowOrDeny()) {
            vivoPass.setAllowOrDeny(false);
            vivoPassRepo.save(vivoPass);
        } else if (vivoPass.isAllowOrDeny() == false) {
            vivoPass.setAllowOrDeny(true);
            vivoPassRepo.save(vivoPass);
        }
        return new ResponseEntity<>(vivoPass, HttpStatus.OK);
    }

    @GetMapping("vivo/vivo-infos")
    public ResponseEntity<List<VivoInfo>> getAllVivoInfo() {
        List<VivoInfo> vivoInfos = vivoInfoService.getAll();
        List<VivoInfo> vivoInfoTodaysList = new ArrayList<>();
        if (vivoInfos.isEmpty()) {
            throw new EntityNotFoundException("No info for Today");
        }
        for (VivoInfo v : vivoInfos) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String markedOn = sdf.format(v.getMarkedOn());
            String today = sdf.format(new Date());
            if (markedOn.equals(today)) {
                vivoInfoTodaysList.add(v);
            }
        }
        return new ResponseEntity<>(vivoInfoTodaysList, HttpStatus.OK);
    }

    @GetMapping("vivo/vivo-info/{id}")
    public ResponseEntity<VivoInfo> getVivoInfo(@PathVariable("id") long id) {
        VivoInfo vivoInfo = vivoInfoService.get(id);
        return new ResponseEntity<>(vivoInfo, HttpStatus.OK);
    }

    @GetMapping("vivo-info-pulse")
    public ResponseEntity<Boolean> sendPulse() {
        return new ResponseEntity(true, HttpStatus.OK);
    }

    @GetMapping("vivo/vivo-last-info")
    public ResponseEntity<VivoInfo> getVivoLastInfo() {
        List<VivoInfo> vivoInfo = vivoInfoService.getUpdatesAscendingVehicle();
        Collections.reverse(vivoInfo);

        VivoInfo v = vivoInfo.get(0);
//        if (vivoInfo.isEmpty()) {
//            return new ResponseEntity<>(v, HttpStatus.OK);
//        }
//        Collections.reverse(vivoInfo);
//        VivoInfo vivoInfo1 = vivoInfo.get(0);
//        if (vivoInfo1.getShoww() <= 500) {
//            vivoInfo1.setShoww(vivoInfo1.getShoww() + 1);
//            vivoInfoService.save(vivoInfo1);
//            return new ResponseEntity<>(vivoInfo1, HttpStatus.OK);
//        }

        return new ResponseEntity<>(v, HttpStatus.OK);
    }

    //payment rules, calculatePaymentIfRulesApplicable

    @PostMapping("vivo/check-payment/{vehicleNumber}")
    public VivoInfo checkPaymentRules(@PathVariable String vehicleNumber, @RequestBody VivoDto vivoDto) throws ParseException {
        VivoInfo vivoInfoLatest = null;
        List<VivoInfo> vivoInfoList = vivoInfoService.getByVehicleNumber(vehicleNumber);
        Collections.reverse(vivoInfoList);
        if (!vivoInfoList.isEmpty()) {
            vivoInfoLatest = vivoInfoList.get(0);
            if (vivoInfoLatest.getCheckedOut() != null) {
                vivoInfoLatest = null;
            } else if (vivoInfoLatest.getCheckedOut() == null) {
                vivoInfoLatest = vivoInfoLatest;
            }
        }

        VivoInfo vivoInfoLat = vivoInfoLatest;
        VivoInfo vivoInfo = vivoInfoLatest;

//        VivoInfo vivoInfo = vivoInfoService.getByVehicleNumberAndLoggedOn(new Date(), vehicleNumber);
        if (vivoInfo == null) {
            throw new EntityNotFoundException("CheckIn first");
        }
        if (vivoInfo.getExitTime() != null) {
            throw new EntityNotFoundException("Vehicle Already Checked Out");
        }
        DateFormat timeFormat = new SimpleDateFormat("HH.mm");
        String s = timeFormat.format(vivoInfo.getCheckedIn().getTime());
        vivoInfo.setEntryTime(s);
        vivoInfo.setDriverEntryTime(s);
        VivoInfo v = calculateTotalStayTime(vivoInfo);
        calculateDriverTotalStayTime(vivoInfo);
        v.setGrossWeight(vivoDto.getGrossWeight());
        v.setTareWeight(vivoDto.getTareWeight());
        v.setDescription(vivoDto.getDescription());
        v.setCheckedOut(new Date());
        v.setMarkedOn(new Date());
        vivoInfoService.save(v);
        return v;
    }

    @GetMapping("vivo/check-driver-payment/{vehicleNumber}")
    public VivoInfo checkDriverPaymentRules(@PathVariable String vehicleNumber) throws ParseException {
        VivoInfo vivoInfo = vivoInfoService.getByVehicleNumberAndLoggedOn(new Date(), vehicleNumber);
//        List<VivoInfo> vivoInfoLatest = vivoInfoService.getByVehicleNumber(vehicleNumber);
//        VivoInfo vivoInfo = vivoInfoLatest.get(0);
        if (vivoInfo == null) {
            throw new EntityNotFoundException("CheckIn first");
        }
        if (vivoInfo.getDriverExitTime() != null) {
            throw new EntityNotFoundException("Already Checked Out");
        }
        VivoInfo v = calculateDriverTotalStayTime(vivoInfo);
        v.setMarkedOn(new Date());
        vivoInfoService.save(v);
        return v;
    }

    private VivoInfo calculateDriverTotalStayTime(VivoInfo vehicleNumber) throws ParseException {
        VivoInfo vivoInfo = vehicleNumber;
        vivoInfo.setCheckedOut(new Date());
        CompanyConfigure paymentRule = companyConfigureService.getCompanyById(1);
        DateFormat timeFormat = new SimpleDateFormat("HH.mm");
        String inTime = (vivoInfo.getDriverEntryTime());
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("HH.mm");
        String outTime = sdf.format(date);
        long vehicleInTime = timeFormat.parse(inTime).getTime();
        long vehicleOutTime = timeFormat.parse(outTime).getTime();
        vivoInfo.setDriverExitTime(String.valueOf(outTime));
        double workedMinutes = 0;
        double workedMillis;
        workedMillis = vehicleOutTime - vehicleInTime;
        workedMinutes = TimeUnit.MILLISECONDS.toMinutes((long) workedMillis);
        double workedHrs = workedMinutes / 60;
        vivoInfo.setWorkedHours(String.valueOf(workedHrs));
        long secs = (vivoInfo.getCheckedIn().getTime() - vivoInfo.getCheckedOut().getTime()) / 1000;
        long hours = secs / 3600;
        long h = Math.abs(hours);
        String s = getDiff(vivoInfo.getCheckedIn(), vivoInfo.getCheckedOut());
        vivoInfo.setWorkedHours(s);
        if (workedHrs > vivoInfo.getStayTime()) {
            double extraHour = workedHrs - vivoInfo.getStayTime();
            double overTimeHrs = (extraHour + 150) / 60;
            System.out.println("ExtraTime: " + extraHour);
            vivoInfo.setDriverExtraTime(String.valueOf(extraHour));
            double payableAmount = 0;
            long rate;
            if (vivoInfo.getVehicleType() == null) {
                payableAmount = 0;
            } else {
                rate = vivoInfo.getVehicleType().getRate();
                payableAmount = extraHour * rate;
            }
            if (paymentRule.isVivoPayment()) {
                vivoInfo.setDriverPayableAmount(payableAmount);
            }
        } else {
            vivoInfo.setDriverExtraTime(String.valueOf(0));
            vivoInfo.setDriverPayableAmount(0);
        }
        return vivoInfo;
    }

//    @GetMapping("vivo/vivo-pass")
//    public ResponseEntity<List<VivoPass>> getRegisteredVivoWithPass() {
//        List<VivoPass> vivoPasses = vivoPassRepo.findAll();
////        getEmployees();
//        return new ResponseEntity<>(vivoPasses, HttpStatus.OK);
//    }

    @GetMapping("vivo/vivo-pass")
    public ResponseEntity<List<VivoPass>> getRegisteredVivoWithPass() {
        List<VivoPass> vivoPassList= vivoPassRepo.findAll();
        List<VivoPass> vivoPasses=new ArrayList<>();
        for (VivoPass  v:vivoPassList) {
            if(!v.isTerminate()){
                vivoPasses.add(v);
            }
        }
//        getEmployees();
        return new ResponseEntity<>(vivoPasses , HttpStatus.OK);
    }

    @GetMapping("vivo/vivo-pass-terminate")
    public ResponseEntity<List<VivoPass>> getTerminate() {
        List<VivoPass> vivoPasses = vivoPassRepo.findAll();
        List<VivoPass> vivoPassList=new ArrayList<>();
        for (VivoPass v:vivoPasses) {
            if(v.isTerminate()){
                vivoPassList.add(v);
            }
        }
//        getEmployees();
        return new ResponseEntity<>(vivoPassList , HttpStatus.OK);
    }

    @GetMapping("vivo/vivo-terminate/{terId}")
    public ResponseEntity<VivoPass> saveVivoTerminate(@PathVariable("terId") long terId){
        VivoPass vivoPass=vivoPassRepo.findOne(terId);
        vivoPass.setTerminate(true);
        vivoPassRepo.save(vivoPass);
        return new ResponseEntity<>(vivoPass,HttpStatus.OK);
    }
    @PostMapping("vivo/checkIn-out-list")
    public ResponseEntity<List<VivoInfo>> getCheckInEmployees(@RequestBody VivoDateDto vivoDto) {
        Date date = vivoDto.startDate;
        List<VivoInfo> vivoInfoList = vivoInfoService.getByMarkedOn(date);
        return new ResponseEntity<>(vivoInfoList, HttpStatus.OK);

    }

    @Autowired
    EvacuationTrackerRepository evacuationTrackerRepository;

    @GetMapping("vivo/checkIn-out-list")
    public ResponseEntity<List<VivoInfo>> getCheckInEmployees() {
        Date date = new Date();
        List<VivoInfo> vivoInfoList = vivoInfoService.getByMarkedOn(date);
        List<EvacuationTracker> evacuationTrackers = evacuationTrackerRepository.findAll();
        if (!evacuationTrackers.isEmpty()) {
            Collections.reverse(evacuationTrackers);
            EvacuationTracker evacuationTracker = evacuationTrackers.get(0);
            evacuationTracker.setVehicleCount(vivoInfoList.size());
            evacuationTrackerRepository.save(evacuationTracker);
        }
        return new ResponseEntity<>(vivoInfoList, HttpStatus.OK);

    }

    private VivoInfo calculateTotalStayTime(VivoInfo id) throws ParseException {
        VivoInfo vivoInfo = id;
        CompanyConfigure paymentRule = companyConfigureService.getCompanyById(1);
        DateFormat timeFormat = new SimpleDateFormat("HH.mm");
        String inTime = (vivoInfo.getEntryTime());
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("HH.mm");
        String outTime = sdf.format(date);
        long vehicleInTime = timeFormat.parse(inTime).getTime();
        long vehicleOutTime = timeFormat.parse(outTime).getTime();
        vivoInfo.setExitTime(String.valueOf(outTime));
        double workedMinutes = 0;
        double workedMillis;
        workedMillis = vehicleOutTime - vehicleInTime;
        workedMinutes = TimeUnit.MILLISECONDS.toMinutes((long) workedMillis);
        double workedHrs = workedMinutes / 60;
        String s = getDiff(vivoInfo.getCheckedIn(), new Date());
        vivoInfo.setWorkedHours(s);
        if (workedHrs > vivoInfo.getStayTime()) {
            double extraHour = workedHrs - vivoInfo.getStayTime();
            double overTimeHrs = (extraHour + 150) / 60;
            System.out.println("ExtraTime: " + extraHour);
            vivoInfo.setExtraTime(String.valueOf(extraHour));
            double payableAmount = 0;
            long rate;
            if (vivoInfo.getVehicleType() == null) {
                payableAmount = 0;
            } else {
                rate = vivoInfo.getVehicleType().getRate();
                payableAmount = extraHour * rate;
            }
            if (paymentRule.isVivoPayment()) {
                vivoInfo.setPayableAmount(payableAmount);
            } else {
                vivoInfo.setExtraTime(String.valueOf(0));
            }
        } else {
            vivoInfo.setExtraTime(String.valueOf(0));
            vivoInfo.setPayableAmount(0);
        }
        vivoInfo.setBayArchiveNumber(vivoInfo.getBayNumber());
        vivoInfo.setSlotArchiveNumber(vivoInfo.getSlotNumber());
        vivoInfo.setBayNumber(null);
        vivoInfo.setSlotNumber(null);
        return vivoInfo;
    }

    public String getDiff(Date checkIn, Date chekOut) {
        long diffInMilliSec = new Date().getTime() - checkIn.getTime();

        long seconds = (diffInMilliSec / 1000) % 60;

        long minutes = (diffInMilliSec / (1000 * 60)) % 60;

        long hours = (diffInMilliSec / (1000 * 60 * 60)) % 24;

        long days = (diffInMilliSec / (1000 * 60 * 60 * 24)) % 365;

        long years = (diffInMilliSec / (1000l * 60 * 60 * 24 * 365));

        System.out.println("Difference is ---> ");

        String diff = "" + hours + ":" + minutes;

        return diff;

    }

    /**
     * approved rules
     *
     * @param available
     * @return
     */
    @PostMapping("vivo/toggle-company/{status}")
    public ResponseEntity<Company> setApprovedCompanyOrOthers(@PathVariable("status") boolean available) {
        List<Company> companies = companyService.getAll();
        for (Company c : companies) {
            c.setAvailable(available);
            companyService.save(c);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("vivo/approved-company/status")
    public ResponseEntity<Boolean> getApprovedCompanyOrOthers() {
        List<Company> companies = companyService.getAll();
        boolean s = companies.get(0).isAvailable();
        return new ResponseEntity<>(s, HttpStatus.OK);
    }

    /**
     * payment rules
     */

    @PostMapping("vivo/toggle-payment/{status}")
    public ResponseEntity<PaymentRules> togglePayment(@PathVariable("status") boolean available) {
        List<PaymentRules> paymentRules = paymentRulesService.getAll();
        PaymentRules paymentRule = paymentRules.get(0);
        paymentRule.setApplicable(available);
        paymentRule.setMarkedOn(new Date());
        paymentRulesService.save(paymentRule);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("vivo/toggle-currency/{currency}")
    public ResponseEntity<PaymentRules> setcurrencyPayment(@PathVariable("currency") String currency) {
        List<PaymentRules> paymentRules = paymentRulesService.getAll();
        PaymentRules paymentRule = paymentRules.get(0);
        paymentRule.setCurrency(currency);
        paymentRule.setMarkedOn(new Date());
        paymentRulesService.save(paymentRule);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("vivo/payment-rules")
    public ResponseEntity<PaymentRules> getPaymentRules() {
        List<PaymentRules> paymentRules = paymentRulesService.getAll();
        PaymentRules paymentRule = paymentRules.get(0);
        return new ResponseEntity<>(paymentRule, HttpStatus.OK);
    }


    /**
     * for security screen assuming data from camera
     */

    @GetMapping("vivo/count-in")
    public ResponseEntity<Long> countOfVehiclesInSide() {
        List<VivoInfo> vivoInfos = getTodaysVivoInfo();
        List<VivoInfo> vivoInfoList = new ArrayList<>();
        for (VivoInfo v : vivoInfos) {
            if (v.getEntryTime() != null && v.getExitTime() == null) {
                vivoInfoList.add(v);
            }
        }
        long count = vivoInfoList.size();
        return new ResponseEntity<>(count, HttpStatus.OK);
    }

    @GetMapping("vivo/count-total")
    public ResponseEntity<Long> countOfVehiclesAllToday() {
        List<VivoInfo> vivoInfos = getTodaysVivoInfo();
        List<VivoInfo> vivoInfoList = new ArrayList<>();
        for (VivoInfo v : vivoInfos) {
            if (v.getEntryTime() != null && v.getExitTime() != null) {
                vivoInfoList.add(v);
            }
        }
        long count = vivoInfos.size();
        return new ResponseEntity<>(count, HttpStatus.OK);
    }


    public List<VivoInfo> getTodaysVivoInfo() {
        List<VivoInfo> vivoInfos = vivoInfoService.getAll();
        List<VivoInfo> vivoInfoTodaysList = new ArrayList<>();
        if (vivoInfos.isEmpty()) {
            throw new EntityNotFoundException("No info for Today");
        }
        for (VivoInfo v : vivoInfos) {

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String markedOn = sdf.format(v.getMarkedOn());
            String today = sdf.format(new Date());
            if (markedOn.equals(today)) {
                vivoInfoTodaysList.add(v);
            }
        }
        return vivoInfoTodaysList;
    }

    @GetMapping("vivo/dashboard/vehicle-type-count")
    public ResponseEntity<VivoDto> countOfVehicleTypeWise() {
        long twoWheeler = 0;
        long threeWheeler = 0;
        long fourWheeler = 0;
        long others = 0;
        VivoDto vdto = new VivoDto();
        List<VivoInfo> vivoInfoList = getTodaysVivoInfo();
        for (VivoInfo v : vivoInfoList) {
            if (v.getVehicleType() != null) {
                if (v.getVehicleType().getTypeOfVehicle().equals("TWO_WHEELER")) {
                    twoWheeler++;
                } else if (v.getVehicleType().getTypeOfVehicle().equals("THREE_WHEELER")) {
                    threeWheeler++;
                } else if (v.getVehicleType().getTypeOfVehicle().equals("FOUR_WHEELER")) {
                    fourWheeler++;
                } else {
                    others++;
                }
            } else {
                others++;
            }
        }
        vdto.setTwo_wheeler(twoWheeler);
        vdto.setThree_wheeler(threeWheeler);
        vdto.setFour_wheeler(fourWheeler);
        vdto.setOthers(others);
        return new ResponseEntity<>(vdto, HttpStatus.OK);
    }

    @GetMapping("vivo/dashboard/vehicle-withinTime-count")
    public ResponseEntity<VivoDto> countOfWitinTimeVehicleTypeWise() {
        long twoWheeler = 0;
        long threeWheeler = 0;
        long fourWheeler = 0;
        long others = 0;
        VivoDto vdto = new VivoDto();
        List<VivoInfo> vivoInfoList = getTodaysVivoInfo();
        for (VivoInfo v : vivoInfoList) {
            if (v.getVehicleType().getTypeOfVehicle().equals("TWO_WHEELER") && v.getExtraTime().equalsIgnoreCase("0") && (v.getExitTime() != null)) {
                twoWheeler++;
            } else if (v.getVehicleType().getTypeOfVehicle().equals("THREE_WHEELER") && v.getExtraTime().equalsIgnoreCase("0") && (v.getExitTime() != null)) {
                threeWheeler++;
            } else if (v.getVehicleType().getTypeOfVehicle().equals("FOUR_WHEELER") && v.getExtraTime().equalsIgnoreCase("0") && (v.getExitTime() != null)) {
                fourWheeler++;
            } else if (v.getVehicleType().getTypeOfVehicle().equals("OTHERS") && v.getExtraTime().equalsIgnoreCase("0") && (v.getExitTime() != null)) {
                others++;
            }
        }
        vdto.setTwo_wheeler(twoWheeler);
        vdto.setThree_wheeler(threeWheeler);
        vdto.setFour_wheeler(fourWheeler);
        vdto.setOthers(others);
        return new ResponseEntity<>(vdto, HttpStatus.OK);
    }

    @GetMapping("vivo/dashboard/vehicle-withinTime-count/{bayId}")
    public ResponseEntity<VivoDto> countOfWitinTimeVehicleTypeWiseBay(@PathVariable long bayId) {
        long twoWheeler = 0;
        long threeWheeler = 0;
        long fourWheeler = 0;
        long others = 0;
        VivoDto vdto = new VivoDto();
        List<VivoInfo> vivoInfoList = getTodaysVivoInfo();
        for (VivoInfo v : vivoInfoList) {
            Bay bay = bayService.getBay(bayId);
            if (v.getBayArchiveNumber().equalsIgnoreCase(bay.getBayNumber())) {
                if (v.getVehicleType() != null) {
                    if (v.getVehicleType().getTypeOfVehicle().equals("TWO_WHEELER") && v.getExtraTime().equalsIgnoreCase("0") && (v.getExitTime() != null)) {
                        twoWheeler++;
                    } else if (v.getVehicleType().getTypeOfVehicle().equals("THREE_WHEELER") && v.getExtraTime().equalsIgnoreCase("0") && (v.getExitTime() != null)) {
                        threeWheeler++;
                    } else if (v.getVehicleType().getTypeOfVehicle().equals("FOUR_WHEELER") && v.getExtraTime().equalsIgnoreCase("0") && (v.getExitTime() != null)) {
                        fourWheeler++;
                    } else if (v.getVehicleType().getTypeOfVehicle().equals("OTHERS") && v.getExtraTime().equalsIgnoreCase("0") && (v.getExitTime() != null)) {
                        others++;
                    }
                } else {
                    others++;
                }
            }
        }
        vdto.setTwo_wheeler(twoWheeler);
        vdto.setThree_wheeler(threeWheeler);
        vdto.setFour_wheeler(fourWheeler);
        vdto.setOthers(others);
        return new ResponseEntity<>(vdto, HttpStatus.OK);
    }


    @GetMapping("vivo/dashboard/vehicle-extraTime-count")
    public ResponseEntity<VivoDto> countOfExtraVehicleTypeWise() {
        long twoWheeler = 0;
        long threeWheeler = 0;
        long fourWheeler = 0;
        long others = 0;
        VivoDto vdto = new VivoDto();
        List<VivoInfo> vivoInfoList = getTodaysVivoInfo();
        for (VivoInfo v : vivoInfoList) {
            if (v.getVehicleType() != null) {
                if (v.getVehicleType().getTypeOfVehicle().equals("TWO_WHEELER") && !v.getExtraTime().equalsIgnoreCase("0") && (v.getExitTime() != null)) {
                    twoWheeler++;
                } else if (v.getVehicleType().getTypeOfVehicle().equals("THREE_WHEELER") && !v.getExtraTime().equalsIgnoreCase("0") && (v.getExitTime() != null)) {
                    threeWheeler++;
                } else if (v.getVehicleType().getTypeOfVehicle().equals("FOUR_WHEELER") && !v.getExtraTime().equalsIgnoreCase("0") && (v.getExitTime() != null)) {
                    fourWheeler++;
                } else if (v.getVehicleType().getTypeOfVehicle().equals("OTHERS") && !v.getExtraTime().equalsIgnoreCase("0") && (v.getExitTime() != null)) {
                    others++;
                }
            } else {
                others++;
            }
        }
        vdto.setTwo_wheeler(twoWheeler);
        vdto.setThree_wheeler(threeWheeler);
        vdto.setFour_wheeler(fourWheeler);
        vdto.setOthers(others);
        return new ResponseEntity<>(vdto, HttpStatus.OK);
    }

    @GetMapping("vivo/dashboard/vehicle-extraTime-count/{bayId}")
    public ResponseEntity<VivoDto> countOfExtraVehicleTypeWiseBay(@PathVariable long bayId) {
        long twoWheeler = 0;
        long threeWheeler = 0;
        long fourWheeler = 0;
        long others = 0;
        VivoDto vdto = new VivoDto();
        List<VivoInfo> vivoInfoList = getTodaysVivoInfo();
        for (VivoInfo v : vivoInfoList) {
            Bay bay = bayService.getBay(bayId);
            if (v.getBayArchiveNumber().equalsIgnoreCase(bay.getBayNumber())) {
                if (v.getVehicleType() != null) {
                    if (v.getVehicleType().getTypeOfVehicle().equals("TWO_WHEELER") && !v.getExtraTime().equalsIgnoreCase("0") && (v.getExitTime() != null)) {
                        twoWheeler++;
                    } else if (v.getVehicleType().getTypeOfVehicle().equals("THREE_WHEELER") && !v.getExtraTime().equalsIgnoreCase("0") && (v.getExitTime() != null)) {
                        threeWheeler++;
                    } else if (v.getVehicleType().getTypeOfVehicle().equals("FOUR_WHEELER") && !v.getExtraTime().equalsIgnoreCase("0") && (v.getExitTime() != null)) {
                        fourWheeler++;
                    } else if (v.getVehicleType().getTypeOfVehicle().equals("OTHERS") && !v.getExtraTime().equalsIgnoreCase("0") && (v.getExitTime() != null)) {
                        others++;
                    }
                } else {
                    others++;
                }
            }

        }
        vdto.setTwo_wheeler(twoWheeler);
        vdto.setThree_wheeler(threeWheeler);
        vdto.setFour_wheeler(fourWheeler);
        vdto.setOthers(others);
        return new ResponseEntity<>(vdto, HttpStatus.OK);
    }

    //-------Bay------------
    @PostMapping("vivo/bay")
    public ResponseEntity<Bay> saveBay(@RequestBody Bay bay) {

        long id = 0;

        List<Bay> bays = bayService.getBays();
        Collections.reverse(bays);
        if (!bays.isEmpty()) {
            id = bays.get(0).getId();
        }
        if (bay.getId() > 0) {
            id = bay.getId();
        }

        List<Slot> slotList1 = new ArrayList<>();
        List<String> slotsNames = getSlotNumber(bay.getBayNumber(), bay.getNumberOfSlots());
        for (String s : slotsNames) {
            Slot slot = new Slot();
            slot.setSlotNumber(s + id);
            slot.setStatus(true);
            slotService.save(slot);
            slotList1.add(slot);
        }

        bay.setSlots(slotList1);
        bay.setStatus(true);
        Bay bay1 = bayService.saveBay(bay);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    public List<String> getSlotNumber(String slot, long number) {
        List<String> slotNumber = new ArrayList<>();
        for (int i = 1; i <= number; i++) {
            slotNumber.add("slot-" + i);
        }
        return slotNumber;
    }

    @GetMapping("vivo/bays")
    public ResponseEntity<List<Bay>> getAllBays() {
        List<Bay> bays = bayService.getBays();
        List<Bay> bayList = new ArrayList<>();
        for (Bay b : bays) {
            if (b.isStatus()) {
                bayList.add(b);
            }
        }
        return new ResponseEntity(bayList, HttpStatus.OK);

    }

    @GetMapping("vivo/bays-screen")
    public ResponseEntity<List<Bay>> getAllBaysScreen() {
        List<Bay> bayList = new ArrayList<>();
        List<TypeOfVehicle> typeOfVehicles = typeOfVehicleService.getAllVehicles();
        for (TypeOfVehicle typeOfVehicle : typeOfVehicles) {
            List<Bay> bays = bayService.getByType(typeOfVehicle.getTypeOfVehicle());
            bayList.add(bays.get(0));
        }
        return new ResponseEntity(bayList, HttpStatus.OK);

    }

    @GetMapping("vivo/bays/{typeOfVehicle}")
    public ResponseEntity<List<Bay>> getAllBays(@PathVariable String typeOfVehicle) {
        List<Bay> bays = bayService.getBays();
        List<Bay> bayList = new ArrayList<>();
        for (Bay b : bays) {
            if (b.isStatus() && b.getBayVehicleType().equalsIgnoreCase(typeOfVehicle)) {
                bayList.add(b);
            }
        }
        return new ResponseEntity(bayList, HttpStatus.OK);

    }

    @DeleteMapping("vivo/delete-bay/{id}")
    public ResponseEntity<Bay> getBays(@PathVariable long id) {
        Bay bay = bayService.getBay(id);
        bay.setStatus(false);
        bayService.saveBay(bay);
        return new ResponseEntity(bay, HttpStatus.OK);

    }


    @GetMapping("vivo-by-vehicleNumber/{vehicleNumber}")
    public ResponseEntity<VivoPass> getByVehicleNumber(@PathVariable String vehicleNumber) {
        VivoPass vivoPass = vivoPassRepo.findByVehicleNumber(vehicleNumber);
//        VivoInfo vivoInfo1 = vivoInfoService.getByVehicleNumberAndLoggedOn(new Date(), vehicleNumber);
        List<VivoInfo> vivoInfoLatest = vivoInfoService.getByVehicleNumber(vehicleNumber);
        VivoInfo vivoInfo1 = null;
        if (!vivoInfoLatest.isEmpty()) {
            Collections.reverse(vivoInfoLatest);
            vivoInfo1 = vivoInfoLatest.get(0);
            if (vivoInfo1.getCheckedOut() != null) {
                vivoInfo1 = vivoInfoService.getByVehicleNumberAndLoggedOn(new Date(), vehicleNumber);
            }
        }
        if (vivoInfo1 != null) {
            if (vivoInfo1.getEntryTime() != null) {
                vivoPass.setEntryTime(vivoInfo1.getEntryTime());
            }
            if (vivoInfo1.getExitTime() != null) {
                vivoPass.setExitTime(vivoInfo1.getExitTime());
            }
            vivoInfo1.setVivoPass(null);
            vivoPass.setVivoInfo(vivoInfo1);
        }
        return new ResponseEntity(vivoPass, HttpStatus.OK);
    }


    @GetMapping("vivo-bay-slot/{id}/{status}")
    public ResponseEntity<CompanyConfigure> syncParking(@PathVariable("id") long id, @PathVariable("status") boolean status) {
        CompanyConfigure configure = companyConfigureService.getCompanyById(id);
        configure.setBayExists(status);
        companyConfigureService.saveCompany(configure);
        return new ResponseEntity<>(configure, HttpStatus.OK);
    }

    @PostMapping("vivo-pass-duration/{id}/{status}")
    public ResponseEntity<CompanyConfigure> checkDailyOrDurationPass(@PathVariable("id") long id, @PathVariable("status") boolean status) {
        CompanyConfigure configure = companyConfigureService.getCompanyById(id);
        configure.setDurationWise(status);
        companyConfigureService.saveCompany(configure);
        return new ResponseEntity<>(configure, HttpStatus.OK);
    }


    @GetMapping("vivo/slots/{bayNumber}")
    public ResponseEntity<List<Slot>> getSlotsByBayNumber(@PathVariable long bayNumber) {
        Bay bay = bayService.getBay(bayNumber);
        List<Slot> slots = bay.getSlots();
        return new ResponseEntity<>(slots, HttpStatus.OK);
    }


    @GetMapping("vivo/slots-availablity/{bayId}")
    public ResponseEntity<List<Slot>> checkForSlotsAllocation(@PathVariable long bayId) {
        List<Slot> slotList = new ArrayList<>();
        Bay bay = bayService.getBay(bayId);
        List<Slot> slots = bay.getSlots();
        for (Slot slot : slots) {
            VivoInfo vivoInfo = vivoInfoService.getByAllocatedInfoByBayAndSlot(bay.getBayNumber(), slot.getSlotNumber());
            if (vivoInfo != null) {
                slot.setAvailable(false);
                slotList.add(slot);
            } else {
                slot.setAvailable(true);
                slotList.add(slot);
            }
        }
        return new ResponseEntity<>(slotList, HttpStatus.OK);
    }

    @DeleteMapping("vivo/vivo-pass/{id}")
    public void deletePass(@PathVariable long id) {
        vivoPassRepo.delete(id);
    }


    @GetMapping("vivo/slots-availablity-utilization")
    public ResponseEntity<List<VivoDto>> checkForSlotsAllocationCount() {
        List<VivoDto> vivoDtos = new ArrayList<>();
        List<TypeOfVehicle> typeOfVehicles = typeOfVehicleService.getAllVehicles();
        for (TypeOfVehicle typeOfVehicle : typeOfVehicles) {
            List<Bay> bays = bayService.getByType(typeOfVehicle.getTypeOfVehicle());
            VivoDto vivoDto = new VivoDto();
            List<Slot> slotList = new ArrayList<>();
            long util = 0;
            long avil = 0;
            for (Bay bayy : bays) {
                if (bayy.isStatus()) {
//                    VivoDto vivoDto = new VivoDto();
//                    List<Slot> slotList = new ArrayList<>();
                    Bay bay = bayService.getBayByType(bayy.getBayNumber(), bayy.getBayVehicleType());
                    List<Slot> slots = bay.getSlots();
                    for (Slot slot : slots) {
                        VivoInfo vivoInfo = vivoInfoService.getByAllocatedInfoByBayAndSlot(bayy.getBayNumber(), slot.getSlotNumber());
                        if (vivoInfo != null) {
                            slot.setAvailable(false);
                            slotList.add(slot);
                            util++;
                            vivoDto.setUtilisedSlots(util);
                        } else {
                            slot.setAvailable(true);
                            slotList.add(slot);
                            avil++;
                            vivoDto.setAvailableSlots(avil);
                        }
                    }
                    vivoDto.setBayNumber(bayy.getBayNumber());
                    vivoDto.setVehicleType(bayy.getBayVehicleType());
                }
            }
            vivoDto.setTotalSlots(slotList.size());
            vivoDtos.add(vivoDto);

        }
        return new ResponseEntity<>(vivoDtos, HttpStatus.OK);
    }


//    @GetMapping("vivo/slots-availablity-utilization")
//    public ResponseEntity<List<VivoDto>> checkForSlotsAllocationCount() {
//        List<VivoDto> vivoDtos = new ArrayList<>();
//
//        List<Bay> bays = bayService.getBays();
//        for (Bay bayy : bays) {
//            if (bayy.isStatus()) {
//                VivoDto vivoDto = new VivoDto();
//                long util = 0;
//                long avil = 0;
//                List<Slot> slotList = new ArrayList<>();
//                Bay bay = bayService.getBayByType(bayy.getBayNumber(),bayy.getBayVehicleType());
//                List<Slot> slots = bay.getSlots();
//                for (Slot slot : slots) {
//                    VivoInfo vivoInfo = vivoInfoService.getByAllocatedInfoByBayAndSlot(bayy.getBayNumber(), slot.getSlotNumber());
//                    if (vivoInfo != null) {
//                        slot.setAvailable(false);
//                        slotList.add(slot);
//                        util++;
//                        vivoDto.setUtilisedSlots(util);
//                    } else {
//                        slot.setAvailable(true);
//                        slotList.add(slot);
//                        avil++;
//                        vivoDto.setAvailableSlots(avil);
//                    }
//                }
//                vivoDto.setTotalSlots(slotList.size());
//                vivoDto.setBayNumber(bayy.getBayNumber());
//                vivoDto.setVehicleType(bayy.getBayVehicleType());
//                vivoDtos.add(vivoDto);
//            }
//        }
//        return new ResponseEntity<>(vivoDtos, HttpStatus.OK);
//    }

    /**
     * BULK UPLOAD
     *
     * @param file
     * @return
     * @throws Exception
     */
    @PostMapping("vehicles-bulk-upload")
    public ResponseEntity<List<VivoPass>> vehicleBulkUpload(@RequestParam("file") MultipartFile file) throws Exception {
        XSSFWorkbook wb = new XSSFWorkbook(file.getInputStream());
        XSSFSheet sheet = wb.getSheetAt(0);
        List<VivoPass> vivoPassList = new ArrayList<>();
        for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {
            VivoPass vivoPass = new VivoPass();
            XSSFRow row = sheet.getRow(i);
            vivoPass.setVehicleNumber(row.getCell(1).getStringCellValue());
            vivoPass.setTypeOfVehicle(row.getCell(2).getStringCellValue());
            vivoPass.setDriverDetails(row.getCell(4).getStringCellValue());
            vivoPass.setCompanyName(row.getCell(7).getStringCellValue());
            vivoPassRepo.save(vivoPass);
        }
        return new ResponseEntity<>(vivoPassList, HttpStatus.OK);
    }


    /**
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    @GetMapping("vehicle/template-download")
    private void createVehicleDetails(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        OutputStream out = null;
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment; filename= Vehicle.xlsx");
        WritableWorkbook workbook = jxl.Workbook.createWorkbook(response.getOutputStream());
        try {
            writeVehicleToSheet(workbook, response, 0);
            workbook.write();
            workbook.close();
        } catch (Exception e) {
            throw new ServletException("Exception in Excel download", e);
        } finally {
            if (out != null)
                out.close();
        }
    }


    private WritableWorkbook writeVehicleToSheet(WritableWorkbook workbook, HttpServletResponse response, int index) throws IOException, WriteException {
        WritableSheet s = workbook.createSheet("Data Input", index);
        s.getSettings().setPrintGridLines(false);

        WritableFont headerFont = new WritableFont(WritableFont.TIMES, 14);
        headerFont.setBoldStyle(WritableFont.BOLD);
        WritableCellFormat headerFormat = new WritableCellFormat(headerFont);
        headerFormat.setAlignment(CENTRE);
        headerFormat.setBackground(Colour.GRAY_25);

        s.setColumnView(0, 10);
        s.setColumnView(1, 20);
        s.setColumnView(2, 20);
        s.setColumnView(3, 20);
        s.setColumnView(4, 20);
        s.setColumnView(5, 20);
        s.setColumnView(6, 20);
        s.setColumnView(7, 20);
        s.setColumnView(8, 20);

        s.addCell(new Label(0, 0, "#", headerFormat));
        s.addCell(new Label(1, 0, "Vehicle Number", headerFormat));
        s.addCell(new Label(2, 0, "Vehicle Type", headerFormat));
        s.addCell(new Label(3, 0, "DL Number", headerFormat));
        s.addCell(new Label(4, 0, "Name Of Driver", headerFormat));
        s.addCell(new Label(5, 0, "RFID or Pass Number", headerFormat));
        s.addCell(new Label(6, 0, "Passport Validity", headerFormat));
        s.addCell(new Label(7, 0, "Valid UpTo", headerFormat));
        s.addCell(new Label(8, 0, "Approved Company", headerFormat));
        return workbook;
    }


    //---ARUN GEBE---

    @GetMapping("vivo-details-download")
    public ResponseEntity<List<VivoPass>> getVehicleThroughExcel(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, ParseException {
        List<VivoPass> monthlyEmployeeAttendanceDtos = vivoPassRepo.findAll();
        OutputStream out = null;
        String fileName = "Vehicle_Report";
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName + ".xls");
        WritableWorkbook workbook = jxl.Workbook.createWorkbook(response.getOutputStream());
        try {
            if (monthlyEmployeeAttendanceDtos != null) {
                attendanceEntry1(workbook, monthlyEmployeeAttendanceDtos, response, 0);
            }
            if (monthlyEmployeeAttendanceDtos.isEmpty())
                throw new EntityNotFoundException("entries");
            workbook.write();
            workbook.close();
        } catch (Exception e) {
            throw new ServletException("Exception in excel download", e);
        } finally {
            if (out != null)
                out.close();
        }
        return new ResponseEntity(monthlyEmployeeAttendanceDtos, HttpStatus.OK);
    }

    private WritableWorkbook attendanceEntry1(WritableWorkbook workbook, List<VivoPass> monthlyEmployeeAttendanceDto, HttpServletResponse response, int index) throws IOException, WriteException {
        WritableSheet s = workbook.createSheet("Vehicle-Report", index);
        s.getSettings().setPrintGridLines(false);
        WritableFont headerFont = new WritableFont(WritableFont.TIMES, 14);
        headerFont.setBoldStyle(WritableFont.BOLD);
        WritableCellFormat headerFormat = new WritableCellFormat(headerFont);
        headerFormat.setAlignment(CENTRE);
        WritableFont headerFontLeft = new WritableFont(WritableFont.TIMES, 14);
        headerFontLeft.setBoldStyle(WritableFont.BOLD);
        WritableCellFormat headerFormatLeft = new WritableCellFormat(headerFontLeft);
        headerFormatLeft.setAlignment(LEFT);
        WritableFont headerFontRight = new WritableFont(WritableFont.TIMES, 14);
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
        int rowNum = 1;
        for (VivoPass employeeAttendanceDto : monthlyEmployeeAttendanceDto) {
            s.addCell(new Label(0, 0, "#", cellFormat));
            s.addCell(new Label(0, rowNum, "" + Integer.valueOf(rowNum)));
            s.addCell(new Label(1, 0, "Date", cellFormat));
            s.addCell(new Label(1, rowNum, "" + employeeAttendanceDto.getMarkedOn()));
//                VivoPass vivoPass = vivoPassRepo.findByVehicleNumber(employeeAttendanceDto.getVehicleNumber());
            s.addCell(new Label(2, 0, "RFID", cellFormat));
            s.addCell(new Label(2, rowNum, "" + employeeAttendanceDto.getRfid()));
            s.addCell(new Label(3, 0, "Vehicle Type", cellFormat));
            if (employeeAttendanceDto.getVehicleType() != null) {
                s.addCell(new Label(3, rowNum, "" + employeeAttendanceDto.getVehicleType().getTypeOfVehicle()));
            }
            s.addCell(new Label(4, 0, "Purpose", cellFormat));
            s.addCell(new Label(4, rowNum, "" + employeeAttendanceDto.getPurpose()));
            s.addCell(new Label(5, 0, "Vehicle Number", cellFormat));
            s.addCell(new Label(5, rowNum, "" + employeeAttendanceDto.getVehicleNumber()));
            s.addCell(new Label(6, 0, "Driver Details", cellFormat));
            s.addCell(new Label(6, rowNum, "" + employeeAttendanceDto.getDriverDetails()));
            s.addCell(new Label(6, 0, "DL Number", cellFormat));
            s.addCell(new Label(6, rowNum, "" + employeeAttendanceDto.getDlNumber()));
            s.addCell(new Label(7, 0, "DL Valid Till", cellFormat));
            s.addCell(new Label(7, rowNum, "" + employeeAttendanceDto.getDlValidity()));
            s.addCell(new Label(8, 0, "Route", cellFormat));
            s.addCell(new Label(8, rowNum, "" + employeeAttendanceDto.getStayTime()));
            s.addCell(new Label(9, 0, "Approved Company", cellFormat));
            s.addCell(new Label(9, rowNum, "" + employeeAttendanceDto.getCompanyName()));
            s.addCell(new Label(10, 0, "Pass Start Date", cellFormat));
            s.addCell(new Label(10, rowNum, "" + employeeAttendanceDto.getStartDate()));
            s.addCell(new Label(11, 0, "Pass End Date", cellFormat));
            s.addCell(new Label(11, rowNum, "" + employeeAttendanceDto.getEndDate()));
            rowNum = rowNum + 1;

        }
        return workbook;
    }

    @GetMapping("vivo-pass-expiry-view")
    public ResponseEntity<List<VivoInfoDto>> getPassExpiryView() {
        List<VivoPass> vivoPassList = vivoPassRepo.findAll();
        List<VivoInfoDto> vivoInfoDtoList = new ArrayList<>();
        for (VivoPass v : vivoPassList) {
            VivoInfoDto vivoInfoDto = new VivoInfoDto();
            Date d = new Date();
            if (v.getStartDate() != null && v.getEndDate() != null) {
                if (d.after(v.getEndDate()) || d.before(v.getStartDate())) {
                    vivoInfoDto.setRfid(v.getRfid());
                    vivoInfoDto.setTypeOfVehicle(v.getTypeOfVehicle());
                    vivoInfoDto.setVehicleNumber(v.getVehicleNumber());
                    vivoInfoDto.setDriverDetails(v.getDriverDetails());
                    vivoInfoDto.setEmpname(v.getDriverDetails());
                    vivoInfoDto.setEmpId(v.getEmpId());
                    vivoInfoDto.setRfid(v.getCardId());
                    vivoInfoDto.setEndDate(v.getEndDate());
                    vivoInfoDtoList.add(vivoInfoDto);
                }
            } else {
                vivoInfoDtoList.add(vivoInfoDto);
            }
        }
        return new ResponseEntity<>(vivoInfoDtoList, HttpStatus.OK);
    }


    @GetMapping("vivo-pass-expire-report")
    public ResponseEntity<List<VivoInfoDto>> getVehiclePassExpiryReport(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, ParseException {
        List<VivoInfoDto> monthlyEmployeeAttendanceDtos = getPassExpiry();
        OutputStream out = null;
        String fileName = "Vehicle_Pass_Expiry_Report";
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName + ".xls");
        WritableWorkbook workbook = jxl.Workbook.createWorkbook(response.getOutputStream());
        try {
            if (monthlyEmployeeAttendanceDtos != null) {
                attendanceEntry2(workbook, monthlyEmployeeAttendanceDtos, response, 0);
            }
            if (monthlyEmployeeAttendanceDtos.isEmpty())
                throw new EntityNotFoundException("entries");
            workbook.write();
            workbook.close();
        } catch (Exception e) {
            throw new ServletException("Exception in excel download", e);
        } finally {
            if (out != null)
                out.close();
        }
        return new ResponseEntity(monthlyEmployeeAttendanceDtos, HttpStatus.OK);
    }

    private WritableWorkbook attendanceEntry2(WritableWorkbook workbook, List<VivoInfoDto> monthlyEmployeeAttendanceDto, HttpServletResponse response, int index) throws IOException, WriteException {
        WritableSheet s = workbook.createSheet("Vehicle-Pass-Expire-Report", index);
        s.getSettings().setPrintGridLines(false);
        WritableFont headerFont = new WritableFont(WritableFont.TIMES, 14);
        headerFont.setBoldStyle(WritableFont.BOLD);
        WritableCellFormat headerFormat = new WritableCellFormat(headerFont);
        headerFormat.setAlignment(CENTRE);
        WritableFont headerFontLeft = new WritableFont(WritableFont.TIMES, 14);
        headerFontLeft.setBoldStyle(WritableFont.BOLD);
        WritableCellFormat headerFormatLeft = new WritableCellFormat(headerFontLeft);
        headerFormatLeft.setAlignment(LEFT);
        WritableFont headerFontRight = new WritableFont(WritableFont.TIMES, 14);
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
        int rowNum = 1;
        for (VivoInfoDto employeeAttendanceDto : monthlyEmployeeAttendanceDto) {
            s.addCell(new Label(0, 0, "#", cellFormat));
            s.addCell(new Label(0, rowNum, "" + Integer.valueOf(rowNum)));
            s.addCell(new Label(1, 0, "RFID", cellFormat));
            s.addCell(new Label(1, rowNum, "" + employeeAttendanceDto.getRfid()));
            s.addCell(new Label(2, 0, "Vehicle Type", cellFormat));
            s.addCell(new Label(2, rowNum, "" + employeeAttendanceDto.getTypeOfVehicle()));
//            VivoPass vivoPass=vivoPassRepo.findByVehicleNumber(employeeAttendanceDto.getTypeOfVehicle());
            s.addCell(new Label(3, 0, "Vehicle Number", cellFormat));
            s.addCell(new Label(3, rowNum, "" + employeeAttendanceDto.getVehicleNumber()));
            s.addCell(new Label(4, 0, "Driver Details", cellFormat));
            s.addCell(new Label(4, rowNum, "" + employeeAttendanceDto.getDriverDetails()));
            s.addCell(new Label(5, 0, "Start Date", cellFormat));
            s.addCell(new Label(5, rowNum, "" + employeeAttendanceDto.getStartDate()));
            s.addCell(new Label(6, 0, "End Date", cellFormat));
            s.addCell(new Label(6, rowNum, "" + employeeAttendanceDto.getEndDate()));
            rowNum = rowNum + 1;

        }
        return workbook;
    }

    public List<VivoInfoDto> getPassExpiry() {
        List<VivoPass> vivoPassList = vivoPassRepo.findAll();
        List<VivoInfoDto> vivoInfoDtoList = new ArrayList<>();
        for (VivoPass v : vivoPassList) {
            VivoInfoDto vivoInfoDto = new VivoInfoDto();
            Date d = new Date();
            if (d.after(v.getEndDate()) || d.before(v.getStartDate())) {
                vivoInfoDto.setTypeOfVehicle(v.getTypeOfVehicle());
                vivoInfoDto.setVehicleNumber(v.getVehicleNumber());
                vivoInfoDto.setDriverDetails(v.getDriverDetails());
                vivoInfoDto.setEmpname(v.getDriverDetails());
                vivoInfoDto.setEmpId(v.getEmpId());
                vivoInfoDto.setRfid(v.getCardId());
                vivoInfoDto.setStartDate(v.getStartDate());
                vivoInfoDto.setEndDate(v.getEndDate());
                vivoInfoDtoList.add(vivoInfoDto);
            }
        }
        return vivoInfoDtoList;
    }

    /**
     * Direct veh checkout
     */
    @PostMapping("vivo-direct-check-out/{id}")
    public void directCheckOutVehicle(@PathVariable long id) throws ParseException {
        VivoInfo vivoInfo = vivoInfoService.get(id);
        if (vivoInfo == null) {
            throw new EntityNotFoundException("CheckIn first");
        }
        if (vivoInfo.getExitTime() != null) {
            throw new EntityNotFoundException("Vehicle Already Checked Out");
        }
        DateFormat timeFormat = new SimpleDateFormat("HH.mm");
        String s = timeFormat.format(vivoInfo.getCheckedIn().getTime());
        vivoInfo.setEntryTime(s);
        vivoInfo.setDriverEntryTime(s);
        VivoInfo v = calculateTotalStayTime(vivoInfo);
        calculateDriverTotalStayTime(vivoInfo);
        v.setMarkedOn(new Date());
        v.setCheckedOut(new Date());
        vivoInfoService.save(v);
    }


    /**
     * Calling another api
     */
    private static void getEmployees() {
//        final String uri = "http://localhost:8080/springrestexample/employees.xml";
        final String uri = "http://localhost:8082/vivo/sync-trans";
        RestTemplate restTemplate = new RestTemplate();
        String result = "0";
        if (uri != null) {
            result = restTemplate.getForObject(uri, String.class);
        }
        System.out.println(result);
    }


    @GetMapping("vivo-renew-all-for-1year")
    public void passrenewForAll() {
        List<VivoPass> visitorPasses = vivoPassRepo.findAll();
        for (VivoPass vivoPass : visitorPasses) {
            Calendar cal = Calendar.getInstance();
            Date today = cal.getTime();
            cal.add(Calendar.YEAR, 2); // to get previous year add -1
            Date nextYear = cal.getTime();
            vivoPass.setStartDate(today);
            vivoPass.setEndDate(nextYear);
            vivoPassRepo.save(vivoPass);
        }
    }


    /**
     * Bay wise Screens
     *
     * @return
     */
//    @GetMapping("vivo/slots-availablity-utilization-screen/{bayId}")
//    public ResponseEntity<List<VivoDto>> bayWiseScreensUtil(@PathVariable long bayId) {
//        List<VivoDto> vivoDtos = new ArrayList<>();
//        List<TypeOfVehicle> typeOfVehicles = typeOfVehicleService.getAllVehicles();
//        for (TypeOfVehicle typeOfVehicle : typeOfVehicles) {
//            List<Bay> bays = bayService.getByType(typeOfVehicle.getTypeOfVehicle());
//            VivoDto vivoDto = new VivoDto();
//            List<Slot> slotList = new ArrayList<>();
//            long util = 0;
//            long avil = 0;
//            Bay bay = bayService.getBay(bayId);
//            List<Slot> slots = bay.getSlots();
//            for (Slot slot : slots) {
//                VivoInfo vivoInfo = vivoInfoService.getByAllocatedInfoByBayAndSlot(bay.getBayNumber(), slot.getSlotNumber());
//                if (vivoInfo != null) {
//                    slot.setAvailable(false);
//                    slotList.add(slot);
//                    util++;
//                    vivoDto.setUtilisedSlots(util);
//                } else {
//                    slot.setAvailable(true);
//                    slotList.add(slot);
//                    avil++;
//                    vivoDto.setAvailableSlots(avil);
//                }
//            }
//            vivoDto.setBayNumber(bay.getBayNumber());
//            vivoDto.setVehicleType(typeOfVehicle.getTypeOfVehicle());
//            vivoDto.setTotalSlots(slotList.size());
//            vivoDtos.add(vivoDto);
//        }
//        return new ResponseEntity<>(vivoDtos, HttpStatus.OK);
//    }
    @GetMapping("vivo/slots-availablity-utilization-screen/{bayId}")
    public ResponseEntity<List<VivoDto>> bayWiseScreensUtil(@PathVariable long bayId) throws ParseException {
        List<VivoDto> vivoDtos = new ArrayList<>();
        List<TypeOfVehicle> typeOfVehicles = typeOfVehicleService.getAllVehicles();
        for (TypeOfVehicle typeOfVehicle : typeOfVehicles) {
            List<Bay> bays = bayService.getByType(typeOfVehicle.getTypeOfVehicle());
            VivoDto vivoDto = new VivoDto();
            Bay baySelected = bayService.getBay(bayId);
            for (Bay bay : bays) {
                long util = 0;
                long avil = 0;
                List<Slot> slotList = new ArrayList<>();
                List<Slot> slots = null;
                if (baySelected.getBayNumber().equalsIgnoreCase(bay.getBayNumber())) {
                    slots = bay.getSlots();
                }
                if (slots != null) {
//                    for (Slot slot : slots) {
//                        List<VivoInfo> vivoInfoOnlyBay = vivoInfoService.getByAllocatedInfoByBay(bay.getBayNumber());
//                        VivoInfo vivoInfo = vivoInfoService.getByAllocatedInfoByBayAndSlot(bay.getBayNumber(), slot.getSlotNumber());
//                        if (vivoInfo != null) {
//                            slot.setAvailable(false);
//                            slotList.add(slot);
//                            util++;
//                            vivoDto.setUtilisedSlots(util);
//                        } else {
//                            slot.setAvailable(true);
//                            slotList.add(slot);
//                            avil++;
//                            vivoDto.setAvailableSlots(avil);
//                        }
//                    }

//                    List<VivoInfo> vivoInfoOnlyBay = vivoInfoService.getByAllocatedInfoByBay(bay.getBayNumber(), typeOfVehicle.getTypeOfVehicle());
                    List<VivoInfo> vivoInfoOnlyBay = vivoInfoService.getByAllocatedInfoByBay(bay.getBayNumber(), typeOfVehicle.getTypeOfVehicle(), new Date());

                    if (vivoInfoOnlyBay != null) {
                        vivoDto.setTotalSlots(slots.size());
                        vivoDto.setVehicleType(typeOfVehicle.getTypeOfVehicle());
                        vivoDto.setUtilisedSlots(vivoInfoOnlyBay.size());
                        vivoDto.setBayNumber(bay.getBayNumber());
                        vivoDto.setAvailableSlots(slots.size() - vivoInfoOnlyBay.size());
                        vivoDtos.add(vivoDto);
                    }
                }

            }

        }
        return new ResponseEntity<>(vivoDtos, HttpStatus.OK);
    }


    @PostMapping("vehicle-report-mysql-sql")
    public boolean checkDate(@RequestBody DateDto dateDto) {
        boolean b = true;
        CompanyConfigure companyConfigure = companyConfigureService.getCompanyById(1);
        Date installedDate = companyConfigure.getTrialDate();
        if (dateDto.startDate.after(installedDate)) {
            b = true;
        }
        if (dateDto.startDate.before(installedDate)) {
            b = false;
        }
        return b;
    }

    @GetMapping("notify-vivo-info")
    public List<String> attendanceNotify() throws ParseException {
        List<VivoInfo> vivoInfos = vivoInfoService.getByMarkedOn(new Date());
        List<String> strings = new ArrayList<>();
        for (VivoInfo vivoInfo : vivoInfos) {
            if (vivoInfo.getCheckedIn() == null) {
                String s = "No InTime for " + vivoInfo.getVehicleNumber() + " On " + vivoInfo.getMarkedOn();
                strings.add(s);
            }
        }
        return strings;
    }

    @Autowired
    FileStorageService fileStorageService;

    @GetMapping("/downloadFile/{id}")
    public ResponseEntity<Resource> getDownloadAnyDocFileById(@PathVariable long id) {
        Document sopDocumentUpload = fileStorageService.getDocument(id);
        Document sopDocumentUploadObj = sopDocumentUpload;
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(sopDocumentUploadObj.getFileType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + sopDocumentUploadObj.getFileName()+ "\"")
                .body(new ByteArrayResource(sopDocumentUploadObj.getData()));
    }

}
