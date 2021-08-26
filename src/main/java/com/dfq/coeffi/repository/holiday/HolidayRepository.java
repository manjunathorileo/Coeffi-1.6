package com.dfq.coeffi.repository.holiday;

import com.dfq.coeffi.entity.holiday.Holiday;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Date;
import java.util.List;

/*
 * @author Azhar razvi
 * 
 */

public interface HolidayRepository extends JpaRepository<Holiday, Long>
{
    @Query("SELECT h FROM Holiday h where h.startDate between :startDate and :endDate")
    List<Holiday> getHolidayList(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

    Holiday findByStartDate(Date inputDate);
}