package com.dfq.coeffi.preventiveMaintenance.user.repository;

import com.dfq.coeffi.preventiveMaintenance.user.entity.SopStepsAssigned;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional
public interface SopStepsAssignedRepository extends JpaRepository<SopStepsAssigned, Long> {
    Optional<SopStepsAssigned> findById(long id);
}
