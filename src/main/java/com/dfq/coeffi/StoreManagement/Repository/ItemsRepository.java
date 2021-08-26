package com.dfq.coeffi.StoreManagement.Repository;

import com.dfq.coeffi.StoreManagement.Entity.Items;
import com.dfq.coeffi.StoreManagement.Entity.Materials;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface ItemsRepository extends JpaRepository<Items,Long> {

    Items findByItemNumber(long id);
}
