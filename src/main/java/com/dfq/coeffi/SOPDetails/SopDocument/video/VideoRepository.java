package com.dfq.coeffi.SOPDetails.SopDocument.video;

import com.dfq.coeffi.SOPDetails.SOPCategory.SopCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.Optional;


@EnableJpaRepositories
public interface VideoRepository extends JpaRepository<Video,Long>
{
    Video findBySopCategory(SopCategory sopCategory);
    Optional<Video> findById(long fileId);
}