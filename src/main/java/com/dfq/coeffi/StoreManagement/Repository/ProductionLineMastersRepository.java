package com.dfq.coeffi.StoreManagement.Repository;

import com.dfq.coeffi.StoreManagement.Entity.ProductionItemQualityCheck;
import com.dfq.coeffi.StoreManagement.Entity.ProductionLineMasters;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductionLineMastersRepository extends JpaRepository<ProductionLineMasters,Long> {
}
