package com.dfq.coeffi.SOPDetails.Event.eventCompletion;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

@EnableJpaRepositories
public interface EventCompletionRepository extends JpaRepository<EventCompletion,Long>
{
    @Query("SELECT e FROM EventCompletion e WHERE e.sopType.id = :sopTypeId AND e.sopCategory.id = :digitalSopId AND e.eventMaster.id = :eventId ")
    List<EventCompletion> findBySopTypeBydigitalSopByEvent(@Param("sopTypeId") long sopTypeId, @Param("digitalSopId") long digitalSopId, @Param("eventId") long eventId);

    Optional<EventCompletion> findById(long id);

    @Query("SELECT e FROM EventCompletion e WHERE e.sopType.id = :sopTypeId AND e.sopCategory.id = :sopCategoryId")
    List<EventCompletion> findBySopTypeBySopCategory(@Param("sopTypeId") long sopTypeId, @Param("sopCategoryId") long sopCategoryId);
}
