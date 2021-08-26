package com.dfq.coeffi.LossAnalysis;

import com.dfq.coeffi.LossAnalysis.LossCategory.LossCategory;
import com.dfq.coeffi.LossAnalysis.machine.MachineMaster;
import com.dfq.coeffi.LossAnalysis.productionLine.ProductionLineMaster;
import com.dfq.coeffi.master.shift.Shift;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class LossAnalysisServiceImpl implements LossAnalysisService {

    @Autowired
    private LossAnalysisRepository lossAnalysisRepository;

    @Override
    public LossAnalysis createLossAnalysis(LossAnalysis lossAnalysis) {
        return lossAnalysisRepository.save(lossAnalysis);
    }

    @Override
    public List<LossAnalysis> getAllLossAnalysis() {
        return lossAnalysisRepository.findAll();
    }

    @Override
    public Optional<LossAnalysis> getLossAnalysisById(long id) {
        return lossAnalysisRepository.findById(id);
    }

    @Override
    public List<LossAnalysis> getLossAnalysisByAssemblyLineByStage(ProductionLineMaster productionLineMaster, MachineMaster machineMaster) {
        List<LossAnalysis> lossAnalyses = new ArrayList<>();
        List<LossAnalysis> lossAnalysisList = lossAnalysisRepository.findByProductionLineByMachine(productionLineMaster, machineMaster);
        for (LossAnalysis lossAnalysis:lossAnalysisList) {
            if (lossAnalysis.getStatus().equals(true) && lossAnalysis.getProductionLine().getStatus().equals(true) && lossAnalysis.getMachine().getStatus().equals(true) && lossAnalysis.getLossCategory().getStatus().equals(true) && lossAnalysis.getLossSubCategory().getStatus().equals(true)){
                lossAnalyses.add(lossAnalysis);
            }
        }
        return lossAnalyses;
    }

    @Override
    public List<LossAnalysis> getLossAnalysisByAssemblyLine(ProductionLineMaster productionLineMaster) {
        List<LossAnalysis> lossAnalyses = new ArrayList<>();
        List<LossAnalysis> lossAnalysisList = lossAnalysisRepository.findByProductionLine(productionLineMaster);
        for (LossAnalysis lossAnalysis:lossAnalysisList) {
            if (lossAnalysis.getStatus().equals(true) && lossAnalysis.getMachine().getStatus().equals(true) && lossAnalysis.getLossCategory().getStatus().equals(true) && lossAnalysis.getLossSubCategory().getStatus().equals(true)){
                lossAnalyses.add(lossAnalysis);
            }
        }
        return lossAnalyses;
    }

    @Override
    public List<LossAnalysis> getLossAnalysisByShift(Shift shift) {
        List<LossAnalysis> lossAnalyses = new ArrayList<>();
        List<LossAnalysis> lossAnalysisList = lossAnalysisRepository.findByShift(shift);
        for (LossAnalysis lossAnalysis:lossAnalysisList) {
            if (lossAnalysis.getStatus().equals(true) && lossAnalysis.getProductionLine().getStatus().equals(true) && lossAnalysis.getMachine().getStatus().equals(true) && lossAnalysis.getLossCategory().getStatus().equals(true) && lossAnalysis.getLossSubCategory().getStatus().equals(true)){
                lossAnalyses.add(lossAnalysis);
            }
        }
        return lossAnalyses;
    }

    @Override
    public List<LossAnalysis> getLossAnalysisByLossCategory(LossCategory lossCategory) {
        List<LossAnalysis> lossAnalyses = new ArrayList<>();
        List<LossAnalysis> lossAnalysisList = lossAnalysisRepository.findByLossCategory(lossCategory);
        for (LossAnalysis lossAnalysis:lossAnalysisList) {
            if (lossAnalysis.getStatus().equals(true) && lossAnalysis.getProductionLine().getStatus().equals(true) && lossAnalysis.getMachine().getStatus().equals(true) && lossAnalysis.getLossCategory().getStatus().equals(true) && lossAnalysis.getLossSubCategory().getStatus().equals(true)){
                lossAnalyses.add(lossAnalysis);
            }
        }
        return lossAnalyses;
    }

    @Override
    public void deleteLossAnalysis(long id) {

    }

    @Override
    public List<LossAnalysis> getLossAnalysisByShiftByDateByProductionLine(Date createOn, long shiftId, long productionLineId) {
        List<LossAnalysis> lossAnalyses = new ArrayList<>();
        List<LossAnalysis> lossAnalysisList = lossAnalysisRepository.findByCreatedOnByShiftByProductionLine(createOn,shiftId,productionLineId);
        for (LossAnalysis lossAnalysis:lossAnalysisList) {
            if (lossAnalysis.getStatus().equals(true) && lossAnalysis.getProductionLine().getStatus().equals(true) && lossAnalysis.getMachine().getStatus().equals(true) && lossAnalysis.getLossCategory().getStatus().equals(true) && lossAnalysis.getLossSubCategory().getStatus().equals(true)){
                lossAnalyses.add(lossAnalysis);
            }
        }
        return lossAnalyses;
    }

    @Override
    public List<LossAnalysis> getLossAnalysisByShiftByFromDateByToDateByProductionLine(Date fromDate, Date toDate, long shiftId, long productionLineId) {
        List<LossAnalysis> lossAnalyses = new ArrayList<>();
        List<LossAnalysis> lossAnalysisList = lossAnalysisRepository.findByFromDateByToDateByShiftByProductionLine(fromDate,toDate,shiftId,productionLineId);
        for (LossAnalysis lossAnalysis:lossAnalysisList) {
            if (lossAnalysis.getStatus().equals(true) && lossAnalysis.getProductionLine().getStatus().equals(true) && lossAnalysis.getMachine().getStatus().equals(true) && lossAnalysis.getLossCategory().getStatus().equals(true) && lossAnalysis.getLossSubCategory().getStatus().equals(true)){
                lossAnalyses.add(lossAnalysis);
            }
        }
        return lossAnalyses;
    }

    @Override
    public List<LossAnalysis> getLossAnalysisByFromDateByToDateByShiftByProductionLineByLossCategory(Date fromDate, Date toDate, long shiftId, long productionLineId, long lossCategoryId) {
        List<LossAnalysis> lossAnalyses = new ArrayList<>();
        List<LossAnalysis> lossAnalysisList = lossAnalysisRepository.findByfromDateByToDateByShiftByProductionLineByLossCategory(fromDate, toDate, shiftId, productionLineId, lossCategoryId);
        for (LossAnalysis lossAnalysis:lossAnalysisList) {
            if (lossAnalysis.getStatus().equals(true) && lossAnalysis.getProductionLine().getStatus().equals(true) && lossAnalysis.getMachine().getStatus().equals(true) && lossAnalysis.getLossCategory().getStatus().equals(true) && lossAnalysis.getLossSubCategory().getStatus().equals(true)){
                lossAnalyses.add(lossAnalysis);
            }
        }
        return lossAnalyses;
    }

    @Override
    public List<LossAnalysis> getLossAnalysisByShiftByFromDateByToDateByMachine(Date fromDate, Date toDate, long shiftId, long machineId) {
        List<LossAnalysis> lossAnalyses = new ArrayList<>();
        List<LossAnalysis> lossAnalysisList = lossAnalysisRepository.findByFromDateByToDateByShiftByMachine(fromDate,toDate,shiftId,machineId);
        for (LossAnalysis lossAnalysis:lossAnalysisList) {
            if (lossAnalysis.getStatus().equals(true) && lossAnalysis.getProductionLine().getStatus().equals(true) && lossAnalysis.getMachine().getStatus().equals(true) && lossAnalysis.getLossCategory().getStatus().equals(true) && lossAnalysis.getLossSubCategory().getStatus().equals(true)){
                lossAnalyses.add(lossAnalysis);
            }
        }
        return lossAnalyses;
    }

    @Override
    public List<LossAnalysis> getLossAnalysisByFromDateByToDateByShiftByMachineByLossCategory(Date fromDate, Date toDate, long shiftId, long machineId, long lossCategoryId) {
        List<LossAnalysis> lossAnalyses = new ArrayList<>();
        List<LossAnalysis> lossAnalysisList = lossAnalysisRepository.findByfromDateByToDateByShiftByMachineByLossCategory(fromDate, toDate, shiftId, machineId, lossCategoryId);
        for (LossAnalysis lossAnalysis:lossAnalysisList) {
            if (lossAnalysis.getStatus().equals(true) && lossAnalysis.getProductionLine().getStatus().equals(true) && lossAnalysis.getMachine().getStatus().equals(true) && lossAnalysis.getLossCategory().getStatus().equals(true) && lossAnalysis.getLossSubCategory().getStatus().equals(true)){
                lossAnalyses.add(lossAnalysis);
            }
        }
        return lossAnalyses;
    }
}