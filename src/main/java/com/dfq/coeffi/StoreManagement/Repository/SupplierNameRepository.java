package com.dfq.coeffi.StoreManagement.Repository;

import com.dfq.coeffi.StoreManagement.Entity.RequestNumber;
import com.dfq.coeffi.StoreManagement.Entity.SupplierName;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SupplierNameRepository extends JpaRepository<SupplierName,Long> {
}
