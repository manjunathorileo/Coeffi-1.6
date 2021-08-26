package com.dfq.coeffi.foodManagement;

import java.util.List;

public interface CanteenService {

    void createFoodTime(FoodTimeMaster foodTimeMaster);
    List<FoodTimeMaster> getFoodTimes();
    FoodTimeMaster getFoodTime(long id);
    void deleteFoodTime(long id);

    void createCatererDetails(CatereDetails catereDetails);
    List<CatereDetails> getCatererDetails();
    CatereDetails getCatererDetail(long id);
    void deleteCatererDetail(long id);

    void createCatererSettings(CatererSettings catererSettings);
    List<CatererSettings> getCatererSettings();
    CatererSettings getCatererSetting(long id);

    void deleteCatererSettings(long id);
    CatererSettings getCatererSetting(String foodType,String employeeType);

    void createFoodTracker(FoodTracker foodTracker);
    List<FoodTracker> getFoodTracks();
    FoodTracker getFoodTrack(long id);
    void deleteTrack(long id);



}
