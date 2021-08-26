package com.dfq.coeffi.servicesimpl.finance;

import com.dfq.coeffi.entity.finance.daybook.DayBook;
import com.dfq.coeffi.entity.master.AcademicYear;
import com.dfq.coeffi.repository.finance.DayBookRepository;
import com.dfq.coeffi.service.AcademicYearService;
import com.dfq.coeffi.service.finance.DayBookService;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static java.util.Optional.ofNullable;

@Service
@Transactional
public class DayBookServiceImpl implements DayBookService {

	private final DayBookRepository dayBookRepository;
	private final AcademicYearService academicYearService;
	
	public DayBookServiceImpl(DayBookRepository dayBookRepository, AcademicYearService academicYearService) {
        this.dayBookRepository = dayBookRepository;
        this.academicYearService = academicYearService;
	}
	
	@Override
	public DayBook save(DayBook dayBook) {
		return dayBookRepository.save(dayBook);
	}

	@Override
	public List<DayBook> findAllDaybook() {
		return dayBookRepository.findAll();
	}

	@Override
	public Optional<DayBook> getDayBook(Long id) {
		return ofNullable(dayBookRepository.findOne(id));
	}

	@Override
	public void delete(Long id) {
		dayBookRepository.delete(id);
	}

    @Override
    public void daybookEntry(BigDecimal amount, String description, Date createdOn, long refId, String refName) {
	    Optional<AcademicYear> academicYear = academicYearService.getActiveAcademicYear();
	    if(!academicYear.isPresent()){
            throw new EntityNotFoundException("Academic Year");
        }
        AcademicYear academicYearObj = academicYear.get();
        DayBook daybook = new DayBook();
        daybook.setAmount(amount);
        daybook.setPaidDate(createdOn);
        daybook.setDescription(description);
        daybook.setAcademicYear(academicYearObj.getRange());
        daybook.setRefId(refId);
        daybook.setRefName(refName);
        dayBookRepository.save(daybook);
    }

    @Override
    public DayBook getDaybookByRefName(long refId, String refName) {
        return dayBookRepository.findByRefIdAndRefName(refId, refName);
    }
}