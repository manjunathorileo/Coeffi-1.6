package com.dfq.coeffi.E_Learning.service;

import com.dfq.coeffi.E_Learning.modules.AnswerSheet;
import com.dfq.coeffi.E_Learning.repository.AnswerSheetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.Optional.ofNullable;

@Service("AnswerSheet")
public class AnswerSheetServiceImpl implements AnswerSheetService {

    @Autowired
    private AnswerSheetRepository answerSheetRepository;

    @Override
    public List<AnswerSheet> saveAnswerSheet(List<AnswerSheet> answerSheet) {

        return answerSheetRepository.save(answerSheet);

    }

    @Override
    public List<AnswerSheet> getAnswerSheet(boolean status) {
        return answerSheetRepository.findByStatus(status);
    }

    @Override
    public Optional<AnswerSheet> getAnswerSheetById(long id) {
        return ofNullable(answerSheetRepository.findOne(id));
    }


    @Override
    public void deActivateById(long id) {

        answerSheetRepository.deleteById(id);
    }

//    @Override
//    public ArrayList<AnswerSheet> getAnswerSheetByProductId(long id) {
//        return answerSheetRepository.findByProductId(id);
//    }

    @Override
    public ArrayList<AnswerSheet> getAnswerSheetByUserId(long id) {
        return answerSheetRepository.findByUserId(id);
    }


}
