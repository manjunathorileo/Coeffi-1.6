package com.dfq.coeffi.StoreManagement.Controller;

import com.dfq.coeffi.StoreManagement.Entity.*;
import com.dfq.coeffi.StoreManagement.Repository.EmployeeRequestRepository;
import com.dfq.coeffi.StoreManagement.Repository.ItemsRepository;
import com.dfq.coeffi.StoreManagement.Repository.MaterialsRequestRepository;
import com.dfq.coeffi.StoreManagement.Service.EmployeeRequestService;
import com.dfq.coeffi.StoreManagement.Service.MaterialsService;
import com.dfq.coeffi.controller.BaseController;
import com.dfq.coeffi.entity.hr.employee.Employee;
import com.dfq.coeffi.service.hr.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
public class ManagerApprovalController extends BaseController {

    @Autowired
    EmployeeRequestService employeeRequestService;
    @Autowired
    MaterialsService materialsService;
    @Autowired
    EmployeeService employeeService;
    @Autowired
    EmployeeRequestRepository employeeRequestRepository;
    @Autowired
    ItemsRepository itemsRepository;
    @Autowired
    MaterialsRequestRepository materialsRequestRepository;

    @GetMapping("manager-request-view/{mgrId}")
    public ResponseEntity<List<EmployeeRequest>> getManagerRequests(@PathVariable("mgrId") long mgrId) {
        List<EmployeeRequest> employeeRequest1 = employeeRequestService.getRequests();
        List<EmployeeRequest> employeeRequestList = new ArrayList<>();
        Date date = new Date();
        for (EmployeeRequest e : employeeRequest1) {
            if (e.getMaterialsStatus().equals(MaterialsEnum.Submitted) && e.getManagerId() == mgrId) {
                employeeRequestList.add(e);
            }
        }

        return new ResponseEntity<>(employeeRequestList, HttpStatus.OK);
    }

    @GetMapping("manager-request-view-for-mgr")
    public ResponseEntity<List<EmployeeRequest>> getManagerRequestsForStoreMgr() {
        List<EmployeeRequest> employeeRequest1 = employeeRequestService.getRequests();
        List<EmployeeRequest> employeeRequestList = new ArrayList<>();
        Date date = new Date();
        for (EmployeeRequest e : employeeRequest1) {
            if (e.getMaterialsStatus().equals(MaterialsEnum.Submitted)) {
                employeeRequestList.add(e);
            }
        }

        return new ResponseEntity<>(employeeRequestList, HttpStatus.OK);
    }

    @PostMapping("manager-approve/{id}/{otp}")
    public ResponseEntity<EmployeeRequest> saveApprove(@PathVariable("id") long empid, @PathVariable("otp") String otp) {
        EmployeeRequest employeeRequest = employeeRequestService.getRequest(empid);
        System.out.println(employeeRequest.getManagerId());
        Optional<Employee> employee = employeeService.getEmployee(employeeRequest.getManagerId());
        employeeRequest.setApprovedBy(employee.get().getFirstName() + " " + employee.get().getLastName());
        Date date = new Date();
        employeeRequest.setApprovedDate(date);
        employeeRequest.setMaterialsStatus(MaterialsEnum.Approved);
        employeeRequest.setOtp(otp);
        employeeRequest.setOtpValidation(OtpValidationEnum.Yes);
        employeeRequestService.saveRequest(employeeRequest);

        return new ResponseEntity<>(employeeRequest, HttpStatus.OK);
    }

    @PostMapping("manager-approve-mgr/{id}/{otp}")
    public ResponseEntity<EmployeeRequest> saveApproveMgr(@PathVariable("id") long empid, @PathVariable("otp") String otp) {
        EmployeeRequest employeeRequest = employeeRequestService.getRequest(empid);
        System.out.println(employeeRequest.getManagerId());
        employeeRequest.setApprovedBy("Store manager");
        Date date = new Date();
        employeeRequest.setApprovedDate(date);
        employeeRequest.setMaterialsStatus(MaterialsEnum.Approved);
        employeeRequest.setOtp(otp);
        employeeRequest.setOtpValidation(OtpValidationEnum.Yes);
        employeeRequestService.saveRequest(employeeRequest);

        return new ResponseEntity<>(employeeRequest, HttpStatus.OK);
    }

    @PostMapping("manager-reject/{id}/{rC}")
    public ResponseEntity<EmployeeRequest> saveReject(@PathVariable("id") long empid, @PathVariable("rC") String rC) {
        EmployeeRequest employeeRequest = employeeRequestService.getRequest(empid);
        employeeRequest.setMaterialsStatus(MaterialsEnum.Rejected);
        Date date = new Date();
        employeeRequest.setRejectDate(date);
        employeeRequest.setReasonCode(rC);
        employeeRequestService.saveRequest(employeeRequest);
        return new ResponseEntity<>(employeeRequest, HttpStatus.OK);
    }

    @PostMapping("manager-reject-mgr/{id}/{rC}")
    public ResponseEntity<EmployeeRequest> saveRejectMgr(@PathVariable("id") long empid, @PathVariable("rC") String rC) {
        EmployeeRequest employeeRequest = employeeRequestService.getRequest(empid);
        employeeRequest.setMaterialsStatus(MaterialsEnum.Rejected);
        Date date = new Date();
        employeeRequest.setRejectDate(date);
        employeeRequest.setReasonCode(rC);
        employeeRequestService.saveRequest(employeeRequest);
        return new ResponseEntity<>(employeeRequest, HttpStatus.OK);
    }

    @GetMapping("manager-approved-list/{mgrId}")
    public ResponseEntity<List<EmployeeRequest>> getManagerApprovedRequestView(@PathVariable long mgrId) {
        List<EmployeeRequest> employeeRequestList = employeeRequestService.getRequests();
        List<EmployeeRequest> employeeRequestList1 = new ArrayList<>();
        for (EmployeeRequest e : employeeRequestList) {
            if (e.getMaterialsStatus().equals(MaterialsEnum.Approved) && e.getManagerId() == mgrId) {
                employeeRequestList1.add(e);
            }
        }

        return new ResponseEntity<>(employeeRequestList1, HttpStatus.OK);
    }

    @GetMapping("manager-approved-list")
    public ResponseEntity<List<EmployeeRequest>> getManagerApprovedRequestViewMgr() {
        List<EmployeeRequest> employeeRequestList = employeeRequestService.getRequests();
        List<EmployeeRequest> employeeRequestList1 = new ArrayList<>();
        for (EmployeeRequest e : employeeRequestList) {
            if (e.getMaterialsStatus().equals(MaterialsEnum.Approved)) {
                employeeRequestList1.add(e);
            }
        }

        return new ResponseEntity<>(employeeRequestList1, HttpStatus.OK);
    }

    @GetMapping("manager-reject-list/{mgrId}")
    public ResponseEntity<List<EmployeeRequest>> getManagerRejectRequestView(@PathVariable long mgrId) {
        List<EmployeeRequest> employeeRequestList = employeeRequestService.getRequests();
        List<EmployeeRequest> employeeRequestList1 = new ArrayList<>();
        for (EmployeeRequest e : employeeRequestList) {
            if (e.getMaterialsStatus().equals(MaterialsEnum.Rejected) && e.getManagerId() == mgrId) {
                employeeRequestList1.add(e);
            }
        }

        return new ResponseEntity<>(employeeRequestList1, HttpStatus.OK);
    }

    @GetMapping("manager-reject-list")
    public ResponseEntity<List<EmployeeRequest>> getManagerRejectRequestViewMgr() {
        List<EmployeeRequest> employeeRequestList = employeeRequestService.getRequests();
        List<EmployeeRequest> employeeRequestList1 = new ArrayList<>();
        for (EmployeeRequest e : employeeRequestList) {
            if (e.getMaterialsStatus().equals(MaterialsEnum.Rejected)) {
                employeeRequestList1.add(e);
            }
        }
        return new ResponseEntity<>(employeeRequestList1, HttpStatus.OK);
    }
}
