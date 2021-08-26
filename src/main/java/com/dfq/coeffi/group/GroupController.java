package com.dfq.coeffi.group;

import com.dfq.coeffi.controller.BaseController;
import com.dfq.coeffi.entity.hr.employee.Employee;
import com.dfq.coeffi.service.hr.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @Author : H Kapil Kumar
 * @Since : Dec-18
 * @Description : Class which provides the group related APIs
 */
@RestController
public class GroupController extends BaseController {

    @Autowired
    private GroupService groupService;

    @Autowired
    private EmployeeService employeeService;


    /**
     * @return list of active groups
     */
    @GetMapping("group")
    public ResponseEntity<List<Group>> getActiveGroups() {
        List<Group> groups = groupService.listAllGroups();
        if (CollectionUtils.isEmpty(groups)) {
            throw new EntityNotFoundException("groups");
        }
        return new ResponseEntity<>(groups, HttpStatus.OK);
    }

    /**
     * @param groupDto
     * @return create the new group and return the newly created group
     */
    @PostMapping("group")
    public ResponseEntity<Group> createNewGroups(@Valid @RequestBody GroupDto groupDto) {
        Group group = toGroupEntity(groupDto);
        Group persistedObject = groupService.createNewGroup(group);
        return new ResponseEntity<>(persistedObject, HttpStatus.CREATED);
    }

    /**
     * @param groupDto
     * @return update the group
     */
    @PutMapping("group")
    public ResponseEntity<Group> updateGroup(@Valid @RequestBody GroupDto groupDto) {
        Group persistedGroup = groupService.getGroup(groupDto.getId());
        if(persistedGroup != null){
            persistedGroup.setTitle(groupDto.getTitle());
            persistedGroup.setDescription(groupDto.getDescription());

            if(groupDto.getGroupEmployeeLeadId() > 0){
                Optional<Employee> employeeObj = employeeService.getEmployee(groupDto.getGroupEmployeeLeadId());
                Employee employee = employeeObj.get();
                persistedGroup.setEmployeeGroupLead(employee);
            }
        }
        Group updatedObject = groupService.createNewGroup(persistedGroup);
        return new ResponseEntity<>(updatedObject, HttpStatus.CREATED);
    }

    /**
     * @param groupDto
     * @return create the new group and retured the newly created group
     */
    @PostMapping("assign-members-group")
    public ResponseEntity<Group> assignNewMembersToGroup(@Valid @RequestBody GroupDto groupDto) {
        List<Employee> employees = null;
        Group persistedGroup = null;
        
        Group group = groupService.getGroup(groupDto.getId());
        if(group != null){
            if(groupDto.getEmployeeIds() != null && groupDto.getEmployeeIds().size() > 0){
                if(group.getGroupEmployees() != null){
                    employees = group.getGroupEmployees();
                }else{
                    employees = new ArrayList<Employee>();
                }
                for(Long empId : groupDto.getEmployeeIds()){
                    Optional<Employee> employeeObj = employeeService.getEmployee(empId);
                    if(!employeeObj.isPresent()){
                        throw  new EntityNotFoundException("Employee- From - Group");
                    }
                    employees.add(employeeObj.get());
                }
                group.setGroupEmployees(employees);
            }

            persistedGroup = groupService.createNewGroup(group);
        }
        
        return new ResponseEntity<>(persistedGroup, HttpStatus.CREATED);
    }
    
    /**
     * @param id
     * @return get the group by id
     */
    @GetMapping("group/{id}")
    private ResponseEntity<Group> getGroup(@PathVariable long id) {
        Group group = groupService.getGroup(id);
        return new ResponseEntity<>(group, HttpStatus.OK);
    }

    /**
     * @param id
     * @return get the group by id
     */
    @DeleteMapping("group/{id}")
    private ResponseEntity<Group> deleteGroup(@PathVariable long id) {
        Group group = groupService.getGroup(id);
        group.setStatus(false);
        groupService.createNewGroup(group);
        return new ResponseEntity<>(group, HttpStatus.OK);
    }

    @GetMapping("remove-member-group/{id}/{refId}/{refName}")
    private ResponseEntity<Group> removeMemberFromGroup(@PathVariable long id, @PathVariable long refId, @PathVariable String refName) {
        Group group = groupService.getGroup(id);
        Group persistedGroup = null;
        if(group != null){
            if(refName.equalsIgnoreCase("EMPLOYEE")){
                List<Employee> employees = group.getGroupEmployees();
                for (Employee employee : employees) {
                    if(employee.getId() == refId){
                        employees.remove(employee);
                        break;
                    }
                }
                group.setGroupEmployees(employees);
            }

            persistedGroup = groupService.createNewGroup(group);
        }
        return new ResponseEntity<>(persistedGroup, HttpStatus.OK);
    }
    // Assign members to the group

    // Remove members from the group

    // Sending communications to the groups


    // Method to convert dto to entity class
    private Group toGroupEntity(GroupDto groupDto){
        Group group = new Group();
        group.setTitle(groupDto.getTitle());
        group.setDescription(groupDto.getDescription());
        group.setStatus(true);

        // Adding employees to the group object from dto
        if(groupDto != null && groupDto.getEmployeeIds() != null && groupDto.getEmployeeIds().size() > 0){
            List<Employee> employees = new ArrayList<Employee>();
            for(Long empId : groupDto.getEmployeeIds()){
                Optional<Employee> employeeObj = employeeService.getEmployee(empId);
                if(!employeeObj.isPresent()){
                    throw  new EntityNotFoundException("Employee- From - Group");
                }
                employees.add(employeeObj.get());
                System.out.println("EMP ID " + empId);
            }

            group.setGroupEmployees(employees);
        }

        // Setting group employee lead if any
        if(groupDto.getGroupEmployeeLeadId() > 0){
            Optional<Employee> employeeObj = employeeService.getEmployee(groupDto.getGroupEmployeeLeadId());
            if(!employeeObj.isPresent()){
                throw  new EntityNotFoundException("Employee- From - Group");
            }
            Employee employee = employeeObj.get();
            group.setEmployeeGroupLead(employee);
        }


        return group;
    }
}