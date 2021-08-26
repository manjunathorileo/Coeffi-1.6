package com.dfq.coeffi.preventiveMaintenance.user.repository;

import com.dfq.coeffi.SOPDetails.SOPCategory.SopCategory;
import com.dfq.coeffi.preventiveMaintenance.user.entity.SopStepsAssignedDocumentUpload;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


@EnableJpaRepositories
@Transactional
public interface SopStepsAssignedDocumentUploadRepository extends JpaRepository<SopStepsAssignedDocumentUpload,Long>
{
    @Query("SELECT e FROM SopStepsAssignedDocumentUpload e where e.sopCategory.id=:sopId AND e.sopType.id =:sopTypeId")
    List<SopStepsAssignedDocumentUpload> getCheckListAssignedDocumentUploadByTwoIds(@Param("sopId") long SopId, @Param("sopTypeId") long sopTypeId);

    SopStepsAssignedDocumentUpload findBySopCategory(SopCategory sopCategory);

    SopStepsAssignedDocumentUpload findById(long fileId);
}
