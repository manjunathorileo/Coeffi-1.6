package com.dfq.coeffi.repository.payroll;

import com.dfq.coeffi.entity.payroll.payrollmaster.PayHeadContract;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.transaction.Transactional;

@Transactional
public interface PayHeadContractRepository extends JpaRepository<PayHeadContract, Long> {

}
