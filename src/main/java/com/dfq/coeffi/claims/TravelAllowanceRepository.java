package com.dfq.coeffi.claims;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@EnableJpaRepositories
@Transactional
public interface TravelAllowanceRepository extends JpaRepository<TravelAllowance,Long> {

    List<TravelAllowance> findByTravellingApprovalStatus(TravellingApprovalStatus travellingApprovalStatus);
    Optional<TravelAllowance> findById(long id);
}
