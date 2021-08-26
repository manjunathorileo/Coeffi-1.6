package com.dfq.coeffi.vivo.service;

import com.dfq.coeffi.vivo.entity.Site;
import com.dfq.coeffi.vivo.repository.SiteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SiteServiceImpl implements SiteService {
    @Autowired
    SiteRepository siteRepository;

    @Override
    public Site saveSite(Site site) {
        return siteRepository.save(site);
    }

    @Override
    public List<Site> getAllSite() {
        return siteRepository.findAll();
    }

    @Override
    public Optional<Site> getSiteById(long id) {
        return siteRepository.findById(id);
    }

    @Override
    public void deleteSiteByid(long id) {
        System.out.println("H");
    }

    @Override
    public List<Site> getActiveSites() {
        return siteRepository.findByAvailable(true);
    }
}
