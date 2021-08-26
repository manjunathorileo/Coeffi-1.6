package com.dfq.coeffi.foodManagement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CanteenServiceImpl implements CanteenService {

    @Autowired
    CatererSettingsRepository catererSettingsRepository;
    @Autowired
    FoodTimeRepository foodTimeRepository;
    @Autowired
    CatererDetailsRepository catererDetailsRepository;


    @Override
    public void createFoodTime(FoodTimeMaster foodTimeMaster) {
        foodTimeRepository.save(foodTimeMaster);
    }

    @Override
    public List<FoodTimeMaster> getFoodTimes() {
        return foodTimeRepository.findAll();
    }

    @Override
    public FoodTimeMaster getFoodTime(long id) {
        return foodTimeRepository.findOne(id);
    }

    @Override
    public void deleteFoodTime(long id) {
        foodTimeRepository.delete(id);
    }

    @Override
    public void createCatererDetails(CatereDetails catereDetails) {
        catererDetailsRepository.save(catereDetails);
    }

    @Override
    public List<CatereDetails> getCatererDetails() {
        return catererDetailsRepository.findAll();
    }

    @Override
    public CatereDetails getCatererDetail(long id) {
        return catererDetailsRepository.findOne(id);
    }

    @Override
    public void deleteCatererDetail(long id) {
        catererDetailsRepository.delete(id);
    }

    @Override
    public void createCatererSettings(CatererSettings catererSettings) {
        catererSettingsRepository.save(catererSettings);
    }

    @Override
    public List<CatererSettings> getCatererSettings() {
        return catererSettingsRepository.findAll();
    }

    @Override
    public CatererSettings getCatererSetting(long id) {
        return catererSettingsRepository.findOne(id);
    }

    @Override
    public void deleteCatererSettings(long id) {
        catererSettingsRepository.delete(id);
    }

    @Override
    public CatererSettings getCatererSetting(String foodType, String employeeType) {
        return catererSettingsRepository.findByFoodTypeAndEmployeeType(foodType,employeeType);
    }

    @Override
    public void createFoodTracker(FoodTracker foodTracker) {

    }

    @Override
    public List<FoodTracker> getFoodTracks() {
        return null;
    }

    @Override
    public FoodTracker getFoodTrack(long id) {
        return null;
    }

    @Override
    public void deleteTrack(long id) {

    }
}
