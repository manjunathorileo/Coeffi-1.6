package com.dfq.coeffi.SOPDetails.SOPCategory;


import com.dfq.coeffi.SOPDetails.SOPType.SopType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.List;
import java.util.Optional;

@EnableJpaRepositories
public interface SopCategoryRepository extends JpaRepository<SopCategory,Long>
{
    Optional<SopCategory> findById(long id);

    List<SopCategory> findBySopType(SopType sopType);
}