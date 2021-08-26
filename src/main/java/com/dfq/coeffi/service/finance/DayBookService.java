package com.dfq.coeffi.service.finance;


import com.dfq.coeffi.entity.finance.daybook.DayBook;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface DayBookService {

	DayBook save(DayBook dayBook);
	
    List<DayBook> findAllDaybook();
    
    Optional<DayBook> getDayBook(Long id);
    
    void delete(Long id);

    void daybookEntry(BigDecimal amount, String description, Date createdOn, long refId, String refName);

    DayBook getDaybookByRefName(long refId, String refName);
 }
