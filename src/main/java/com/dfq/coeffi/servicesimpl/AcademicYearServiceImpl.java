package com.dfq.coeffi.servicesimpl;

import com.dfq.coeffi.entity.master.AcademicYear;
import com.dfq.coeffi.repository.AcademicYearRepository;
import com.dfq.coeffi.service.AcademicYearService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.Optional.ofNullable;

@Service
@Transactional
public class AcademicYearServiceImpl implements AcademicYearService {

	@Autowired
    private AcademicYearRepository academicYearRepository;
	
	@Override
	public AcademicYear createAcademicYear(AcademicYear academicYear) {
		return academicYearRepository.save(academicYear);
	}

	@Override
	public List<AcademicYear> getAcademicYears() {
		return newArrayList(ofNullable(academicYearRepository.findAll()).orElse(Collections.emptyList()));
	}

	@Override
	public Optional<AcademicYear> getAcademicYear(long id) {
		return ofNullable(academicYearRepository.findOne(id));
	}


    @Override
    public Optional<AcademicYear> getActiveAcademicYear() {
        return ofNullable(academicYearRepository.findByStatus(true));
    }


    @Override
	public boolean isAcademicYearExists(long id) {
		return academicYearRepository.exists(id);
	}

	@Override
	public void deleteAcademicYear(long id) {
		academicYearRepository.delete(id);
	}

	@Override
	public AcademicYear getAcademicYearByStatus(Boolean status) {
		return academicYearRepository.findByStatus(true);
	}

	@Override
	public Optional<AcademicYear> getPreviousActiveYear() {
		String year = String.valueOf((Calendar.getInstance().get(Calendar.YEAR))-1);
		return academicYearRepository.findByYear(year);
	}
}