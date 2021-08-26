package com.dfq.coeffi.visitor.Repositories;

import com.dfq.coeffi.visitor.Entities.VisitorDocument;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VisitorDocRepo extends JpaRepository<VisitorDocument,Long> {
}
