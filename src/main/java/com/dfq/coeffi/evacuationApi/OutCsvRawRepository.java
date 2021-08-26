package com.dfq.coeffi.evacuationApi;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@EnableJpaRepositories
@Transactional
public interface OutCsvRawRepository extends JpaRepository<OutCsvRaw,Long> {

//    @Query("select i from OutCsvRaw i where CONVERT(date, i.accessDate) = :todayDate")
//    List<OutCsvRaw> findByTodayDate(@Param("todayDate") Date todayDate);

    @Query("select i from OutCsvRaw i where i.accessDate >= :todayDate and i.processed = false order by id desc")
    List<OutCsvRaw> findByTodayDate(@Param("todayDate") Date todayDate);
}
