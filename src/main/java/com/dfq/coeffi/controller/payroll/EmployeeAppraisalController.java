package com.dfq.coeffi.controller.payroll;

import java.util.List;
import java.util.Optional;
import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import com.dfq.coeffi.controller.BaseController;
import com.dfq.coeffi.entity.hr.employee.Employee;
import com.dfq.coeffi.entity.master.AcademicYear;
import com.dfq.coeffi.entity.payroll.EmployeeAppraisal;
import com.dfq.coeffi.service.AcademicYearService;
import com.dfq.coeffi.service.hr.EmployeeService;
import com.dfq.coeffi.service.payroll.EmployeeAppraisalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.util.CollectionUtils;

@RestController
public class EmployeeAppraisalController extends BaseController
{
	@Autowired
	private EmployeeAppraisalService employeeAppraisalService;
	private EmployeeService employeeService;
	private AcademicYearService academicYearService;
	
	@Autowired
	public EmployeeAppraisalController(EmployeeAppraisalService employeeAppraisalService,EmployeeService employeeService,
			AcademicYearService academicYearService)
	{
		this.employeeAppraisalService = employeeAppraisalService;
		this.employeeService = employeeService;
		this.academicYearService = academicYearService;
	}
	
	
	/**
	 * @return all the Employees Appraisal List with details in the database
	 */
	
	@GetMapping("employeeAppraisal")
	public ResponseEntity<List<EmployeeAppraisal>> listAllEmployeeAppraisal()
	{
		List<EmployeeAppraisal> employeeAppraisalLists=employeeAppraisalService.listAllEmployeeAppraisal();
		if(CollectionUtils.isEmpty(employeeAppraisalLists))
		{
			throw new EntityNotFoundException("employeeAppraisalLists");
		}
		return new ResponseEntity<>(employeeAppraisalLists,HttpStatus.OK);
	}
	
	/**
	 * @param employeeAppraisal
	 *            : save object to database and return the saved object
	 * @return
	 */
	
	@PostMapping("employeeAppraisal")
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<EmployeeAppraisal> createEmployeeAppraisal(@Valid @RequestBody EmployeeAppraisal employeeAppraisal)
	{
		if(employeeAppraisal.getEmployee() != null)
		{
			Optional<Employee> employee=employeeService.getEmployee(employeeAppraisal.getEmployee().getId());
			employeeAppraisal.setEmployee(employee.get());
		}
		
		if(employeeAppraisal.getAcademicYear()!= null)
		{
			Optional<AcademicYear> academicYear =academicYearService.getAcademicYear(employeeAppraisal.getAcademicYear().getId());
			employeeAppraisal.setAcademicYear(academicYear.get());
		}
		EmployeeAppraisal persistedObject = employeeAppraisalService.createEmployeeAppraisal(employeeAppraisal);
		return new ResponseEntity<>(persistedObject,HttpStatus.CREATED);
	}
	
	/**
	 * @param id
	 * @param employeeAppraisal
	 * @return to update the employeeAppraisal object
	 */
	
	@PutMapping("employeeAppraisal/{id}")
	public ResponseEntity<EmployeeAppraisal> update(@PathVariable long id,@Valid @RequestBody EmployeeAppraisal employeeAppraisal)
	{
		Optional<EmployeeAppraisal> persistedemployeeAppraisal = employeeAppraisalService.getEmployeeAppraisal(id);
		if (!persistedemployeeAppraisal.isPresent()) 
		{
			throw new EntityNotFoundException(EmployeeAppraisal.class.getSimpleName());
		}
		employeeAppraisal.setId(id);
		employeeAppraisalService.createEmployeeAppraisal(employeeAppraisal);
		return new ResponseEntity<>(employeeAppraisal,HttpStatus.OK);
	}
	
	/**
	 * @param id
	 * @return permanent deactivate of EmployeeAppraisal by provided id | no permanent delete
	 */
	
	@DeleteMapping("employeeAppraisal/{id}")
	public ResponseEntity<EmployeeAppraisal> deleteEmployeeAppraisal(@PathVariable long id)
	{
		Optional<EmployeeAppraisal> employeeAppraisal=employeeAppraisalService.getEmployeeAppraisal(id);
		if(!employeeAppraisal.isPresent())
		{
			throw new EntityNotFoundException(EmployeeAppraisal.class.getName());
		}
		employeeAppraisalService.deleteEmployeeAppraisal(id);
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
