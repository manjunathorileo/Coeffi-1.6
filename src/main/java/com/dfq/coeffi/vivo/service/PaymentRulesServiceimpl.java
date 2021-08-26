package com.dfq.coeffi.vivo.service;

import com.dfq.coeffi.vivo.entity.PaymentRules;
import com.dfq.coeffi.vivo.repository.PaymentRulesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PaymentRulesServiceimpl implements PaymentRulesService {
    @Autowired
    PaymentRulesRepository paymentRulesRepository;
    @Override
    public PaymentRules save(PaymentRules paymentRules) {
        return paymentRulesRepository.save(paymentRules);
    }

    @Override
    public PaymentRules get(long id) {
        return paymentRulesRepository.findOne(id);
    }

    @Override
    public List<PaymentRules> getAll() {
        return paymentRulesRepository.findAll();
    }
}
