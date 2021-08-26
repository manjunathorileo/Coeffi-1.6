package com.dfq.coeffi.Oqc.Admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CheckListMasterServiceImpl implements CheckListMasterService {

    @Autowired
    private CheckListMasterRepository checkListMasterRepository;


    @Override
    public CheckListMaster createCheckListMaster(CheckListMaster checkListMaster) {
        return checkListMasterRepository.save(checkListMaster);
    }

    @Override
    public List<CheckListMaster> getAllCheckListMaster() {
        return checkListMasterRepository.findAll();
    }

    @Override
    public CheckListMaster getCheckListMaster(long id) {
        return checkListMasterRepository.findOne(id);
    }
}
