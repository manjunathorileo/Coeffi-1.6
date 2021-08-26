package com.dfq.coeffi.StoreManagement.Admin.Repository;

import com.dfq.coeffi.StoreManagement.Admin.Entity.StoreApproval;
import com.dfq.coeffi.StoreManagement.Entity.AdminConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoreApprovalRepository extends JpaRepository<StoreApproval,Long> {
}
