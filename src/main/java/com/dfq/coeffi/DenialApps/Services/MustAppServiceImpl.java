package com.dfq.coeffi.DenialApps.Services;

import com.dfq.coeffi.DenialApps.Entities.MustApps;
import com.dfq.coeffi.DenialApps.Repository.MustAppRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MustAppServiceImpl implements MustAppService  {
    @Autowired
    MustAppRepository mustAppRepository;
    @Override
    public MustApps saveMustApp(MustApps mustApps) {
        return mustAppRepository.save(mustApps) ;
    }

    @Override
    public List<MustApps> getMustApps() {
        return mustAppRepository.findAll();
    }

    @Override
    public MustApps getMustApp(long id) {
        return mustAppRepository.findOne(id) ;
    }


    @Override
    public void deleteMustApp(long id) {
      mustAppRepository.delete(id);

    }
}
