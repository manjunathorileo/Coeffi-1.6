package com.dfq.coeffi.vivo.repository;

import com.dfq.coeffi.vivo.entity.Slot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import javax.transaction.Transactional;

@EnableJpaRepositories
@Transactional
public interface SlotRepository extends JpaRepository<Slot,Long> {

}
