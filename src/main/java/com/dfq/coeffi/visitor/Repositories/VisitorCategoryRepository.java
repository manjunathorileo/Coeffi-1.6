package com.dfq.coeffi.visitor.Repositories;

import com.dfq.coeffi.visitor.Entities.VisitorCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories
public interface VisitorCategoryRepository extends JpaRepository<VisitorCategory,Long>
{

}
