package com.dfq.coeffi.repository.payroll;

import javax.transaction.Transactional;

import com.dfq.coeffi.entity.payroll.payrollmaster.PayHead;
import org.springframework.data.jpa.repository.JpaRepository;


@Transactional
public interface PayHeadRepository extends JpaRepository<PayHead, Long> {

}
