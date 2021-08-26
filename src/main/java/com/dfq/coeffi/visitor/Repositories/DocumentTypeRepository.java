package com.dfq.coeffi.visitor.Repositories;

import com.dfq.coeffi.visitor.Entities.DocumentType;
import com.dfq.coeffi.visitor.Entities.VisitorDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import javax.transaction.Transactional;

@Transactional
@EnableJpaRepositories
public interface DocumentTypeRepository extends JpaRepository<DocumentType,Long>
{

}
