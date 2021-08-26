package com.dfq.coeffi.repository.communication;

import com.dfq.coeffi.entity.communication.Circular;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.List;

public interface CircularRepository extends JpaRepository<Circular, Long> {
    List<Circular> findByFirstManagerAndApproveStatus(long managerId, boolean approveStatus);
    List<Circular> findByApproveStatus(boolean approveStatus);
}