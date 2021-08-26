package com.dfq.coeffi.vivo.service;

import com.dfq.coeffi.vivo.entity.TimeConfig;

import java.util.List;

public interface TimeConfigService {
    TimeConfig save(TimeConfig timeConfig);
    List<TimeConfig> getAll();
    TimeConfig get(long id);

}
