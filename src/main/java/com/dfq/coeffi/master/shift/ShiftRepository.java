package com.dfq.coeffi.master.shift;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

@Transactional
@EnableJpaRepositories
public interface ShiftRepository extends JpaRepository<Shift, Long> {


    @Query("UPDATE Shift s SET s.status=false WHERE s.id=:id")
    @Modifying
    void deactivate(@Param("id") long id);

    List<Shift> findByStatus(boolean status);

    Shift findByName(String shiftName);
}