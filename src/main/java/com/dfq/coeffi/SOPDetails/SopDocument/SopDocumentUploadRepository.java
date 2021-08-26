package com.dfq.coeffi.SOPDetails.SopDocument;

import com.dfq.coeffi.SOPDetails.SOPCategory.SopCategory;
import com.dfq.coeffi.SOPDetails.SOPType.SopType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


@EnableJpaRepositories
public interface SopDocumentUploadRepository extends JpaRepository<SopDocumentUpload, Long> {

    @Query("SELECT e FROM SopDocumentUpload e where e.sopType.id=:sopId AND e.sopCategory.id =:sopTypeId")
    List<SopDocumentUpload> getDocByTwoIds(@Param("sopId") long SopId, @Param("sopTypeId") long sopTypeId);

    List<SopDocumentUpload> findBySopCategory(SopCategory sopCategory);

    Optional<SopDocumentUpload> findById(long fileId);

    @Query("SELECT e FROM SopDocumentUpload e where e.sopType.id = :sopTypeId AND e.sopCategory.id = :sopCategoryId AND e.sopDocumentType = :sopDocumentType")
    List<SopDocumentUpload> findBySopTypeBySopCategory(@Param("sopTypeId") long sopTypeId, @Param("sopCategoryId") long sopCategoryId, @Param("sopDocumentType") SopDocumentType sopDocumentType);
}
