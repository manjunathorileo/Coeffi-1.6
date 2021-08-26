package com.dfq.coeffi.sam.module;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ModuleRepository extends JpaRepository<Module, Long> {

    List<Module> findByStatus(boolean status);
}