package com.dfq.coeffi.controller.hr;

import com.dfq.coeffi.controller.BaseController;
import com.dfq.coeffi.dto.DesignationDto;
import com.dfq.coeffi.entity.hr.Department;
import com.dfq.coeffi.entity.hr.Designation;
import com.dfq.coeffi.service.hr.DepartmentService;
import com.dfq.coeffi.service.hr.DesignationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.net.DatagramPacket;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@Slf4j
public class DesignationController extends BaseController {

    @Autowired
    private DesignationService designationService;

    @Autowired
    private DepartmentService departmentService;

    /**
     * @return all the designations available in the database
     */
    @GetMapping("designation")
    public ResponseEntity<List<Designation>> getDesignations() {
        List<Designation> designations = designationService.findAll();
        if (CollectionUtils.isEmpty(designations)) {
            throw new EntityNotFoundException("Designation");
        }
        return new ResponseEntity<>(designations, HttpStatus.OK);
    }

    /**
     * @param : save object to database and return the saved object
     * @return
     */

    @PostMapping("designation")
    public ResponseEntity<Designation> saveDesignation(@RequestBody DesignationDto designationList) {
        for (Designation dto : designationList.getDesignationList()) {
            Optional<Department> department = departmentService.getDepartment(dto.getDepartment().getId());
            dto.setDepartment(department.get());
            designationService.save(dto);
        }
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    /**
     * @param id
     * @return return single designation object by passing id
     */
    @GetMapping("designation/{id}")
    private ResponseEntity<Designation> getDesignation(@PathVariable long id) {
        Optional<Designation> designation = designationService.getDesignation(id);
        if (!designation.isPresent()) {
            throw new EntityNotFoundException(Designation.class.getSimpleName());
        }
        return new ResponseEntity<>(designation.get(), HttpStatus.OK);
    }

    /**
     * @param id
     * @return permanent deactivate of designation by provided id | no permanent delete
     */
    @DeleteMapping("designation/{id}")
    public ResponseEntity<Designation> deleteDesignation(@PathVariable long id) {
        Optional<Designation> designation = designationService.getDesignation(id);
        if (!designation.isPresent()) {
            throw new EntityNotFoundException(Department.class.getName());
        }
        designationService.delete(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * @param id
     * @param
     * @return to update the designation object
     */
    @PostMapping("designation/{id}")
    public ResponseEntity<Designation> updateDesignation(@PathVariable long id, @Valid @RequestBody Designation designation) {
        Optional<Designation> persistedDesignation = designationService.getDesignation(id);
        if (!persistedDesignation.isPresent()) {
            log.warn("Designation with ID {} not found", id);
            throw new EntityNotFoundException(Designation.class.getSimpleName());
        }
        designation.setId(id);
        designationService.save(designation);
        return new ResponseEntity<>(designation, HttpStatus.OK);
    }

    @GetMapping("designation/department/{departmentId}")
    private ResponseEntity<List<Designation>> getDepartmentDetails(@PathVariable long departmentId) {
        Optional<Department> department = departmentService.getDepartment(departmentId);
        if (!department.isPresent()) {
            throw new EntityNotFoundException(Department.class.getSimpleName());
        }
        List<Designation> activeDesignation = new ArrayList<>();
        List<Designation> designations = designationService.getDepartmentDetail(department.get());
        for (Designation designation : designations) {
            if (designation.isStatus()) {
                activeDesignation.add(designation);
            }
        }
        return new ResponseEntity<>(activeDesignation, HttpStatus.OK);
    }

    @GetMapping("designation/department-by-name/{departmentName}")
    private ResponseEntity<List<Designation>> getDepartmentDetailsByName(@PathVariable String departmentName) {
        Department department = departmentService.getByName(departmentName);
//        if (!department.isPresent()) {
//            throw new EntityNotFoundException(Department.class.getSimpleName());
//        }
        List<Designation> activeDesignation = new ArrayList<>();
        List<Designation> designations = designationService.getDepartmentDetail(department);
        for (Designation designation : designations) {
            if (designation.isStatus()) {
                activeDesignation.add(designation);
            }
        }
        return new ResponseEntity<>(activeDesignation, HttpStatus.OK);
    }
}