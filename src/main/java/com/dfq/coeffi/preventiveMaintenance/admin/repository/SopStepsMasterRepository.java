package com.dfq.coeffi.preventiveMaintenance.admin.repository;

import com.dfq.coeffi.preventiveMaintenance.admin.entity.SopStepsMaster;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SopStepsMasterRepository extends JpaRepository<SopStepsMaster,Long> {
    Optional<SopStepsMaster> findById(long id);
}
