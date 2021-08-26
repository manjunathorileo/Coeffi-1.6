package com.dfq.coeffi.master.compensatoryLeave;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CompensatoryLeaveServiceImpl implements CompensatoryLeaveService {


    private CompensatoryLeaveRepository compensatoryLeaveRepository;

    @Autowired
    public CompensatoryLeaveServiceImpl(CompensatoryLeaveRepository compensatoryLeaveRepository){
        this.compensatoryLeaveRepository = compensatoryLeaveRepository;
    }

    @Override
    public CompensatoryLeave saveCompensatoryLeave(CompensatoryLeave compensatoryLeave) {
        return compensatoryLeaveRepository.save(compensatoryLeave);
    }

    @Override
    public List<CompensatoryLeave> getAllCompensatoryLeaves() {
        return compensatoryLeaveRepository.findAll();
    }

    @Override
    public List<CompensatoryLeave> getCompensatoryLeavesByEmployee(long employeeId) {
        return compensatoryLeaveRepository.findByEmployeeId(employeeId);
    }

    @Override
    public Optional<CompensatoryLeave> getCompensatoryLeaveById(long id) {
        return Optional.ofNullable(compensatoryLeaveRepository.findOne(id));
    }
}
