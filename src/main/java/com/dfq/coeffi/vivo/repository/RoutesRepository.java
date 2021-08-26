package com.dfq.coeffi.vivo.repository;


import com.dfq.coeffi.vivo.entity.Routes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import javax.transaction.Transactional;
import java.util.Optional;

@EnableJpaRepositories
@Transactional
public interface RoutesRepository extends JpaRepository<Routes,Long>
{

    Optional<Routes> findById(long id);

    void deleteById(long id);
}
