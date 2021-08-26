package com.dfq.coeffi.Oqc.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CheckListAssignedServiceImpl implements CheckListAssignedService {

    @Autowired
    private CheckListAssignedRepository checkListAssignedRepository;

    @Override
    public CheckListAssigned createCheckListAssigned(CheckListAssigned checkListAssigned) {
        return checkListAssignedRepository.save(checkListAssigned);
    }

    @Override
    public List<CheckListAssigned> getAllCheckListAssigned() {
        return checkListAssignedRepository.findAll();
    }

    @Override
    public CheckListAssigned getCheckListAssigned(long id) {
        return checkListAssignedRepository.findOne(id);
    }
}
