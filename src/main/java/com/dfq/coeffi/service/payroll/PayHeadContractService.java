package com.dfq.coeffi.service.payroll;


import com.dfq.coeffi.entity.payroll.payrollmaster.PayHeadContract;

import java.util.List;
import java.util.Optional;

public interface PayHeadContractService {

	PayHeadContract createPayHeadNonTeaching(PayHeadContract payHeadContract);
	List<PayHeadContract> getAllPayHeadNonTeaching();
	Optional<PayHeadContract> getPayHeadNonTeaching(long id);
	void deletePayHeadNonTeaching(long id);

}
