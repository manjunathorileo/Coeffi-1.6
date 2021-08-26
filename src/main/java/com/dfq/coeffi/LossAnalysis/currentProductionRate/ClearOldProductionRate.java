package com.dfq.coeffi.LossAnalysis.currentProductionRate;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Configuration
@EnableScheduling
@Slf4j
public class ClearOldProductionRate {

    private final CurrentProductionRateService currentProductionRateService;
    private final CurrentProductionRateRepository currentProductionRateRepository;

    @Autowired
    public ClearOldProductionRate(CurrentProductionRateService currentProductionRateService, CurrentProductionRateRepository currentProductionRateRepository) {
        this.currentProductionRateService = currentProductionRateService;
        this.currentProductionRateRepository = currentProductionRateRepository;
    }

    //@Scheduled(cron = "0 0 0 * * *")
    public void clearOlProductionRate() throws ParseException {
        Date toDate = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(toDate);
        SimpleDateFormat dateFormate = new SimpleDateFormat("yyyy-MM-dd");
        calendar.add(Calendar.DATE, -2);
        Date date = calendar.getTime();
        Date oldDate = dateFormate.parse(dateFormate.format(date));

        List<CurrentProductionRate> currentProductionRates = currentProductionRateService.getAllCurrentProductionRate();
        for (CurrentProductionRate currentProductionRate:currentProductionRates) {
            if (currentProductionRate.getCreatedOn().getDate() < oldDate.getDate()){
                currentProductionRateRepository.delete(currentProductionRate.getId());
            }
        }
    }
}
