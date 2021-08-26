package com.dfq.coeffi.vivo.service;


import com.dfq.coeffi.vivo.entity.Site;

import java.util.List;
import java.util.Optional;

public interface SiteService
{
    Site saveSite(Site site);

    List<Site> getAllSite();

    Optional<Site> getSiteById(long id);

    void deleteSiteByid(long id);

    List<Site> getActiveSites();
}
