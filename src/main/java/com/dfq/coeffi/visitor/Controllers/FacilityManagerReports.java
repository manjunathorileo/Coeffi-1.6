package com.dfq.coeffi.visitor.Controllers;

import com.dfq.coeffi.controller.BaseController;
import com.dfq.coeffi.entity.hr.employee.Employee;
import com.dfq.coeffi.visitor.Entities.EmployeeDto;
import com.dfq.coeffi.visitor.Entities.Visitor;
import com.dfq.coeffi.visitor.Entities.VisitorCategory;
import com.dfq.coeffi.visitor.Entities.VisitorDto;
import com.dfq.coeffi.visitor.Services.VisitorCategoryService;
import com.dfq.coeffi.visitor.Services.VisitorService;
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

@RestController
public class FacilityManagerReports extends BaseController {
    @Autowired
    VisitorService visitorService;

    private long count;

    @GetMapping("facility-manager-reports/visitor-count")
    public ResponseEntity<Long> getAllVisitorCount() {
        List<Visitor> visitor = visitorService.getByMobileDate(new Date());
        count = visitor.size();
        return new ResponseEntity<>(count, HttpStatus.OK);
    }

    @PostMapping("facility-manager-reports/dept-wise-type-of-visit/{deptName}")
    public ResponseEntity<VisitorDto> getAllVisitorDepartment(@PathVariable("deptName") String deptNname) {

        VisitorDto visitorDtos = new VisitorDto();
        List<Visitor> visitor = visitorService.getByMobileDate(new Date());
        long officialCount = 0;
        long nonOfficial = 0;
        long casual = 0;
        List visitor3 = new ArrayList<>();

        for (Visitor visitor1 : visitor) {
            if (visitor1.getVisitorPass().getDepartmentName().equalsIgnoreCase(deptNname)) {

                if (visitor1.getVisitType().equalsIgnoreCase("Official")) {
                    officialCount++;
                } else if (visitor1.getVisitType().equalsIgnoreCase("Non-Official")) {
                    nonOfficial++;
                } else if (visitor1.getVisitType().equalsIgnoreCase("Casual")) {
                    casual++;
                }
            }
        }
        visitorDtos.setOfficialCount(officialCount);
        visitorDtos.setNonOfficial(nonOfficial);
        visitorDtos.setCasual(casual);

        return new ResponseEntity<>(visitorDtos, HttpStatus.OK);
    }


    @PostMapping("facility-manager-reports/within-time-exit/{deptName}")
    public ResponseEntity<VisitorDto> getWithinTimeExit(@PathVariable("deptName") String deptName) {
        VisitorDto visitorDtos = new VisitorDto();
        List<Visitor> visitor = visitorService.getByMobileDate(new Date());
        long officialCount = 0;
        long nonOfficial = 0;
        long casual = 0;
        for (Visitor visitor1 : visitor) {
            if (visitor1.getVisitorPass().getDepartmentName().equalsIgnoreCase(deptName)) {
                if (visitor1.getExtraTime() == null) {
                    if (visitor1.getVisitType().equalsIgnoreCase("Official")) {
                        officialCount++;
                    } else if (visitor1.getVisitType().equalsIgnoreCase("Non-Official")) {
                        nonOfficial++;
                    } else if (visitor1.getVisitType().equalsIgnoreCase("Casual")) {
                        casual++;
                    }
                }
            }
        }
        visitorDtos.setOfficialCount(officialCount);
        visitorDtos.setNonOfficial(nonOfficial);
        visitorDtos.setCasual(casual);

        return new ResponseEntity<>(visitorDtos, HttpStatus.OK);
    }


    @PostMapping("facility-manager-reports/extra-time-exit/{deptName}")
    public ResponseEntity<VisitorDto> getExtraTimeExit(@PathVariable("deptName") String deptName) {
        VisitorDto visitorDtos = new VisitorDto();
        List<Visitor> visitor = visitorService.getByMobileDate(new Date());
        long officialCount = 0;
        long nonOfficial = 0;
        long casual = 0;

        for (Visitor visitor1 : visitor) {
            if (visitor1.getVisitorPass().getDepartmentName().equalsIgnoreCase(deptName)) {
                if (visitor1.getExtraTime() != null) {
                    if (visitor1.getVisitType().equalsIgnoreCase("Official")) {
                        officialCount++;
                    } else if (visitor1.getVisitType().equalsIgnoreCase("Non Official")) {
                        nonOfficial++;
                    } else if (visitor1.getVisitType().equalsIgnoreCase("Casual")) {
                        casual++;
                    }
                }
            }
        }
        visitorDtos.setOfficialCount(officialCount);
        visitorDtos.setNonOfficial(nonOfficial);
        visitorDtos.setCasual(casual);

        return new ResponseEntity<>(visitorDtos, HttpStatus.OK);
    }

    @GetMapping("facility-manager-reports/total-visitor-checkin")
    public ResponseEntity<Long> getTotalCheckins() {
        List<Visitor> visitors = visitorService.getByMobileDate(new Date());
        List<Visitor> visitorObj = new ArrayList<>();

        for (Visitor visitor1 : visitors) {
            if (visitor1.getCheckOutTime() == null && visitor1.getCheckInTime() != null) {
                visitorObj.add(visitor1);
            }
        }
        long checkinCount = visitorObj.size();
        return new ResponseEntity<>(checkinCount, HttpStatus.OK);
    }

    @GetMapping("facility-manager-reports/total-visitor-checkout")
    public ResponseEntity<Long> getTotalCheckouts() {
        List<Visitor> visitors = visitorService.getByMobileDate(new Date());
        List<Visitor> visitorObj = new ArrayList<>();

        for (Visitor visitor1 : visitors) {
            if (visitor1.getCheckOutTime() != null && visitor1.getCheckInTime() != null) {
                visitorObj.add(visitor1);
            }
        }
        long checkinCount = visitorObj.size();
        return new ResponseEntity<>(checkinCount, HttpStatus.OK);
    }
}


