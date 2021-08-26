package com.dfq.coeffi.E_Learning.repository;

import com.dfq.coeffi.E_Learning.modules.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.ArrayList;

@EnableJpaRepositories
@Transactional
public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("UPDATE Product product SET product.status = false where product.id =:id")
    @Modifying
    void deActivate(@Param("id") long id);

    ArrayList<Product> findByStatus(boolean status);

}
