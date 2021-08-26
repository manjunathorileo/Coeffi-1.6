package com.dfq.coeffi.CanteenManagement.Service;

import com.dfq.coeffi.CanteenManagement.Entity.CatererDetailsAdv;
import com.dfq.coeffi.CanteenManagement.Repository.CatererDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CatererDetailsServiceImpl implements CatererDetailsService {

    @Autowired
    private CatererDetailRepository catererDetailsRepository;

    @Override
    public void createCatererDetails(CatererDetailsAdv catererDetailsAdv) {
        catererDetailsAdv.setStatus(true);
        catererDetailsRepository.save(catererDetailsAdv);
    }

    @Override
    public List<CatererDetailsAdv> getCatererDetails() {
        List<CatererDetailsAdv> catererDetailsAdvs = new ArrayList<>();
        List<CatererDetailsAdv> catererDetailsAdvList = catererDetailsRepository.findAll();
        for (CatererDetailsAdv catererDetailsAdv:catererDetailsAdvList) {
            if (catererDetailsAdv.getStatus().equals(true)){
                catererDetailsAdvs.add(catererDetailsAdv);
            }
        }
        return catererDetailsAdvs;
    }

    @Override
    public CatererDetailsAdv getCatererDetail(long id) {
        return catererDetailsRepository.findOne(id);
    }

    @Override
    public void deleteCatererDetail(long id) {
        CatererDetailsAdv catererDetailsAdv = catererDetailsRepository.findOne(id);
        catererDetailsAdv.setStatus(false);
        CatererDetailsAdv catererDetailsAdvObj = catererDetailsRepository.save(catererDetailsAdv);
    }
}
