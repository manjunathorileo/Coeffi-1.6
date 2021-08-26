package com.dfq.coeffi.SOPDetails.SopDocument.audio;

import com.dfq.coeffi.SOPDetails.SOPCategory.SopCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.Optional;


@EnableJpaRepositories
public interface AudioRepository extends JpaRepository<Audio,Long>
{
    Audio findBySopCategory(SopCategory sopCategory);


    Optional<Audio> findById(long fileId);

    void deleteById(long id);
}

