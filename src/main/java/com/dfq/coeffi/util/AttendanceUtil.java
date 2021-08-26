package com.dfq.coeffi.util;

import com.dfq.coeffi.dto.EmployeeAttendanceDto;
import com.dfq.coeffi.dto.EmployeeAttendanceSheetDto;
import com.dfq.coeffi.entity.hr.employee.Employee;
import com.dfq.coeffi.entity.payroll.EmployeeAttendance;

import java.util.*;

public class AttendanceUtil {

    public static List<EmployeeAttendanceSheetDto> employeeAttendanceWeeklyReport(List<EmployeeAttendance> employeeAttendances){

        List<EmployeeAttendanceDto> aDtos = new ArrayList<EmployeeAttendanceDto>();
        List<Employee> employees =  new ArrayList<Employee>();

        for(EmployeeAttendance atd : employeeAttendances){
            EmployeeAttendanceDto dto = new EmployeeAttendanceDto();
            dto.setAttendanceStatus(atd.getAttendanceStatus());
            dto.setMarkedOn(atd.getMarkedOn());
            dto.setEmployeeId(atd.getEmployee().getId());
            dto.setEmployeeName(atd.getEmployee().getFirstName()+" "+atd.getEmployee().getLastName());
            dto.setDay(DateUtil.getDay(atd.getMarkedOn()));
            dto.setDepartmentId(atd.getEmployee().getDepartment().getId());
            dto.setDepartmentName(atd.getEmployee().getDepartment().getName());
            dto.setDesignationId(atd.getEmployee().getDesignation().getId());
            dto.setDesignationName(atd.getEmployee().getDesignation().getName());
            dto.setWorkedHours(atd.getWorkedHours());
            dto.setInTime(atd.getInTime());
            dto.setOutTime(atd.getOutTime());
            dto.setId(atd.getId());
            aDtos.add(dto);
            employees.add(atd.getEmployee());
        }
       /* Set<Employee> employeeSetObj = new HashSet<Employee>(employees);*/
        List<EmployeeAttendanceSheetDto> sheet = new ArrayList<EmployeeAttendanceSheetDto>();
        for(Employee employee : employees){
            EmployeeAttendanceSheetDto sht = new EmployeeAttendanceSheetDto();
            for(EmployeeAttendanceDto dto : aDtos){
                if(employee.getId() == dto.getEmployeeId()){
                    sht.setEmployeeName(dto.getEmployeeName());
                    sht.setEmployeeId(dto.getEmployeeId());
                    if(dto.getDay().equalsIgnoreCase("Mon")){
                        sht.setMonStatus(dto.getAttendanceStatus().toString());
                        sht.setMonWorkedHours(dto.getWorkedHours());
                        sht.setMonInTime(dto.getInTime());
                        sht.setMonOutTime(dto.getOutTime());
                        sht.setMonId(dto.getId());
                    }
                    else if(dto.getDay().equalsIgnoreCase("Tue")){
                        sht.setTueStatus(dto.getAttendanceStatus().toString());
                        sht.setTueWorkedHours(dto.getWorkedHours());
                        sht.setTueInTime(dto.getInTime());
                        sht.setTueOutTime(dto.getOutTime());
                        sht.setTueId(dto.getId());
                    }
                    else if(dto.getDay().equalsIgnoreCase("Wed")){
                        sht.setWedStatus(dto.getAttendanceStatus().toString());
                        sht.setWedWorkedHours(dto.getWorkedHours());
                        sht.setWedInTime(dto.getInTime());
                        sht.setWedOutTime(dto.getOutTime());
                        sht.setWedId(dto.getId());
                    }
                    else if(dto.getDay().equalsIgnoreCase("Thu")){
                        sht.setThuStatus(dto.getAttendanceStatus().toString());
                        sht.setThuWorkedHours(dto.getWorkedHours());
                        sht.setThuInTime(dto.getInTime());
                        sht.setThuOutTime(dto.getOutTime());
                        sht.setThuId(dto.getId());
                    }
                    else if(dto.getDay().equalsIgnoreCase("Fri")) {
                        sht.setFriStatus(dto.getAttendanceStatus().toString());
                        sht.setFriWorkedHours(dto.getWorkedHours());
                        sht.setFriInTime(dto.getInTime());
                        sht.setFriOutTime(dto.getOutTime());
                        sht.setFriId(dto.getId());
                    }
                    else if(dto.getDay().equalsIgnoreCase("Sat")){
                        sht.setSatStatus(dto.getAttendanceStatus().toString());
                        sht.setSatWorkedHours(dto.getWorkedHours());
                        sht.setSatInTime(dto.getInTime());
                        sht.setSatOutTime(dto.getOutTime());
                        sht.setSatId(dto.getId());

                    }
                    sht.setMarkedOn(dto.getMarkedOn());
                    sht.setDepartmentId(dto.getDepartmentId());
                    sht.setDepartmentName(dto.getDepartmentName());
                    sht.setDesignationId(dto.getDesignationId());
                    sht.setDesignationName(dto.getDesignationName());
                }
            }
            sheet.add(sht);
        }
        return sheet;
        }
    }
