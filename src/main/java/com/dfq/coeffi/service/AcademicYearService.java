package com.dfq.coeffi.service;
/**
 * @Auther Ashvini B on 28/4/18.
 * @Company Orileo Technologies
 */
import com.dfq.coeffi.entity.master.AcademicYear;
import java.util.List;
import java.util.Optional;

public interface AcademicYearService {

	AcademicYear createAcademicYear(AcademicYear academicYear);
    List<AcademicYear> getAcademicYears();
    Optional<AcademicYear> getAcademicYear(long id);
    Optional<AcademicYear> getActiveAcademicYear();
    boolean isAcademicYearExists(long id);
    void deleteAcademicYear(long id);
    AcademicYear getAcademicYearByStatus(Boolean status);
    Optional<AcademicYear> getPreviousActiveYear();

}
