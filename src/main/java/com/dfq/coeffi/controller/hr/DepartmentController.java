package com.dfq.coeffi.controller.hr;

import com.dfq.coeffi.controller.BaseController;
import com.dfq.coeffi.dto.DepartmentDto;
import com.dfq.coeffi.employeePermanentContract.entities.EmpPermanentContract;
import com.dfq.coeffi.employeePermanentContract.repositories.PermanentContractRepo;
import com.dfq.coeffi.entity.hr.Department;
import com.dfq.coeffi.entity.hr.employee.Employee;
import com.dfq.coeffi.service.hr.DepartmentService;
import com.dfq.coeffi.service.hr.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.util.*;

@RestController
@Slf4j
public class DepartmentController extends BaseController {

    @Autowired
    private DepartmentService departmentService;
    @Autowired
    EmployeeService employeeService;
    @Autowired
    PermanentContractRepo permanentContractRepo;

    /**
     * @return all the departments available in the database
     */
    @GetMapping("department")
    public ResponseEntity<List<Department>> getDepartments() {
        List<Department> departments = departmentService.findAll();
        if (CollectionUtils.isEmpty(departments)) {
            throw new EntityNotFoundException("Department");
        }
        return new ResponseEntity<>(departments, HttpStatus.OK);
    }

    /**
     * @param : save object to database and return the saved object
     * @return
     */

    @PostMapping("department")
    public ResponseEntity<Department> saveDepartment(@RequestBody DepartmentDto departmentList) throws Exception {
        for (Department dto : departmentList.getDepartmentList()) {
            System.out.println("NAME: " + dto.getName());
            Optional<Department> checkdept = departmentService.isDepartmentExists(dto.getName());
            if (checkdept.isPresent()) {
                throw new Exception("Department Already Exists");
            } else {
                departmentService.save(dto);
            }
        }
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    /**
     * @param id
     * @return return single department object by passing id
     */
    @GetMapping("department/{id}")
    private ResponseEntity<Department> getDepartment(@PathVariable long id) {
        Optional<Department> department = departmentService.getDepartment(id);
        if (!department.isPresent()) {
            throw new EntityNotFoundException(Department.class.getSimpleName());
        }
        return new ResponseEntity<>(department.get(), HttpStatus.OK);
    }

    /**
     * @param id
     * @return permanent deactivate of department by provided id | no permanent delete
     */
    @DeleteMapping("department/{id}")
    public ResponseEntity<Department> deleteDepartment(@PathVariable long id) {
        Optional<Department> department = departmentService.getDepartment(id);
        if (!department.isPresent()) {
            throw new EntityNotFoundException(Department.class.getName());
        }
        departmentService.delete(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * @param id
     * @param
     * @return to update the expense object
     */
    @PostMapping("department/{id}")
    public ResponseEntity<Department> updateDepartment(@PathVariable long id, @Valid @RequestBody Department department) {
        Optional<Department> persistedDepartment = departmentService.getDepartment(id);
        if (!persistedDepartment.isPresent()) {
            log.warn("Department with ID {} not found", id);
            throw new EntityNotFoundException(Department.class.getSimpleName());
        }
        department.setId(id);
        departmentService.save(department);
        return new ResponseEntity<>(department, HttpStatus.OK);
    }

    @GetMapping("autocreate-dep")
    public ResponseEntity<List<Department>> autoCreateDepartment(){
        List<EmpPermanentContract> employees = permanentContractRepo.findAll();
//        List<Employee> employees = employeeService.findAll();
        List<Department> departments = new ArrayList<>();
        for (EmpPermanentContract employee:employees){
            Department department = departmentService.getByName(employee.getDepartmentName());
            if (department==null){
                Department department1 = new Department();
                department1.setName(employee.getDepartmentName());
                department1.setStatus(true);
                departmentService.save(department1);
                departments.add(department1);
            }
        }
        return new ResponseEntity<>(departments,HttpStatus.OK);
    }
}







