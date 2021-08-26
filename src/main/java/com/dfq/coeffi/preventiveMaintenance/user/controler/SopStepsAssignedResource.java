package com.dfq.coeffi.preventiveMaintenance.user.controler;

import com.dfq.coeffi.controller.BaseController;
import com.dfq.coeffi.entity.hr.employee.Employee;
import com.dfq.coeffi.preventiveMaintenance.user.entity.SopStepsAssigned;
import com.dfq.coeffi.preventiveMaintenance.user.entity.SopStepsAssignedDocumentUpload;
import com.dfq.coeffi.preventiveMaintenance.user.service.SopStepsAssignedDocumentUploadService;
import com.dfq.coeffi.preventiveMaintenance.user.service.SopStepsAssignedService;
import com.dfq.coeffi.service.hr.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.security.Principal;
import java.util.Date;
import java.util.Optional;

@RestController
@Slf4j
public class SopStepsAssignedResource extends BaseController {

    private final SopStepsAssignedService sopStepsAssignedService;
    private final EmployeeService employeeService;
    private final SopStepsAssignedDocumentUploadService sopStepsAssignedDocumentUploadService;

    @Autowired
    public SopStepsAssignedResource(SopStepsAssignedService sopStepsAssignedService, EmployeeService employeeService, SopStepsAssignedDocumentUploadService sopStepsAssignedDocumentUploadService) {
        this.sopStepsAssignedService = sopStepsAssignedService;
        this.employeeService = employeeService;
        this.sopStepsAssignedDocumentUploadService = sopStepsAssignedDocumentUploadService;
    }

    @PostMapping("/sop-steps-assigned/{loggerId}")
    public ResponseEntity<SopStepsAssigned> createCheckListAssigned(@Valid @RequestBody SopStepsAssigned sopStepsAssigned, @PathVariable long loggerId, Principal principal){
        Date today = new Date();
        Optional<Employee> loggerOptional = employeeService.getEmployee(loggerId);
        Optional<SopStepsAssigned> checkListAssignedOptional = sopStepsAssignedService.getCheckListAssigned(sopStepsAssigned.getId());
        SopStepsAssignedDocumentUpload sopStepsAssignedDocumentUpload = sopStepsAssignedDocumentUploadService.getDocumentFileById(sopStepsAssigned.getSopStepsAssignedDocumentUpload().getId());
        SopStepsAssigned sopStepsAssignedObj = checkListAssignedOptional.get();
        sopStepsAssignedObj.setCheckPointStatus(sopStepsAssigned.getCheckPointStatus());
        sopStepsAssignedObj.setRemark(sopStepsAssigned.getRemark());
        sopStepsAssignedObj.setSubmitedOn(today);
        sopStepsAssignedObj.setSubmitedBy(loggerOptional.get());
        sopStepsAssignedObj.setSopStepsAssignedDocumentUpload(sopStepsAssignedDocumentUpload);
        SopStepsAssigned sopStepsAssignedUpdate = sopStepsAssignedService.createCheckListAssigned(sopStepsAssignedObj);
        return new ResponseEntity<>(sopStepsAssignedUpdate, HttpStatus.OK);
    }
}
