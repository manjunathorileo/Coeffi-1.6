package com.dfq.coeffi.E_Learning.repository;

import com.dfq.coeffi.E_Learning.modules.AnswerSheet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Transactional
@EnableJpaRepositories
public interface AnswerSheetRepository extends JpaRepository<AnswerSheet, Long> {

    List<AnswerSheet> findByStatus(boolean status);

//    @Query("UPDATE AnswerSheet answerSheet SET answerSheet.status=false where answerSheet.id=:id")
//    @Modifying
//    void deActivateById(@Param("id") long id);

//    @Query("SELECT answerSheet from AnswerSheet answerSheet where answerSheet.product.id= :id")
//    ArrayList<AnswerSheet> findByProductId(@Param("id") long id);

    @Query("SELECT answerSheet from AnswerSheet answerSheet where answerSheet.user.id=:id")
    ArrayList<AnswerSheet> findByUserId(@Param("id") long id);


    void deleteById(long id);
}