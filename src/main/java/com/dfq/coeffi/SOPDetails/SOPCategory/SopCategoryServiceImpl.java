package com.dfq.coeffi.SOPDetails.SOPCategory;

import com.dfq.coeffi.SOPDetails.SOPType.SopType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class SopCategoryServiceImpl implements SopCategoryService
{
    @Autowired
    SopCategoryRepository SOPCategoryRepository;

    @Override
    public SopCategory saveDigitalSOP(SopCategory SOPCategory)
    {
        return SOPCategoryRepository.save(SOPCategory);
    }

    @Override
    public List<SopCategory> getDigitalSOP() {
        List<SopCategory> sopCategories = new ArrayList<>();
        List<SopCategory> sopCategoryList = SOPCategoryRepository.findAll();
        for (SopCategory sopCategory:sopCategoryList) {
            if (sopCategory.getStatus().equals(true)){
                sopCategories.add(sopCategory);
            }
        }
        return sopCategories;
    }

    @Override
    public Optional<SopCategory> getSopCategory(long id) {
        return SOPCategoryRepository.findById(id);
    }

    @Override
    public SopCategory deleteSopCategory(long id) {
        Optional<SopCategory> sopCategoryOptional = SOPCategoryRepository.findById(id);
        SopCategory sopCategory = sopCategoryOptional.get();
        sopCategory.setStatus(false);
        SopCategory deletedSopCategory = SOPCategoryRepository.save(sopCategory);
        return deletedSopCategory;
    }

    @Override
    public List<SopCategory> getSopCategoryBySopType(SopType sopType) {
        List<SopCategory> sopCategories = new ArrayList<>();
        List<SopCategory> sopCategoryList = SOPCategoryRepository.findBySopType(sopType);
        for (SopCategory sopCategory:sopCategoryList) {
            if (sopCategory.getStatus().equals(true)){
                sopCategories.add(sopCategory);
            }
        }
        return sopCategories;
    }

}