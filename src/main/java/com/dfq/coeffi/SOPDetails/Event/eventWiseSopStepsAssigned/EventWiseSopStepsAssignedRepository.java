package com.dfq.coeffi.SOPDetails.Event.eventWiseSopStepsAssigned;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional
public interface EventWiseSopStepsAssignedRepository extends JpaRepository<EventWiseSopStepsAssigned, Long> {

    Optional<EventWiseSopStepsAssigned> findById(long id);
}