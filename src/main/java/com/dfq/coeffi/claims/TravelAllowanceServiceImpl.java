package com.dfq.coeffi.claims;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TravelAllowanceServiceImpl implements TravelAllowanceService {

    private TravelAllowanceRepository travelAllowanceRepository;

    @Autowired
    public TravelAllowanceServiceImpl(TravelAllowanceRepository travelAllowanceRepository){
        this.travelAllowanceRepository = travelAllowanceRepository;
    }

    @Override
    public TravelAllowance applyTravellingAllowanceDearnessAllowance(TravelAllowance travelAllowance) {
        return travelAllowanceRepository.save(travelAllowance);
    }

    @Override
    public List<TravelAllowance> getAllTADA() {
        return travelAllowanceRepository.findAll();
    }

    @Override
    public List<TravelAllowance> getTADAByStatus(TravellingApprovalStatus travellingApprovalStatus) {
        return travelAllowanceRepository.findByTravellingApprovalStatus(travellingApprovalStatus);
    }

    @Override
    public Optional<TravelAllowance> getTADAById(long id) {
        return travelAllowanceRepository.findById(id);
    }
}
