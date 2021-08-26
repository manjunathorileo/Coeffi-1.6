package com.dfq.coeffi.foodManagement;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@EnableJpaRepositories
@Transactional
public interface FoodTimeRepository extends JpaRepository<FoodTimeMaster,Long> {

    FoodTimeMaster findByFoodType(String foodType);
}
