package com.dfq.coeffi.visitor.Repositories;

import com.dfq.coeffi.visitor.Entities.Visitor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;


@EnableJpaRepositories
@Transactional
public interface VisitorRepository extends JpaRepository<Visitor, Long> {

    Visitor findByMobileNumber(String mobileNumber);

    Visitor findByDepartmentName(String departmentName);

    @Query("SELECT visitor FROM Visitor visitor where visitor.loggedOn between :startDate and :endDate")
    List<Visitor> getVisitorsBwnDate(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

    @Query("SELECT visitor FROM Visitor visitor where visitor.mobileNumber = :mobileNumber and visitor.loggedOn =:startDate")
    Visitor findByLoggedOnAndMobileNumber(@Param("startDate") Date date, @Param("mobileNumber") String mobileNumber);

    @Query("SELECT visitor FROM Visitor visitor where visitor.loggedOn = :loggedOn ORDER BY recordedTime ASC")
    List<Visitor> findByLoggedOn(@Param("loggedOn")Date loggedOn);

    @Query("SELECT visitor FROM Visitor visitor where visitor.mobileNumber = :mobileNumber and visitor.loggedOn =:startDate")
    List<Visitor> findByLoggedOnAndMobileNumberGEBE(@Param("startDate") Date date, @Param("mobileNumber") String mobileNumber);

    Visitor findByMobileNumberAndInTime(String mobileId, Date entryDate);
}
