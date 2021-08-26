package com.dfq.coeffi.repository.holiday;

import com.dfq.coeffi.entity.holiday.HolidayDefination;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Date;
import java.util.List;

/*
 * @author Azhar razvi
 * 
 */

public interface HolidayDefinationRepository extends JpaRepository<HolidayDefination, Long>
{
    @Query("from HolidayDefination as h where h.startDate between :startDate and :endDate")
    List<HolidayDefination> getHolidayDefinationList(@Param("startDate") Date startDate, @Param("endDate") Date endDate);
}
