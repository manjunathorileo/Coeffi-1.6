package com.dfq.coeffi.DenialApps.Services;

import com.dfq.coeffi.DenialApps.Entities.MustApps;

import java.util.List;

public interface MustAppService {
    MustApps saveMustApp(MustApps mustApps);
    List<MustApps> getMustApps();
    MustApps getMustApp(long id);
    void deleteMustApp(long id);
}
