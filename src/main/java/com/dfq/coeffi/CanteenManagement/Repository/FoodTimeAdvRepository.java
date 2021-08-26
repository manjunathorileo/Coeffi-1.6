package com.dfq.coeffi.CanteenManagement.Repository;

import com.dfq.coeffi.CanteenManagement.Entity.FoodTimeMasterAdv;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.Transactional;

@EnableJpaRepositories
@Transactional
public interface FoodTimeAdvRepository extends JpaRepository<FoodTimeMasterAdv,Long> {

    FoodTimeMasterAdv findByFoodType(String foodType);

    FoodTimeMasterAdv findById(long id);
}
