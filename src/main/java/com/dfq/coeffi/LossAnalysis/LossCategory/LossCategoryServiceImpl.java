package com.dfq.coeffi.LossAnalysis.LossCategory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
public class LossCategoryServiceImpl implements LossCategoryService {

    @Autowired
    private LossCategoryRepository lossCategoryRepository;

    @Override
    public LossCategory createLossCategory(LossCategory lossCategory) {
        return lossCategoryRepository.save(lossCategory);
    }

    @Override
    public List<LossCategory> getAllLossCategory() {
        List<LossCategory> lossCategories = new ArrayList<>();
        List<LossCategory> lossCategoryList = lossCategoryRepository.findAll();
        for (LossCategory lossCategoryObj:lossCategoryList) {
            if (lossCategoryObj.getStatus().equals(true)){
                lossCategories.add(lossCategoryObj);
            }
        }
        return lossCategories;
    }

    @Override
    public Optional<LossCategory> getLossCategory(long id) {
        return lossCategoryRepository.findById(id);
    }

    @Override
    public LossCategory deleteLossCategory(long id) {
        Optional<LossCategory> lossCategoryOptional = getLossCategory(id);
        LossCategory lossCategory = lossCategoryOptional.get();
        lossCategory.setStatus(false);
        LossCategory lossCategoryObj = createLossCategory(lossCategory);
        return lossCategoryObj;
    }
}