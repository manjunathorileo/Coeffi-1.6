package com.dfq.coeffi.vivo.service;

import com.dfq.coeffi.vivo.entity.TimeConfig;
import com.dfq.coeffi.vivo.repository.TimeConfigRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TimeConfigServiceImpl implements TimeConfigService {
    @Autowired
    TimeConfigRepo timeConfigRepo;
    @Override
    public TimeConfig save(TimeConfig timeConfig) {
        return timeConfigRepo.save(timeConfig);
    }

    @Override
    public List<TimeConfig> getAll() {
        return timeConfigRepo.findAll();
    }

    @Override
    public TimeConfig get(long id) {
        return timeConfigRepo.findOne(id);
    }
}
