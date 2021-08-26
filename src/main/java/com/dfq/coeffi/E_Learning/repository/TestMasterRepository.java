package com.dfq.coeffi.E_Learning.repository;

import com.dfq.coeffi.E_Learning.modules.TestMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TestMasterRepository extends JpaRepository<TestMaster, Long> {
    @Query("SELECT testmaster from TestMaster testmaster where testmaster.product.id=:id ")
    List<TestMaster> findByProductId(@Param("id") long productId);

    void deleteById(long id);
}
