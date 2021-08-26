package com.dfq.coeffi.SOPDetails.SopDocument.doc;

import com.dfq.coeffi.SOPDetails.SOPCategory.SopCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.Optional;


@EnableJpaRepositories
public interface DocRepository extends JpaRepository<Doc,Long>
{


    Doc findBySopCategory(SopCategory sopCategory);

    Optional<Doc> findById(long fileId);
}
