package com.dfq.coeffi.controller.payroll;

import java.util.List;
import java.util.Optional;
import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;

import com.dfq.coeffi.controller.BaseController;
import com.dfq.coeffi.entity.payroll.payrollmaster.PayHead;
import com.dfq.coeffi.service.payroll.PayHeadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import lombok.Getter;
import lombok.Setter;

@RestController
public class PayHeadController extends BaseController {
	
	@Autowired
	private PayHeadService payHeadService;
	
	@Autowired
	public PayHeadController(PayHeadService payHeadService)
	{
		this.payHeadService=payHeadService;
	}
	
	/**
	 * @return all the Employees PayHead Master List with details in the database
	 */
	@GetMapping("payHead")
	public ResponseEntity<List<PayHead>> getAllPayHeadService()
	{
		List<PayHead> payheads=payHeadService.getAllPayHead();
		if(CollectionUtils.isEmpty(payheads))
		{
			throw new EntityNotFoundException("payheads");
		}
		
		return new ResponseEntity<>(payheads,HttpStatus.OK);
	}
	
	
	/**
	 * @param payHead
	 *            : save object to database and return the saved object
	 * @return
	 */
	
	@PostMapping("payHead")
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<PayHead> createPayHead(@Valid @RequestBody PayHead payHead)
	{
		PayHead persistedObject=payHeadService.createPayHead(payHead);
		return new ResponseEntity<>(persistedObject,HttpStatus.CREATED);
	}
	
	/**
	 * @param id
	 * @param payHead
	 * @return to update the payHead object
	 */
	
	@PutMapping("payHead/{id}")
	public ResponseEntity<PayHead> update(@PathVariable long id,@Valid @RequestBody PayHead payHead)
	{
		Optional<PayHead> persistedpayHead = payHeadService.getPayHead(id);
		if (!persistedpayHead.isPresent()) 
		{
			throw new EntityNotFoundException(PayHead.class.getSimpleName());
		}
		payHead.setId(id);
		payHeadService.createPayHead(payHead);
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	/**
	 * @param id
	 * @return permanent deactivate of PayHead by provided id | no permanent delete
	 */
	
	@DeleteMapping("payHead/{id}")
	public ResponseEntity<PayHead> deletePayHead(@PathVariable long id)
	{
		Optional<PayHead> payHead=payHeadService.getPayHead(id);
		if(!payHead.isPresent())
		{
			throw new EntityNotFoundException(PayHead.class.getName());
		}
		payHeadService.deletePayHead(id);
		return new ResponseEntity<>(HttpStatus.OK);
	}

}
