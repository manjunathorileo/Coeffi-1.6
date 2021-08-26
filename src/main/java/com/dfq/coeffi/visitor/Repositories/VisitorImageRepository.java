package com.dfq.coeffi.visitor.Repositories;

import com.dfq.coeffi.visitor.Entities.VisitorDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import javax.transaction.Transactional;
import java.util.Optional;

@Transactional
@EnableJpaRepositories
public interface VisitorImageRepository extends JpaRepository<VisitorDocument,Long> {
    Optional<VisitorDocument> findById(long imageId);
}
