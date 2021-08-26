package com.dfq.coeffi.Gate.Service;

import com.dfq.coeffi.Gate.Entity.Gate;

import java.util.List;

public interface GateService {
    Gate saveGate(Gate gate);
    List<Gate> getGates();
    Gate getGateById(long id);
    Gate getGateByNumber(String num);
    void deleteGate(long id);

}
