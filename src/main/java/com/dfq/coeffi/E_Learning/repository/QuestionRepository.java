package com.dfq.coeffi.E_Learning.repository;

import com.dfq.coeffi.E_Learning.modules.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.List;

@Transactional
@EnableJpaRepositories
public interface QuestionRepository extends JpaRepository<Question, Long> {
    @Query("SELECT q from Question q where q.product.id=:id")
    @Modifying
    List<Question> findByProductId(@Param("id") long id);

    @Query("UPDATE Question question SET question.status=false where question.id=:id")
    @Modifying
    void deactiveStatus(@Param("id") long id);

    @Query("SELECT q from Question q where q.id=:id AND q.product.id=:productId")
    Question findByIdAndProductId(@Param("id") long id, @Param("productId") long productId);

    void deleteById(long id);
}

