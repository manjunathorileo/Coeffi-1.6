package com.dfq.coeffi.LossAnalysis.LossSubCategory;

import com.dfq.coeffi.LossAnalysis.LossCategory.LossCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


@Transactional
public interface LossSubCategoryRepository extends JpaRepository<LossSubCategory,Long> {

    List<LossSubCategory> findByLossCategory(LossCategory lossCategory);

    Optional<LossSubCategory> findById(long id);
}
