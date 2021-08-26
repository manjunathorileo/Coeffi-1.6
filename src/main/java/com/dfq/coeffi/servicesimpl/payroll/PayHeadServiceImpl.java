package com.dfq.coeffi.servicesimpl.payroll;

import java.util.List;
import java.util.Optional;
import static java.util.Optional.ofNullable;
import com.dfq.coeffi.entity.payroll.payrollmaster.PayHead;
import com.dfq.coeffi.repository.payroll.PayHeadRepository;
import com.dfq.coeffi.service.payroll.PayHeadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PayHeadServiceImpl implements PayHeadService {

	private final PayHeadRepository payHeadRepository;
	
	@Autowired
	public PayHeadServiceImpl(PayHeadRepository payHeadRepository)
	{
		this.payHeadRepository = payHeadRepository;
	}
	
	@Override
	public PayHead createPayHead(PayHead payHead) {
		return payHeadRepository.save(payHead);
	}

	@Override
	public List<PayHead> getAllPayHead() {
 		return payHeadRepository.findAll();
	}

	@Override
	public Optional<PayHead> getPayHead(long id) {
 		return ofNullable(payHeadRepository.findOne(id));
	}

	@Override
	public void deletePayHead(long id) {
		payHeadRepository.delete(id);		
	}

}
