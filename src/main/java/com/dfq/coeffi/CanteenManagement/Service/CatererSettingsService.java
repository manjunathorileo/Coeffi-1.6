package com.dfq.coeffi.CanteenManagement.Service;

import com.dfq.coeffi.CanteenManagement.Entity.CatererSettingsAdv;

import java.util.List;

public interface CatererSettingsService {
    CatererSettingsAdv createCatererSettings(CatererSettingsAdv catererSettingsAdv);
    List<CatererSettingsAdv> getCatererSettings();
    CatererSettingsAdv getCatererSetting(long id);
    CatererSettingsAdv deleteCatererSettings(long id);
    CatererSettingsAdv getCatererSetting(String foodType, String employeeType);
    CatererSettingsAdv getCatererSettingsAdvByFoodTypeByEmployeeTypeByCounter(long foodTypeId, String employeeType, long counterId);
}
