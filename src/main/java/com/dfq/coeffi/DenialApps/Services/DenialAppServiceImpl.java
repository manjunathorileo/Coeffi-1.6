package com.dfq.coeffi.DenialApps.Services;

import com.dfq.coeffi.DenialApps.Entities.DenialApps;
import com.dfq.coeffi.DenialApps.Repository.DenialAppRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class DenialAppServiceImpl implements DenialAppService {
    @Autowired
    DenialAppRepository denialAppRepository;
    @Override
    public DenialApps saveDenialApp(DenialApps denialApps) {
        return denialAppRepository.save(denialApps) ;
    }

    @Override
    public List<DenialApps> getDenialApps() {
        return denialAppRepository.findAll();
    }

    @Override
    public DenialApps getDenialApp(long id) {
        return denialAppRepository.findOne(id) ;
    }

    @Override
    public void deleteDenialApp(long id) {
        denialAppRepository.delete(id);
    }
}
