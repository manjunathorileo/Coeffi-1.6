package com.dfq.coeffi.StoreManagement.Repository;

import com.dfq.coeffi.StoreManagement.Entity.AdminConfiguration;
import com.dfq.coeffi.StoreManagement.Entity.BatchMaster;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminConfigurationRepository extends JpaRepository<AdminConfiguration,Long> {

}
