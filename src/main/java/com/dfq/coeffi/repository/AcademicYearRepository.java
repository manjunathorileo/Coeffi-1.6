package com.dfq.coeffi.repository;

import com.dfq.coeffi.entity.master.AcademicYear;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AcademicYearRepository extends JpaRepository<AcademicYear, Long> {

    AcademicYear findByStatus(boolean status);

    @Query("SELECT y FROM AcademicYear y WHERE y.year = :year")
    Optional<AcademicYear> findByYear(@Param("year") String year);

}
