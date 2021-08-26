package com.dfq.coeffi.service.payroll;

import com.dfq.coeffi.entity.payroll.payrollmaster.PayHead;

import java.util.List;
import java.util.Optional;


public interface PayHeadService {
	
	PayHead createPayHead(PayHead payHead);
	List<PayHead> getAllPayHead();
	Optional<PayHead> getPayHead(long id);
	void deletePayHead(long id);

}
