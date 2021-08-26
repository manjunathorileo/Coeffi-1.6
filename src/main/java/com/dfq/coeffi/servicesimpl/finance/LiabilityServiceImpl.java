package com.dfq.coeffi.servicesimpl.finance;

import com.dfq.coeffi.auditlog.log.ApplicationLogService;
import com.dfq.coeffi.entity.finance.expense.Liability;
import com.dfq.coeffi.entity.user.User;
import com.dfq.coeffi.repository.finance.LiabilityRepository;
import com.dfq.coeffi.service.finance.LiabilityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static java.util.Optional.ofNullable;

@Service
public class LiabilityServiceImpl implements LiabilityService {

    private final LiabilityRepository liabilityRepository;
    private final ApplicationLogService applicationLogService;

	@Autowired
	public LiabilityServiceImpl(LiabilityRepository liabilityRepository, ApplicationLogService applicationLogService)
	{
        this.liabilityRepository = liabilityRepository;
        this.applicationLogService = applicationLogService;
	}
	@Override
	public Liability createLiability(Liability liability, User loggedUser) {

        Liability persistedObject = liabilityRepository.save(liability);
        if(persistedObject != null){
            applicationLogService.recordApplicationLog(loggedUser.getFirstName(), "liability created "+persistedObject.getId(),
                    "POST", loggedUser.getId());
        }

		return persistedObject;
	}

	@Override
	public List<Liability> listAllLiability() {
 		return liabilityRepository.findAll();
	}

	@Override
	public Optional<Liability> getLiability(long id) {
 		return ofNullable(liabilityRepository.findOne(id));
	}

	@Override
	public void deleteLiability(long id) 
	{
		liabilityRepository.delete(id);
	}
	@Override
	public List<Liability> getLiabilityBetweenDate(Date startDate, Date endDate) {
		return liabilityRepository.getLiabilityBetweenDate(startDate,endDate);
	}

}
