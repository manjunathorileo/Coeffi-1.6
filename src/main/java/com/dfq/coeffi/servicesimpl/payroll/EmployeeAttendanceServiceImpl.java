package com.dfq.coeffi.servicesimpl.payroll;

import java.util.*;

import static java.util.Optional.ofNullable;

import com.dfq.coeffi.entity.hr.employee.AttendanceStatus;
import com.dfq.coeffi.entity.hr.employee.EmployeeType;
import com.dfq.coeffi.entity.payroll.EmployeeAttendance;
import com.dfq.coeffi.repository.payroll.EmployeeAttendanceRepository;
import com.dfq.coeffi.service.payroll.EmployeeAttendanceService;
import com.dfq.coeffi.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EmployeeAttendanceServiceImpl implements EmployeeAttendanceService {

    private final EmployeeAttendanceRepository employeeAttendanceRepository;

    @Autowired
    public EmployeeAttendanceServiceImpl(EmployeeAttendanceRepository employeeAttendanceRepository) {
        this.employeeAttendanceRepository = employeeAttendanceRepository;
    }

    @Override
    public EmployeeAttendance createEmployeeAttendance(EmployeeAttendance employeeAttendance, boolean changed) {
        employeeAttendance.setDataProcessed(changed);
        return employeeAttendanceRepository.save(employeeAttendance);
    }

    @Override
    public Optional<EmployeeAttendance> getEmployeeAttendance(long id) {
        return ofNullable(employeeAttendanceRepository.findOne(id));
    }

    @Override
    public List<EmployeeAttendance> getTodayMarkedEmployeeAttendance(Date todayDate) {
        return employeeAttendanceRepository.findByMarkedOn(todayDate);
    }

    @Override
    public List<EmployeeAttendance> getAllEmployeeAttendance() {
        return employeeAttendanceRepository.findAll();
    }

    @Override
    public void deleteEmployeeAttendance(long id) {
        employeeAttendanceRepository.delete(id);
    }


    @Override
    public List<EmployeeAttendance> getEmployeetWeekAttendance(Date startDate, Date endDate) {
        return employeeAttendanceRepository.getEmployeeAttendanceBetweenDate(startDate, endDate);
    }

    @Override
    public List<EmployeeAttendance> getEmployeeMontlyAttendanceByEmployeeId(Date startDate, Date endDate, long employeeId) {
        return employeeAttendanceRepository.getEmployeeAttendanceBetweenDateByEmployeeId(startDate, endDate, employeeId);
    }

    @Override
    public List<EmployeeAttendance> getTodayMarkedEmployeeAttendanceByDeptIdAndDesgId(Date todayDate, long departmentId, long designationId) {
        return employeeAttendanceRepository.findAttendanceByMarkedOnAndDepartmentIdAndDesignationId(todayDate, departmentId, designationId);
    }

//    @Override
//    public List<EmployeeAttendance> getEmployeeAttendanceByEmployeeIdAndStatus(long employeeId, AttendanceStatus attendanceStatus, Date startDate, Date endDate) {
//        return employeeAttendanceRepository.getEmployeeAttendance(employeeId, attendanceStatus, startDate, endDate);
//    }

    @Override
    public List<EmployeeAttendance> getEmployeeAttendanceByEmployeeIdAndStatus(long employeeId, AttendanceStatus attendanceStatus, Date startDate, Date endDate) {
        List<EmployeeAttendance> employeeAttendanceList = new ArrayList<>();
        List<Date> dates = DateUtil.getDaysBetweenDates(startDate, endDate);
        for (Date date : dates) {
            EmployeeAttendance employeeAttendance = getEmployeeAttendanceByEmployeeId(date, employeeId);
            if (employeeAttendance != null) {
                if (employeeAttendance.getAttendanceStatus().equals(attendanceStatus)) {
                    employeeAttendanceList.add(employeeAttendance);
                }
            }
        }

        return employeeAttendanceList;
    }

    @Override
    public EmployeeAttendance getEmployeeAttendanceByEmployeeIdAndStatus(long employeeId, AttendanceStatus attendanceStatus, Date todayDate) {
        return employeeAttendanceRepository.getEmployeeAttendanceByEmployeeIdAndStatus(employeeId, attendanceStatus, todayDate);
    }

    @Override
    public EmployeeAttendance getEmployeeAttendanceByEmployeeId(Date startDate, long employeeId) {
        List<EmployeeAttendance> employeeAttendances = employeeAttendanceRepository.getEmployeeAttendanceByEmployeeId(startDate, employeeId);
//        Collections.reverse(employeeAttendances);
        if (employeeAttendances.isEmpty()) {
            return null;
        }

//        if (employeeAttendances.size() > 1) {
//            for (int i = 0; i < (employeeAttendances.size() - 1); i++) {
//                employeeAttendanceRepository.delete(employeeAttendances.get(i));
//            }
//        }
        return employeeAttendances.get(0);
    }


    @Override
    public List<EmployeeAttendance> getEmployeeAttendanceByDepartment(Date startDate, long departmentId) {
        return employeeAttendanceRepository.getEmployeeAttendanceByDepartment(startDate, departmentId);
    }

	/*@Override
	public List<EmployeeAttendance> getTodayMarkedEmployeeAttendanceReport(Date todayDate,int monthName,EmployeeType employeeType, long departmentId) {
		return employeeAttendanceRepository.getTodayMarkedEmployeeAttendanceReport(todayDate,monthName,employeeType,departmentId);
	}
*/

}