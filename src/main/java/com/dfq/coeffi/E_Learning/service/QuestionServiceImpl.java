package com.dfq.coeffi.E_Learning.service;

import com.dfq.coeffi.E_Learning.modules.Question;
import com.dfq.coeffi.E_Learning.repository.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuestionServiceImpl implements QuestionService {
    @Autowired
    private QuestionRepository questionRepository;

    @Override
    public Question saveUpdateQuestion(Question question) {
        question.setStatus(true);
        questionRepository.save(question);
        return question;
    }


    @Override
    public List<Question> getQuestions() {
        return questionRepository.findAll();
    }

    @Override
    public Question getQuestionByIdAndProductId(long id, long productId) {
        return questionRepository.findByIdAndProductId(id, productId);
    }

    @Override
    public List<Question> getQuestionByProductId(long productId) {
        return questionRepository.findByProductId(productId);
    }

    @Override
    public void deleteById(long id) {
        questionRepository.deleteById(id);

    }

    @Override
    public void deActiveStatus(long id) {
        questionRepository.deactiveStatus(id);

    }
}
