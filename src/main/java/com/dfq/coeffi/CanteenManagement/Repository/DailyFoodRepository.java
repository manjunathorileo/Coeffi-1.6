package com.dfq.coeffi.CanteenManagement.Repository;

import com.dfq.coeffi.CanteenManagement.Entity.CounterDetailsAdv;
import com.dfq.coeffi.CanteenManagement.Entity.DailyFoodMenu;
import com.dfq.coeffi.CanteenManagement.Entity.FoodTimeMasterAdv;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@EnableJpaRepositories
@Transactional
public interface DailyFoodRepository extends JpaRepository<DailyFoodMenu,Long> {

    DailyFoodMenu findById(long id);

    List<DailyFoodMenu> findByCounterDetailsAdv(CounterDetailsAdv counterDetailsAdv);

    @Query("SELECT dailyFoodMenu FROM DailyFoodMenu dailyFoodMenu WHERE dailyFoodMenu.counterDetailsAdv.id = :counterDetailsId AND dailyFoodMenu.foodType.id = :foodTypeId")
    List<DailyFoodMenu> findByCounterDetailsByFoodType(@Param("counterDetailsId") long counterDetailsId, @Param("foodTypeId") long foodTypeId);

    @Query("SELECT dailyFoodMenu FROM DailyFoodMenu dailyFoodMenu " +
            "where (dailyFoodMenu.effectiveFrom <= :today AND dailyFoodMenu.effictiveTo >= :today) " +
            "AND dailyFoodMenu.foodType.id = :foodTypeId AND dailyFoodMenu.counterDetailsAdv.id = :counterId")
    List<DailyFoodMenu> findByTodaysDate(@Param("today") Date today, @Param("foodTypeId") long foodTypeId, @Param("counterId") long counterId);

    List<DailyFoodMenu> findByFoodType(FoodTimeMasterAdv foodType);

    @Query("SELECT dailyFoodMenu FROM DailyFoodMenu dailyFoodMenu " +
            "where (dailyFoodMenu.effectiveFrom <= :today AND dailyFoodMenu.effictiveTo >= :today) " +
            "AND dailyFoodMenu.foodType.id = :foodTypeId ")
    List<DailyFoodMenu> findByTodaysDateByFoodtype(@Param("today") Date today, @Param("foodTypeId") long foodTypeId);
}
