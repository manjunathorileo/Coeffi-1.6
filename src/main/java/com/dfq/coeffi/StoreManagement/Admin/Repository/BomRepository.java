package com.dfq.coeffi.StoreManagement.Admin.Repository;

import com.dfq.coeffi.StoreManagement.Admin.Entity.Bom;
import com.dfq.coeffi.StoreManagement.Admin.Entity.ItemCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BomRepository extends JpaRepository<Bom,Long> {
}
