package com.dfq.coeffi.Gate.Service;

import com.dfq.coeffi.Gate.Entity.Gate;
import com.dfq.coeffi.Gate.Repository.GateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class GateServiceImpl implements GateService {

    @Autowired
    GateRepository gateRepository;
    @Override
    public Gate saveGate(Gate gate) {
        return gateRepository.save(gate) ;
    }

    @Override
    public List<Gate> getGates() {
        return gateRepository.findAll();
    }

    @Override
    public Gate getGateById(long id) {
        return gateRepository.findOne(id) ;
    }

    @Override
    public Gate getGateByNumber(String num) {
        return gateRepository.findByGateNumber(num);
    }

    @Override
    public void deleteGate(long id) {
        gateRepository.delete(id);

    }
}
