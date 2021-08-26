package com.dfq.coeffi.vivo.repository;

import com.dfq.coeffi.vivo.entity.TypeOfVehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import javax.transaction.Transactional;
import java.util.Optional;

@EnableJpaRepositories
@Transactional
public interface TypeOfVehicleRepository extends JpaRepository<TypeOfVehicle,Long> {

    Optional<TypeOfVehicle> findById(long id);

    void deleteById(long id);
}
