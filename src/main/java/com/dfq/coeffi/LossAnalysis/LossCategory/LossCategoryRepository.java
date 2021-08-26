package com.dfq.coeffi.LossAnalysis.LossCategory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional
public interface LossCategoryRepository extends JpaRepository<LossCategory,Long> {
    Optional<LossCategory> findById(long id);
}
