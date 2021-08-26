//package com.dfq.coeffi.LossAnalysis.machine;
//
//import com.dfq.coeffi.LossAnalysis.productionLine.ProductionLineMaster;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
//
//import java.util.List;
//import java.util.Optional;
//
//@EnableJpaRepositories
//public interface CurrentProductionRateRepository extends JpaRepository<CurrentProductionRate,Long> {
//
//    Optional<CurrentProductionRate> findById(long id);
//
//    List<CurrentProductionRate> findByProductionLineMaster(ProductionLineMaster productionLineMaster);
//}