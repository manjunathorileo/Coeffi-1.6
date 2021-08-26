package com.dfq.coeffi.LossAnalysis.productionTrack;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ProductionTrackServiceImpl implements ProductionTrackService {

    @Autowired
    private ProductionTrackRepository productionTrackRepository;


    @Override
    public ProductionTrack createProductionTrack(ProductionTrack productionTrack) {
        return productionTrackRepository.save(productionTrack);
    }

    @Override
    public List<ProductionTrack> getAllProductionTrack() {
        List<ProductionTrack> productionTracks = new ArrayList<>();
        List<ProductionTrack> productionTrackList = productionTrackRepository.findAll();
        for (ProductionTrack productionTrackObj:productionTrackList) {
            if (productionTrackObj.getStatus().equals(true) && productionTrackObj.getProductionLineMaster().getStatus().equals(true)){
                productionTracks.add(productionTrackObj);
            }
        }
        return productionTracks;
    }

    @Override
    public Optional<ProductionTrack> getProductionTrack(long id) {
        return productionTrackRepository.findById(id);
    }

    @Override
    public List<ProductionTrack> getProductionTrackByDateByShiftByProduction(Date today, long shiftId, long productionLineId) {
        List<ProductionTrack> productionTracks = new ArrayList<>();
        List<ProductionTrack> productionTrackList = productionTrackRepository.findByDateByShiftByProductionLine(today, shiftId, productionLineId);
        for (ProductionTrack productionTrackObj:productionTrackList) {
            if (productionTrackObj.getStatus().equals(true)){
                productionTracks.add(productionTrackObj);
            }
        }
        return productionTracks;
    }
}
