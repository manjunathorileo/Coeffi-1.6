package com.dfq.coeffi.CanteenManagement.Service;

import com.dfq.coeffi.CanteenManagement.Entity.CatererDetailsAdv;

import java.util.List;

public interface CatererDetailsService {

    void createCatererDetails(CatererDetailsAdv catererDetailsAdv);
    List<CatererDetailsAdv> getCatererDetails();
    CatererDetailsAdv getCatererDetail(long id);
    void deleteCatererDetail(long id);
}
