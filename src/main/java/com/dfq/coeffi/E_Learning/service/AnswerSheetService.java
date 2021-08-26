package com.dfq.coeffi.E_Learning.service;

import com.dfq.coeffi.E_Learning.modules.AnswerSheet;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public interface AnswerSheetService {

    List<AnswerSheet> saveAnswerSheet(List<AnswerSheet> answerSheet);

    List<AnswerSheet> getAnswerSheet(boolean status);

    Optional<AnswerSheet> getAnswerSheetById(long id);

    void deActivateById(long id);

//    ArrayList<AnswerSheet> getAnswerSheetByProductId(long id);

    ArrayList<AnswerSheet> getAnswerSheetByUserId(long id);


}