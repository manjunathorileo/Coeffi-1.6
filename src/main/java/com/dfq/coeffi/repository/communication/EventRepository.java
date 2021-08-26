package com.dfq.coeffi.repository.communication;

/**
 * @Auther H Kapil Kumar on 7/3/18.
 * @Company Orileo Technologies
 */

import com.dfq.coeffi.entity.communication.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import javax.transaction.Transactional;
import java.util.List;

@Transactional
@EnableJpaRepositories
public interface EventRepository extends JpaRepository<Event, Long>
{
    List<Event> findByEmployeeId(long employeeId);
}
