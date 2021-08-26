package com.dfq.coeffi.vivo.repository;

import com.dfq.coeffi.vivo.entity.TypeOfVisit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TypeOfVisitRepository extends JpaRepository<TypeOfVisit,Long>
{

    Optional<TypeOfVisit> findById(long id);

    void deleteById(long id);
}
