package com.dfq.coeffi.DenialApps.Services;

import com.dfq.coeffi.DenialApps.Entities.DenialApps;

import java.util.List;

public interface DenialAppService {
    DenialApps  saveDenialApp(DenialApps denialApps);
    List<DenialApps> getDenialApps();
    DenialApps getDenialApp(long id);
    void deleteDenialApp(long id);
}
