package com.dfq.coeffi.StoreManagement.Admin.Repository;

import com.dfq.coeffi.StoreManagement.Admin.Entity.Bom;
import com.dfq.coeffi.StoreManagement.Admin.Entity.BomItems;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BomItemsRepository extends JpaRepository<BomItems,Long> {
}
