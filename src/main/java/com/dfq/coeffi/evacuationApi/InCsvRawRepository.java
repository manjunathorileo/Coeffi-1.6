package com.dfq.coeffi.evacuationApi;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;
import java.util.List;

@EnableJpaRepositories
@Transactional
public interface InCsvRawRepository extends JpaRepository<InCsvRaw,Long> {


//    @Query("select i from InCsvRaw i where CONVERT(date, i.accessDate) = :todayDate")
//    List<InCsvRaw> findByTodayDate(@Param("todayDate") Date todayDate);

    @Query("select i from InCsvRaw i where i.accessDate >= :todayDate and i.processed = false order by id desc")
    List<InCsvRaw> findByTodayDate(@Param("todayDate") Date todayDate);
}
