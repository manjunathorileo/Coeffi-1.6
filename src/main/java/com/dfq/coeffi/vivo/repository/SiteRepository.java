package com.dfq.coeffi.vivo.repository;

import com.dfq.coeffi.vivo.entity.Site;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SiteRepository extends JpaRepository<Site,Long>
{

    Optional<Site> findById(long id);

    List<Site> findByAvailable(boolean available);
}
