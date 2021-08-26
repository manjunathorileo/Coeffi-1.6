package com.dfq.coeffi.E_Learning.service;

import com.dfq.coeffi.E_Learning.modules.Question;

import java.util.List;

public interface QuestionService {
    public Question saveUpdateQuestion(Question question);

    List<Question> getQuestions();

    Question getQuestionByIdAndProductId(long id, long productId);

    List<Question> getQuestionByProductId(long productId);

    void deleteById(long id);

    void deActiveStatus(long id);

}
