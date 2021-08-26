package com.dfq.coeffi.LossAnalysis.productionTrack;

import com.dfq.coeffi.LossAnalysis.currentProductionRate.CurrentProductionRate;
import com.dfq.coeffi.LossAnalysis.currentProductionRate.CurrentProductionRateService;
import com.dfq.coeffi.LossAnalysis.productionLine.ProductionLineMaster;
import com.dfq.coeffi.LossAnalysis.productionLine.ProductionLineMasterService;
import com.dfq.coeffi.master.shift.Shift;
import com.dfq.coeffi.master.shift.ShiftController;
import com.dfq.coeffi.master.shift.ShiftService;
import com.dfq.coeffi.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import javax.persistence.EntityNotFoundException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

@Configuration
@EnableScheduling
@Slf4j
public class ProductionTrackAssignScheduler {

    private final ProductionTrackService productionTrackService;
    private final ShiftController shiftController;
    private final ProductionLineMasterService productionLineMasterService;
    private final ShiftService shiftService;
    private final CurrentProductionRateService currentProductionRateService;

    @Autowired
    public ProductionTrackAssignScheduler(ProductionTrackService productionTrackService, ShiftController shiftController, ProductionLineMasterService productionLineMasterService, ShiftService shiftService, CurrentProductionRateService currentProductionRateService) {
        this.productionTrackService = productionTrackService;
        this.shiftController = shiftController;
        this.productionLineMasterService = productionLineMasterService;
        this.shiftService = shiftService;
        this.currentProductionRateService = currentProductionRateService;
    }

    //@Scheduled(cron = "0 */1 * * * *")
    @Scheduled(cron = "0 0 */1 * * *")
    public void assignProductionTrack() throws ParseException {
        System.out.println("****************Production line tracked******************");
        Date today = new Date();
        int currentHr = DateUtil.getRunningHour();
        ResponseEntity<Shift> shiftResponseEntity = shiftController.getCurrentShift();
        if (shiftResponseEntity == null){
            throw new EntityNotFoundException("There is no running shift");
        }
        Shift shift = shiftResponseEntity.getBody();
        int startTime = DateUtil.getRunningHour(shift.getStartTime());
        int endTime = DateUtil.getRunningHour(shift.getEndTime());
        int totalShiftTime = endTime - startTime;
        int totalWorkingHr = currentHr - startTime;
        List<ProductionLineMaster> productionLineMasters = productionLineMasterService.getAllProductionLineMaster();
        for (ProductionLineMaster productionLineMasterObj : productionLineMasters) {
            ProductionTrack productionTrack = new ProductionTrack();
            List <ProductionTrack> productionTracks = productionTrackService.getProductionTrackByDateByShiftByProduction(today, shift.getId(), productionLineMasterObj.getId());
            if (!productionTracks.isEmpty()) {
                for (ProductionTrack productionTrackObj : productionTracks) {
                    productionTrack = productionTrackObj;
                }
            }
            CurrentProductionRate currentProductionRate = currentProductionRateService.getCurrentProductionRateByDateByProductionLine(today, productionLineMasterObj.getId());
            long totalDefaultProduction = productionLineMasterObj.getDefaultProductionRate() * totalWorkingHr;
            float completeDefaultProduction = Float.valueOf(totalDefaultProduction)/Float.valueOf(productionLineMasterObj.getDefaultProductionRate() * totalShiftTime);
            float defaultProductionPer = completeDefaultProduction * 100;
            productionTrack.setStatus(true);
            productionTrack.setCreatedOn(today);
            productionTrack.setProductionLineMaster(productionLineMasterObj);
            productionTrack.setShift(shift);
            productionTrack.setTotalWorkingHrs(totalWorkingHr);
            productionTrack.setDefaultProductionRate(productionLineMasterObj.getDefaultProductionRate());
            productionTrack.setDefaultTotalItemProductionNo(totalDefaultProduction);
            productionTrack.setDefaultProductionPercent(defaultProductionPer);
            if (currentProductionRate.getId() > 0) {
                long totalCurrentProduction = currentProductionRate.getProducationRate() * totalWorkingHr;
                float completeCurrentProduction = Float.valueOf(totalCurrentProduction)/Float.valueOf(currentProductionRate.getProducationRate() * totalShiftTime);
                float currentProductionPer = completeCurrentProduction * 100;
                productionTrack.setCurrentProductionRate(currentProductionRate.getProducationRate());
                productionTrack.setCurrentTotalItemProductionNo(totalCurrentProduction);
                productionTrack.setCurrentProductionPercent(currentProductionPer);
            } else {
                productionTrack.setCurrentProductionRate(productionLineMasterObj.getDefaultProductionRate());
                productionTrack.setCurrentTotalItemProductionNo(totalDefaultProduction);
                productionTrack.setCurrentProductionPercent(defaultProductionPer);
            }
            ProductionTrack productionTrackObj = productionTrackService.createProductionTrack(productionTrack);
        }
    }
}

