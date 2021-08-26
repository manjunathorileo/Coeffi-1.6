package com.dfq.coeffi.StoreManagement.Repository;

import com.dfq.coeffi.StoreManagement.Entity.ProductName;
import com.dfq.coeffi.StoreManagement.Entity.RequestNumber;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductNameRepository extends JpaRepository<ProductName,Long> {
}
