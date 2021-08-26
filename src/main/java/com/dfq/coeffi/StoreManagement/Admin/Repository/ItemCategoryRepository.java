package com.dfq.coeffi.StoreManagement.Admin.Repository;

import com.dfq.coeffi.StoreManagement.Admin.Entity.ItemCategory;
import com.dfq.coeffi.StoreManagement.Admin.Entity.MinPercentage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemCategoryRepository extends JpaRepository<ItemCategory,Long> {
}
