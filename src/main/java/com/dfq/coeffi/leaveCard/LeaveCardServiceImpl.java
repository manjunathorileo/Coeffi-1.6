package com.dfq.coeffi.leaveCard;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LeaveCardServiceImpl implements LeaveCardService {

    @Autowired
    LeaveCardRepository leaveCardRepository;

    @Override
    public LeaveCard save(LeaveCard leaveCard) {
        return leaveCardRepository.save(leaveCard);
    }

    @Override
    public LeaveCard get(long id) {
        return leaveCardRepository.findOne(id);
    }

    @Override
    public List<LeaveCard> getAll() {
        return leaveCardRepository.findAll();
    }

    @Override
    public List<LeaveCard> get(String month, String year) {
        return leaveCardRepository.findByMonthAndYear(month, year);
    }

    @Override
    public void delete(long id) {
        leaveCardRepository.delete(id);
    }
}
