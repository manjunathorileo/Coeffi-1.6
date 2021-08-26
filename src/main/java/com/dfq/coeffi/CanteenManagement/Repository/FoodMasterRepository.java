package com.dfq.coeffi.CanteenManagement.Repository;

import com.dfq.coeffi.CanteenManagement.Entity.FoodMaster;
import com.dfq.coeffi.CanteenManagement.Entity.FoodTimeMasterAdv;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@EnableJpaRepositories
@Transactional
public interface FoodMasterRepository extends JpaRepository<FoodMaster, Long> {
    FoodMaster findById(long id);

    List<FoodMaster> findByFoodType(FoodTimeMasterAdv foodType);
}
