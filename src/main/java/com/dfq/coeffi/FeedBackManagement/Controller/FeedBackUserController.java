package com.dfq.coeffi.FeedBackManagement.Controller;


import com.dfq.coeffi.CanteenManagement.Entity.CounterDetailsAdv;
import com.dfq.coeffi.CanteenManagement.Entity.FoodTimeMasterAdv;
import com.dfq.coeffi.CanteenManagement.Service.CounterDetailsService;
import com.dfq.coeffi.CanteenManagement.Service.EmployeeRechargeService;
import com.dfq.coeffi.CanteenManagement.Service.FoodTimeService;
import com.dfq.coeffi.FeedBackManagement.Entity.*;
import com.dfq.coeffi.FeedBackManagement.Repository.*;
import com.dfq.coeffi.StoreManagement.Entity.RequestNumber;
import com.dfq.coeffi.StoreManagement.Repository.RequestNumberRepository;
import com.dfq.coeffi.controller.BaseController;
import com.dfq.coeffi.entity.hr.employee.Employee;
import com.dfq.coeffi.entity.user.User;
import com.dfq.coeffi.service.UserService;
import com.dfq.coeffi.service.hr.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Parameter;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

@RestController
public class FeedBackUserController extends BaseController {

    @Autowired
    FeedBackParameterRepository feedBackParameterRepository;

    @Autowired
    FeedBackGradesRepository feedBackGradesRepository;

    @Autowired
    FeedBackTrackRepository feedBackTrackRepository;

    @Autowired
    EmployeeService employeeService;

    @Autowired
    RequestNumberRepository requestNumberRepository;

    @Autowired
    UserService userService;

    @Autowired
    RemarksRepository remarksRepository;

    @Autowired
    CounterDetailsService counterDetailsService;

    @Autowired
    FoodTimeService foodTimeService;

    @Autowired
    FeedBackCounterSettingsRepository feedBackCounterSettingsRepository;


    @PostMapping("feedback/counter/save")
    public ResponseEntity<FeedBackCounterSettings>  saveCounter(@RequestBody FeedBackCounterSettings feedBackCounterSettings){
        feedBackCounterSettingsRepository.save(feedBackCounterSettings);

        return new ResponseEntity<>(feedBackCounterSettings,HttpStatus.OK);
    }
    @PostMapping("feedback/user/validate")
    public ResponseEntity<ValidateDto> validate(@RequestBody UserCreDto userCreDto){
        List<User> userList=userService.getUsers();
        ValidateDto validateDto=new ValidateDto();
        CounterDetailsAdv counterDetailsAdv=counterDetailsService.getCounterDetails(userCreDto.getCounterId());
        validateDto.setCounterDetailsAdv(counterDetailsAdv);
        long userId=0;
        for (User u:userList) {
            if (u.getEmail().equals(userCreDto.getUsername()) && u.getPassword().equals(userCreDto.getPassword())) {
                validateDto.setValidate(true);
                userId=u.getId();
            }
        }
        List<Employee> employeeList=employeeService.findAll();
        for (Employee e:employeeList) {
            if(e.getEmployeeLogin()!=null){
                if(e.getEmployeeLogin().getId()==userId){
                    validateDto.setEmpId(e.getId());
                    validateDto.setEmployee(e);
                }
            }

        }

        return new ResponseEntity<>(validateDto,HttpStatus.OK);
    }

    @GetMapping("feedback/user/get-list")
    public ResponseEntity<FeedBackDto> getFeedBack() {
        FeedBackDto feedBackDto=new FeedBackDto();
        List<FeedBackParameter> feedBackParameterList=feedBackParameterRepository.findAll();
        List<FeedBackGrades> feedBackGradesList=feedBackGradesRepository.findAll();
        feedBackDto.setFeedBackParameterList(feedBackParameterList);
        feedBackDto.setFeedBackGradesList(feedBackGradesList);
        return new ResponseEntity<>(feedBackDto, HttpStatus.OK);
    }
    @PostMapping("feedback/user/save")
    public ResponseEntity<FeedBackTrack> saveFeedBack(@RequestBody FeedBackTrack feedBackTrack){
        Optional<Employee> employee=employeeService.getEmployee(feedBackTrack.getEmpId());
        CounterDetailsAdv counterDetailsAdv=counterDetailsService.getCounterDetails(feedBackTrack.getCounterDetailsAdv().getId());
        Date date=new Date();
        feedBackTrack.setCounterDetailsAdv(counterDetailsAdv);
        FeedBackParameter  parameter=feedBackParameterRepository.findOne(feedBackTrack.getFeedBackParameter().getId());
        feedBackTrack.setFeedBackParameter(parameter);
        FeedBackGrades feedBackGrades=feedBackGradesRepository.findOne(feedBackTrack.getFeedBackGrades().getId());
        feedBackTrack.setFeedBackGrades(feedBackGrades);
        feedBackTrack.setEmployee(employee.get());
        feedBackTrack.setCreatedDate(date);
        feedBackTrack.setFeedBackStatus(FeedBackStatus.Open);
        feedBackTrackRepository.save(feedBackTrack);
        return new ResponseEntity<>(feedBackTrack,HttpStatus.OK);
    }

    @PostMapping("feedback/remarks/submit")
    public ResponseEntity<Remarks> submitSave(@RequestBody Remarks remarks){
        List<FeedBackTrack> feedBackTrackList = feedBackTrackRepository.findFeedBacktrack(remarks.getEmpId(), new Date());
        List<FeedBackTrack> feedBackTracks=new ArrayList<>();
        CounterDetailsAdv counterDetailsAdv=counterDetailsService.getCounterDetails(remarks.getCounterDetailsAdv().getId());
        for (FeedBackTrack feed:feedBackTrackList) {
            if (feed.getEmpId() == remarks.getEmpId()) {
                if (feed.getFeedBackStatus().equals(FeedBackStatus.Open)) {
                    feedBackTracks.add(feed);
                }
            }
        }
        Optional<Employee> employee=employeeService.getEmployee(remarks.getEmpId());
        remarks.setFeedBackStatus(FeedBackStatus.Open);
        remarks.setEmployee(employee.get());
        remarks.setCounterDetailsAdv(counterDetailsAdv);
        remarks.setTicketNumber(ticketnumber());
        remarks.setFeedBackTrackList(feedBackTracks);
        remarks.setCreatedDate(new Date());
        FeedBackGrades feedBackGrades=feedBackGradesRepository.findOne(remarks.getOverAllRating().getId());
        remarks.setOverAllRating(feedBackGrades);
        remarksRepository.save(remarks);

        return new ResponseEntity<>(remarks,HttpStatus.OK);
    }
    public String ticketnumber() {
        RequestNumber requestNumber = requestNumberRepository.findOne((long) 1);
        Date date = new Date();
        ZonedDateTime localDate = date.toInstant().atZone(ZoneId.systemDefault());
        Calendar cal = Calendar.getInstance();
        String d = String.valueOf(cal.get(Calendar.DATE));
        String m = String.valueOf(localDate.getMonthValue());
        String y = String.valueOf(cal.get(Calendar.YEAR));
        requestNumber.setDateStr(d + m + y);
        requestNumber.setNumber(requestNumber.getNumber() + 1);
        String  number= d + m + y+requestNumber.getNumber() + 1;

        requestNumberRepository.save(requestNumber);
        return number;
    }

    @GetMapping("feedback/admin/list")
    public ResponseEntity<List<Remarks>> getAdminList(){
        List<Remarks> remarksList=remarksRepository.findAll();
        List<Remarks> remarksList1=new ArrayList<>();
        for (Remarks r:remarksList) {
            if(r.getFeedBackStatus().equals(FeedBackStatus.Open) ){
                Remarks remarks=new Remarks();
                remarks.setId(r.getId());
            remarks.setCounterDetailsAdv(r.getCounterDetailsAdv());
            remarks.setCreatedDate(r.getCreatedDate());
            remarks.setTicketNumber(r.getTicketNumber());
            remarks.setFeedBackStatus(r.getFeedBackStatus());
            remarks.setRemarks(r.getRemarks());
            remarks.setEmployee(r.getEmployee());
            List<FeedBackTrack> feedBackTrackList=new ArrayList<>();
                for (FeedBackTrack f:r.getFeedBackTrackList()) {
                    if(f.getFeedBackStatus().equals(FeedBackStatus.Open)){
                        feedBackTrackList.add(f);
                    }

                }
                remarks.setFeedBackTrackList(feedBackTrackList);
                remarksList1.add(remarks);

        }
        }
        return new ResponseEntity<>(remarksList1,HttpStatus.OK);
    }

    @GetMapping("feedback/admin/closed-list")
    public ResponseEntity<List<Remarks>> getAdminClosedList(){
        List<Remarks> remarksList=remarksRepository.findAll();
        List<Remarks> remarksList1=new ArrayList<>();
        for (Remarks r:remarksList) {
            if(r.getFeedBackStatus().equals(FeedBackStatus.Closed) ){
                Remarks remarks=new Remarks();
                remarks.setId(r.getId());
                remarks.setCounterDetailsAdv(r.getCounterDetailsAdv());
                remarks.setCreatedDate(r.getCreatedDate());
                remarks.setTicketNumber(r.getTicketNumber());
                remarks.setFeedBackStatus(r.getFeedBackStatus());
                remarks.setRemarks(r.getRemarks());
                remarks.setEmployee(r.getEmployee());
                remarks.setAdminRemarks(r.getAdminRemarks());
                List<FeedBackTrack> feedBackTrackList=new ArrayList<>();
                for (FeedBackTrack f:r.getFeedBackTrackList()) {
                    if(f.getFeedBackStatus().equals(FeedBackStatus.Closed)){
                        feedBackTrackList.add(f);
                    }

                }
                remarks.setFeedBackTrackList(feedBackTrackList);
                remarksList1.add(remarks);

            }
        }
        return new ResponseEntity<>(remarksList1,HttpStatus.OK);
    }

    @PostMapping("feedback/admin/submit")
    public ResponseEntity<Remarks> saveAdminFeedBack(@RequestBody Remarks remarks){
        Date date=new Date();
        if(remarks.getId()!=0){
            remarks.setFeedBackStatus(FeedBackStatus.Closed);
            remarks.setAdminDate(date);
            for (FeedBackTrack f:remarks.getFeedBackTrackList()) {
                if(f.getId()!=0) {
                    FeedBackTrack feedBackTrack=feedBackTrackRepository.findOne(f.getId());
                    feedBackTrack.setFeedBackStatus(FeedBackStatus.Closed);
                    feedBackTrackRepository.save(feedBackTrack);
                }

            }
            remarksRepository.save(remarks);
        }
        return new ResponseEntity<>(remarks,HttpStatus.OK);

    }




}
