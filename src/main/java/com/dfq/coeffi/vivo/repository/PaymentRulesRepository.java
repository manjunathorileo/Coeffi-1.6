package com.dfq.coeffi.vivo.repository;

import com.dfq.coeffi.vivo.entity.PaymentRules;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories
public interface PaymentRulesRepository extends JpaRepository<PaymentRules,Long> {

}
