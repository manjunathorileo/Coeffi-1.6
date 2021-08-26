package com.dfq.coeffi.CanteenManagement.Service;

import com.dfq.coeffi.CanteenManagement.Entity.CatererSettingsAdv;
import com.dfq.coeffi.CanteenManagement.Repository.CatererSettingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CatererSettingsServiceImpl implements CatererSettingsService {

    @Autowired
    private CatererSettingRepository catererSettingRepository;

    @Override
    public CatererSettingsAdv createCatererSettings(CatererSettingsAdv catererSettingsAdv) {
        catererSettingsAdv.setStatus(true);
        return catererSettingRepository.save(catererSettingsAdv);
    }

    @Override
    public List<CatererSettingsAdv> getCatererSettings() {
        List<CatererSettingsAdv> catererSettingAdvs = new ArrayList<>();
        List<CatererSettingsAdv> catererSettingsAdvList = catererSettingRepository.findAll();
        for (CatererSettingsAdv catererSettingsAdvObj : catererSettingsAdvList) {
            if (catererSettingsAdvObj.getStatus().equals(true)) {
                if (catererSettingsAdvObj.getFoodTimeMasterAdv().getStatus().equals(true) && catererSettingsAdvObj.getCounterDetailsAdv().getStatus().equals(true)) {
                    catererSettingAdvs.add(catererSettingsAdvObj);
                } else {
                    catererSettingsAdvObj.setStatus(false);
                    catererSettingRepository.save(catererSettingsAdvObj);
                }
            }
        }
        return catererSettingAdvs;
    }

    @Override
    public CatererSettingsAdv getCatererSetting(long id) {
        return catererSettingRepository.findById(id);
    }

    @Override
    public CatererSettingsAdv deleteCatererSettings(long id) {
        CatererSettingsAdv catererSettingsAdv = catererSettingRepository.findById(id);
        catererSettingsAdv.setStatus(false);
        return catererSettingRepository.save(catererSettingsAdv);
    }

    @Override
    public CatererSettingsAdv getCatererSetting(String foodType, String employeeType) {
        return catererSettingRepository.findByFoodTypeAndEmployeeType(foodType, employeeType);
    }

    @Override
    public CatererSettingsAdv getCatererSettingsAdvByFoodTypeByEmployeeTypeByCounter(long foodTypeId, String employeeType, long counterId) {
        return catererSettingRepository.findByFoodTypeByEmployeeTypeByCounter(foodTypeId, employeeType, counterId);
    }
}