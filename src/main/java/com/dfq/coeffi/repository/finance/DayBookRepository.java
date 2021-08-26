package com.dfq.coeffi.repository.finance;

import com.dfq.coeffi.entity.finance.daybook.DayBook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

public interface DayBookRepository extends JpaRepository<DayBook, Long> {

    DayBook findByRefIdAndRefName(@Param("refId") long refId, @Param("refName") String refName);
}