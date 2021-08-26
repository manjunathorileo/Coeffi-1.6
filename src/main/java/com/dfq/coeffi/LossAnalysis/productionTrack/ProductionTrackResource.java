package com.dfq.coeffi.LossAnalysis.productionTrack;

import com.dfq.coeffi.controller.BaseController;
import com.dfq.coeffi.master.shift.ShiftService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.util.Date;
import java.util.List;

@RestController
@Slf4j
public class ProductionTrackResource extends BaseController {

    private final ProductionTrackService productionTrackService;
    private final ShiftService shiftService;

    @Autowired
    public ProductionTrackResource(ProductionTrackService productionTrackService, ShiftService shiftService) {
        this.productionTrackService = productionTrackService;
        this.shiftService = shiftService;
    }

    @PostMapping("/production-track")
    private ResponseEntity<ProductionTrack> createProductionTrack(@Valid @RequestBody ProductionTrack productionTrack) {
        List<ProductionTrack> productionTracks = productionTrackService.getProductionTrackByDateByShiftByProduction(productionTrack.getCreatedOn(), productionTrack.getShift().getId(), productionTrack.getProductionLineMaster().getId());
        if (productionTracks.isEmpty()) {
            throw new EntityNotFoundException("There is No Production track for this shift");
        }
        return new ResponseEntity(productionTracks, HttpStatus.OK);
    }

    @PostMapping("/production-track-productionline-good-production-track")
    private ResponseEntity<ProductionTrack> trackGoodProductionAndNonQualityProduction(@Valid @RequestBody ProductionTrack productionTrack) {
        Date today = new Date();
        List<ProductionTrack> productionTracks = productionTrackService.getProductionTrackByDateByShiftByProduction(productionTrack.getCreatedOn(), productionTrack.getShift().getId(), productionTrack.getProductionLineMaster().getId());
        if (productionTracks.isEmpty()) {
            throw new EntityNotFoundException("There is No Production track for this shift");
        }
        ProductionTrack productionTrackUpdate = new ProductionTrack();
        for (ProductionTrack productionTrackObj : productionTracks) {
            productionTrackObj.setGoodProduction(productionTrack.getGoodProduction());
            productionTrackObj.setNonQualityProduction(productionTrack.getNonQualityProduction());
            productionTrackUpdate = productionTrackService.createProductionTrack(productionTrackObj);
        }
        return new ResponseEntity(productionTrackUpdate, HttpStatus.OK);
    }
}
