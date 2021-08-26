package com.dfq.coeffi.servicesimpl.payroll;

import com.dfq.coeffi.entity.payroll.payrollmaster.PayHeadContract;
import com.dfq.coeffi.repository.payroll.PayHeadContractRepository;
import com.dfq.coeffi.service.payroll.PayHeadContractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
public class PayHeadContractServiceImpl implements PayHeadContractService {

    private final PayHeadContractRepository payHeadContractRepository;

    @Autowired
    public PayHeadContractServiceImpl(PayHeadContractRepository payHeadContractRepository) {
        this.payHeadContractRepository = payHeadContractRepository;
    }

    @Override
    public PayHeadContract createPayHeadNonTeaching(PayHeadContract payHeadContract) {
        return payHeadContractRepository.save(payHeadContract);
    }

    @Override
    public List<PayHeadContract> getAllPayHeadNonTeaching() {
        return payHeadContractRepository.findAll();
    }

    @Override
    public Optional<PayHeadContract> getPayHeadNonTeaching(long id) {
        return Optional.ofNullable(payHeadContractRepository.findOne(id));
    }

    @Override
    public void deletePayHeadNonTeaching(long id) {
        payHeadContractRepository.delete(id);
    }
}
