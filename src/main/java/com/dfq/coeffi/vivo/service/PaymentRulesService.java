package com.dfq.coeffi.vivo.service;

import com.dfq.coeffi.vivo.entity.PaymentRules;

import java.util.List;

public interface PaymentRulesService {
    PaymentRules save(PaymentRules paymentRules);
    PaymentRules get(long id);
    List<PaymentRules> getAll();


}
