package com.dfq.coeffi.service.finance;


import com.dfq.coeffi.entity.finance.expense.Liability;
import com.dfq.coeffi.entity.user.User;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface LiabilityService
{	
	Liability createLiability(Liability liability, User loggedUser);
	List<Liability> listAllLiability();
	Optional<Liability> getLiability(long id);
	void deleteLiability(long id);
	List<Liability> getLiabilityBetweenDate(Date startDate, Date endDate);

}
