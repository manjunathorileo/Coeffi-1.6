package com.dfq.coeffi.SOPDetails.SOPType;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.Optional;

@EnableJpaRepositories
public interface SopTypeRepository extends JpaRepository<SopType,Long> {
    Optional<SopType> findById(long id);
}
