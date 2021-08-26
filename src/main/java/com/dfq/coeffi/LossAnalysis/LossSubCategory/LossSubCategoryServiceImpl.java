package com.dfq.coeffi.LossAnalysis.LossSubCategory;

import com.dfq.coeffi.LossAnalysis.LossCategory.LossCategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class LossSubCategoryServiceImpl implements LossSubCategoryService {

    @Autowired
    private LossSubCategoryRepository lossSubCategoryRepository;

    @Override
    public LossSubCategory createLossSubCategory(LossSubCategory lossSubCategory) {
        return lossSubCategoryRepository.save(lossSubCategory);
    }

    @Override
    public List<LossSubCategory> getAllLossSubCategory() {
        List<LossSubCategory> lossSubCategories = new ArrayList<>();
        List<LossSubCategory> lossSubCategoryList = lossSubCategoryRepository.findAll();
        for (LossSubCategory lossSubCategoryObj:lossSubCategoryList) {
            if (lossSubCategoryObj.getStatus().equals(true) && lossSubCategoryObj.getLossCategory().getStatus().equals(true)){
                lossSubCategories.add(lossSubCategoryObj);
            }
        }
        return lossSubCategories;
    }

    @Override
    public Optional<LossSubCategory> getLossSubCategory(long id) {
        return lossSubCategoryRepository.findById(id);
    }

    @Override
    public List<LossSubCategory> getLossSubCategoryByLosscategory(LossCategory lossCategory) {
        List<LossSubCategory> lossSubCategoryList = new ArrayList<>();
        List<LossSubCategory> lossSubCategories = lossSubCategoryRepository.findByLossCategory(lossCategory);
        for (LossSubCategory lossSubCategoryObj:lossSubCategories) {
            if (lossSubCategoryObj.getStatus().equals(true)){
                lossSubCategoryList.add(lossSubCategoryObj);
            }
        }
        return lossSubCategoryList;
    }

    @Override
    public LossSubCategory deleteLossSubCategory(long id) {
        Optional<LossSubCategory> lossSubCategoryOptional = getLossSubCategory(id);
        LossSubCategory lossSubCategory = lossSubCategoryOptional.get();
        lossSubCategory.setStatus(false);
        LossSubCategory lossSubCategoryObj = createLossSubCategory(lossSubCategory);
        return lossSubCategoryObj;
    }
}