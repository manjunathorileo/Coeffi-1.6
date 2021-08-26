package com.dfq.coeffi.StoreManagement.Repository;

import com.dfq.coeffi.StoreManagement.Entity.ProductName;
import com.dfq.coeffi.StoreManagement.Entity.ServiceDuration;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceDurationRepository extends JpaRepository<ServiceDuration,Long> {
}
