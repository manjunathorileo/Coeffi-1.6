package com.dfq.coeffi.Oqc.User;

import com.dfq.coeffi.Oqc.User.OqcUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OqcUserRepository extends JpaRepository<OqcUser,Long> {

    @Query("SELECT oqc FROM OqcUser oqc where oqc.productName.id = :productId AND oqc.productionLineMaster.id = :productionLineId " +
            " ORDER BY oqc.id ASC")
    List<OqcUser> findByProductByProductionLine(@Param("productId") long productId, @Param("productionLineId") long productionLineId);
}
