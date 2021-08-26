package com.dfq.coeffi.SOPDetails.Event.eventSopBinding;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

@EnableJpaRepositories
public interface EventSopBindRepository extends JpaRepository<EventSopBind,Long>{

    @Query("SELECT e FROM EventSopBind e where e.sopType.id = :sopTypeId AND e.sopCategory.id = :digitalSopId AND e.status = true")
    List<EventSopBind> findBySopTypeBydigitalSOP(@Param("sopTypeId") long sopTypeId, @Param("digitalSopId") long digitalSopId);

    Optional<EventSopBind> findById(long id);
}
