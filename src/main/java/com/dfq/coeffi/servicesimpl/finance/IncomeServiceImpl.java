package com.dfq.coeffi.servicesimpl.finance;

import com.dfq.coeffi.auditlog.log.ApplicationLogService;
import com.dfq.coeffi.entity.finance.income.Income;
import com.dfq.coeffi.entity.finance.income.IncomeCategory;
import com.dfq.coeffi.entity.user.User;
import com.dfq.coeffi.repository.finance.IncomeCategoryRepository;
import com.dfq.coeffi.repository.finance.IncomeRepository;
import com.dfq.coeffi.service.finance.IncomeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static java.util.Optional.ofNullable;

@Service
public class IncomeServiceImpl implements IncomeService {

	private final IncomeRepository incomeRepository;
    private final IncomeCategoryRepository incomeCategoryRepository;
    private final ApplicationLogService applicationLogService;

	@Autowired
	public IncomeServiceImpl(IncomeRepository incomeRepository,IncomeCategoryRepository incomeCategoryRepository, ApplicationLogService applicationLogService)
	{
		this.incomeRepository = incomeRepository;
        this.incomeCategoryRepository = incomeCategoryRepository;
        this.applicationLogService = applicationLogService;
	}
	
	@Override
	public Income createIncome(Income income, User loggedUser)
	{
	    Income persistedObject = incomeRepository.save(income);

        if(persistedObject != null){
            applicationLogService.recordApplicationLog(loggedUser.getFirstName(), "income created "+persistedObject.getId(),
                    "POST", loggedUser.getId());
        }
		return persistedObject;
	}

	@Override
	public List<Income> listAllIncome() {
		return incomeRepository.findAll();
	}

	@Override
	public Optional<Income> getIncome(long id) {
		
		return ofNullable(incomeRepository.findOne(id));
	}

	@Override
	public List<Income> getIncomeByYear(long id) {
		return null;
	}

	@Override
	public void deleteIncome(long id) {
		incomeRepository.delete(id);
	}

	@Override
	public IncomeCategory createIncomeCategory(IncomeCategory incomeCategory, User loggedUser) {
	    IncomeCategory persistedObject = incomeCategoryRepository.save(incomeCategory);
        if(persistedObject != null){
            applicationLogService.recordApplicationLog(loggedUser.getFirstName(), "income category created "+persistedObject.getId(),
                    "POST", loggedUser.getId());
        }
		return persistedObject;
	}

	@Override
	public List<IncomeCategory> listAllIncomeCategory() {
		return incomeCategoryRepository.findAll();
	}

	@Override
	public Optional<IncomeCategory> getIncomeCategory(long id) {
		return ofNullable(incomeCategoryRepository.findOne(id));
	}

	@Override
	public List<IncomeCategory> getIncomeCategoryByYear(long year) {
		return null;
	}

	@Override
	public void deleteIncomeCategory(long id) {
		incomeCategoryRepository.delete(id);		
	}

	@Override
	public boolean isIncomeExists(long id) {
		return incomeRepository.exists(id);
	}

	@Override
	public boolean isIncomeCategoryExists(long id) {
		return incomeCategoryRepository.exists(id);
	}

	@Override
	public List<Income> getIncomeBetweenDate(Date startDate, Date endDate) {
		return incomeRepository.getIncomeBetweenDate(startDate,endDate);
	}

}
