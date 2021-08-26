package com.dfq.coeffi.StoreManagement.Controller;

import com.dfq.coeffi.StoreManagement.Admin.Entity.MinPercentage;
import com.dfq.coeffi.StoreManagement.Entity.*;
import com.dfq.coeffi.StoreManagement.Repository.*;
import com.dfq.coeffi.StoreManagement.Service.EmployeeRequestService;
import com.dfq.coeffi.StoreManagement.Service.MaterialsService;
import com.dfq.coeffi.controller.BaseController;
import com.dfq.coeffi.entity.hr.employee.Employee;
import com.dfq.coeffi.service.hr.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

@RestController
public class EmployeeRequestController extends BaseController {
    @Autowired
    EmployeeRequestService employeeRequestService;
    @Autowired
    EmployeeService employeeService;
    @Autowired
    ItemsRepository itemsRepository;
    @Autowired
    MaterialsRequestRepository materialsRequestRepository;
    @Autowired
    RequestNumberRepository requestNumberRepository;
    @Autowired
    OtpRepository otpRepository;

    @PostMapping("employee-request-submit")
    public ResponseEntity<EmployeeRequest> submit(@RequestBody MaterialDto  expensesDto) throws Exception {
        Date date=new Date();
        Optional<Employee> employee = employeeService.getEmployee(expensesDto.getEmployeeId());
        Employee emp = employee.get();
        EmployeeRequest employeeRequest=new EmployeeRequest();
        employeeRequest.setMaterialsStatus(MaterialsEnum.Submitted);
        employeeRequest.setRequestNumber(expensesDto.getRequestNumber()) ;
        employeeRequest.setDepartment(expensesDto.getDepartment());
        employeeRequest.setPurpose(expensesDto.getPurpose());
        employeeRequest.setDate(expensesDto.getDate());
        employeeRequest.setCostCenter(expensesDto.getCostCenter());
        employeeRequest.setRequestedBy(employee.get().getFirstName()+" "+employee.get().getLastName());
        employeeRequest.setRequestIndication(expensesDto.isRequestIndication());

        if(expensesDto.getApprovalEnum().equals("WithApproval")){
            employeeRequest.setManagerId(emp.getFirstApprovalManager().getId());
        }
        employeeRequest.setMarkedOn(date);
        if(expensesDto.getApprovalEnum().equals("WithOutApproval")){
            employeeRequest.setOtp(expensesDto.getOtp());
            employeeRequest.setManagerId(0);
            employeeRequest.setOtpValidation(OtpValidationEnum.Yes);
        }
        else if(expensesDto.getApprovalEnum().equals("WithApproval")){
            employeeRequest.setOtp(null);
        }
        employeeRequest.setMaterialType(expensesDto.getMaterialType());
        employeeRequest.setCustomerName(expensesDto.getCustomerName());
        employeeRequest.setJobOrderNumber(expensesDto.getJobOrderNumber());
        employeeRequest.setProductName(expensesDto.getProductName());
        employeeRequest.setApprovalEnum(expensesDto.getApprovalEnum());
        employeeRequest.setEmployeeName(expensesDto.getEmployeeName());
        employeeRequest.setEmployeeId(expensesDto.getEmployeeId());
        employeeRequest.setMaterials(expensesDto.getMaterials());
        for (Materials  e : expensesDto.getMaterials()) {
//            Items items=itemsRepository.findOne(e.getItemNumber());
            Items items=itemsRepository.findByItemNumber(e.getItemNumber());
            if(e.getQuantity()>items.getQuantity()){
                throw new Exception(e.getItemName()+" "+"Quantity Insufficient");
            }
            else {
                long quantity=0;
                quantity=items.getQuantity()-e.getQuantity();
                items.setQuantity(quantity);
                itemsRepository.save(items);

            }
        }
        employeeRequestService.saveRequest(employeeRequest);
        return new ResponseEntity<>(employeeRequest,HttpStatus.CREATED);
    }

    @GetMapping("store-request-view")
    public ResponseEntity<List<EmployeeRequest>> getEmployeeRequest(){
        List<EmployeeRequest> employeeRequestList=employeeRequestService.getRequests();
        List<EmployeeRequest> employeeRequestList1=new ArrayList<>();
        for (EmployeeRequest e:employeeRequestList) {
            if(e.getMaterialsStatus().equals(MaterialsEnum.Submitted) && e.getManagerId() == 0) {
                employeeRequestList1.add(e);
            }
            else if(e.getMaterialsStatus().equals(MaterialsEnum.Approved)) {
               employeeRequestList1.add(e);
            }
        }

        return new ResponseEntity<>(employeeRequestList1,HttpStatus.OK) ;
    }

    @GetMapping("get-employee-request/{id}")
    public ResponseEntity<EmployeeRequestDto> getEmployee(@PathVariable("id")long empid){
        Date date=new Date();
        Optional<Employee> employee=employeeService.getEmployee(empid);
        EmployeeRequestDto employeeRequestDto=new EmployeeRequestDto();
        employeeRequestDto.setEmployeeName(employee.get().getFirstName()+" "+employee.get().getLastName());
        employeeRequestDto.setDepartment(employee.get().getDepartment().getName());
        employeeRequestDto.setDate(date);
        return new ResponseEntity<>(employeeRequestDto,HttpStatus.OK);
    }

    @PostMapping("meterial-issue/{id}")
    public ResponseEntity<EmployeeRequest>  saveMaterial(@PathVariable("id") long id){
        EmployeeRequest employeeRequest=employeeRequestService.getRequest(id);
        employeeRequest.setMaterialsStatus(MaterialsEnum.Completed);
        Date date=new Date();
        employeeRequest.setIssuedDate(date);
        employeeRequestService.saveRequest(employeeRequest);
        return new ResponseEntity<>(employeeRequest,HttpStatus.OK);
    }
    @GetMapping("get-request-Number/{id}")
    public ResponseEntity<RequestNumber> getRequestNumbers(@PathVariable("id") long empId){
        RequestNumber requestNumber=requestNumberRepository.findOne((long)1) ;
        Optional<Employee> employee=employeeService.getEmployee(empId);
        String costCenter=employee.get().getDepartment().getName();
        System.out.println(costCenter);
        Date date=new Date();
        ZonedDateTime localDate=date.toInstant().atZone(ZoneId.systemDefault());
        Calendar cal = Calendar.getInstance();
        System.out.println(date.getDate()+" "+date.getMonth()+" "+date.getYear());
        String d= String.valueOf(cal.get(Calendar.DATE ));
        String m= String.valueOf(localDate.getMonthValue());
        String y= String.valueOf(cal.get(Calendar.YEAR));
        requestNumber.setDateStr(d+m+y);
        requestNumber.setCostCenter(costCenter.charAt(0));
        requestNumber.setNumber(requestNumber.getNumber()+1);
        requestNumberRepository.save(requestNumber);
        return new ResponseEntity<>(requestNumber ,HttpStatus.OK);
    }

    @GetMapping("favorite-list/{id}")
    public ResponseEntity<List<Materials>> getFavoriteList(@PathVariable("id") long empId){
        List<Materials> materialsList=materialsRequestRepository.findByEmployeeId(empId);
        int i=1;
        List<Materials> materialsArrayList=new ArrayList<>();
        for (Materials m:materialsList) {
            Materials materials=materialsList.get(i);
            if(m.getQuantity()>materials.getQuantity()){
                materialsArrayList.add(m);
                i=i+1;
            }
        }
        return new ResponseEntity<>(materialsArrayList,HttpStatus.OK);
    }

    @GetMapping("employee-view/{id}")
    public ResponseEntity<List<Materials>> getEmployeeView(@PathVariable("id") long empId){
        List<Materials> materialsList=materialsRequestRepository.findByEmployeeId(empId);

        return new ResponseEntity<>(materialsList,HttpStatus.OK);
    }

    @GetMapping("get-otp")
    public ResponseEntity<Otp> getOtp(){
        Otp otp=otpRepository.findOne((long)1);
        String Capital_chars="ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String Small_chars = "abcdefghijklmnopqrstuvwxyz";
        int num1=otp.getF();
        int num2=otp.getS();
        int num3=otp.getL();
        int num4=otp.getF1();
        int num5=otp.getL1();
        if(otp.getS()>otp.getF()){
            otp.setS(otp.getS()+1);
           num2=otp.getS()+1;
        }
        else if(otp.getL()>otp.getF()){
            otp.setL(otp.getL()+1);
            num3=otp.getL()+1;
        }
        else{
            otp.setF(otp.getF()+1);
            num1=otp.getF()+1;

        }
        if(otp.getF()>15){
            otp.setF(0);
            num1 = otp.getF();
        }
        else if(otp.getS()>15){
            otp.setS(0);
            num2 = otp.getS();

        }
        else if(otp.getL()>15){
            otp.setL(0);
            num3 = otp.getL();
        }
        else if(otp.getF1()>15){
            otp.setF1(0);
            num4 = otp.getF1();

        }
        else if(otp.getL1()>15){
            otp.setL1(0);
            num5 = otp.getL1();
        }
        otp.setCharOtp1(Capital_chars.charAt(num1));
        otp.setCharOtp2(Capital_chars.charAt(num2));
        otp.setCharOtp3(Capital_chars.charAt(num3));
        if(otp.getL1()>otp.getF1()){
            otp.setL1(otp.getL1()+1);
            num4=otp.getL1()+1;
        }
        else{
            otp.setF1(otp.getF1()+1);
            num5=otp.getF1()+1;
        }
        otp.setCharOtp4(Capital_chars.charAt(num4) );
        otp.setCharOtp5(Capital_chars.charAt(num5) );
        otp.setNumberValue(otp.getNumberValue()+1);
        otpRepository.save(otp);
        return new ResponseEntity<>(otp,HttpStatus.OK );
    }

    @GetMapping("employee-self-view/{id}")
    public ResponseEntity<List<EmployeeRequest>> getEmployeeSelfRequest(@PathVariable("id") long empId){
        List<EmployeeRequest> employeeRequestList=employeeRequestService.getRequests();
        List<EmployeeRequest> employeeRequestList1=new ArrayList<>();
        Date date=new Date();
        for (EmployeeRequest e:employeeRequestList) {
            if(e.getEmployeeId()==empId) {
                employeeRequestList1.add(e);
            }

        }

        return new ResponseEntity<>(employeeRequestList,HttpStatus.OK) ;
    }
    @GetMapping("store-processed-list")
    public ResponseEntity<List<EmployeeRequest>> getProcessedList(){
        List<EmployeeRequest> employeeRequestList=employeeRequestService.getRequests();
        List<EmployeeRequest> employeeRequestList1=new ArrayList<>();
        for (EmployeeRequest e:employeeRequestList) {
            if(e.getMaterialsStatus().equals(MaterialsEnum.Completed)||e.getMaterialsStatus().equals(MaterialsEnum.Rejected) ){
                employeeRequestList1.add(e);
            }

        }
        return new ResponseEntity<>(employeeRequestList1,HttpStatus.OK);
    }

    @GetMapping("report-price/{price}")
    public ResponseEntity<List<EmployeeRequest>> getReportPrice(@PathVariable("price") long price){
        List<EmployeeRequest> employeeRequestList=employeeRequestService.getRequests();
        List<EmployeeRequest> employeeRequestList1=new ArrayList<>();
        for (EmployeeRequest e:employeeRequestList) {
            List<Materials> materialsList=e.getMaterials();
            for (Materials m:materialsList) {
                if(m.getItemPrice()==price){
                    employeeRequestList1.add(e);
                }
            }

        }
        return new ResponseEntity<>(employeeRequestList1,HttpStatus.OK);
    }

    @GetMapping("report-department/{dept}")
    public ResponseEntity<List<EmployeeRequest>> getReportDept(@PathVariable("dept") String dept){
        List<EmployeeRequest> employeeRequestList=employeeRequestService.getRequests();
        List<EmployeeRequest> employeeRequestList1=new ArrayList<>();
        for (EmployeeRequest e:employeeRequestList) {
            Optional<Employee> employee=employeeService.getEmployee(e.getEmployeeId());
            if(employee.get().getDepartment().getName().equals(dept)){
                employeeRequestList1.add(e);
            }

        }
        return new ResponseEntity<>(employeeRequestList1,HttpStatus.OK);

    }

    @GetMapping("report-item-category/{cat}")
    public ResponseEntity<List<EmployeeRequest>> getItemCategory(@PathVariable("cat") String cat){
        List<EmployeeRequest> employeeRequestList=employeeRequestService.getRequests();
        List<EmployeeRequest> employeeRequestList1=new ArrayList<>();
        for (EmployeeRequest e:employeeRequestList) {
            List<Materials> materialsList=e.getMaterials();
            for (Materials m:materialsList) {
                if (m.getItemCategory()==cat) {
                    employeeRequestList1.add(e);
                }
            }

        }
        return new ResponseEntity<>(employeeRequestList1,HttpStatus.OK);

    }






}
