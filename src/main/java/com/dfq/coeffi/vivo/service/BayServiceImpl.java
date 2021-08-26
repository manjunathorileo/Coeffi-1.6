package com.dfq.coeffi.vivo.service;

import com.dfq.coeffi.vivo.entity.Bay;
import com.dfq.coeffi.vivo.entity.Slot;
import com.dfq.coeffi.vivo.repository.BayRepository;
import com.dfq.coeffi.vivo.repository.SlotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BayServiceImpl implements BayService, SlotService {
    @Autowired
    BayRepository bayRepository;
    @Autowired
    SlotRepository slotRepository;

    @Override
    public Bay saveBay(Bay bay) {
        return bayRepository.save(bay);
    }

    @Override
    public List<Bay> getBays() {
        return bayRepository.findAll();
    }

    @Override
    public Bay getBay(long id) {
        return bayRepository.findOne(id);
    }

    @Override
    public List<Bay> getByBayNumber(String bayNumber) {
        return bayRepository.findByBayNumber(bayNumber);
    }

    @Override
    public Bay getBayByType(String bayNumber, String bayVehicleType) {
        return bayRepository.findByBayNumberAndBayVehicleType(bayNumber,bayVehicleType);
    }

    @Override
    public List<Bay> getByType(String typeOfVehicle) {
        return bayRepository.findByBayVehicleType(typeOfVehicle);
    }

    @Override
    public Slot save(Slot slot) {
        return slotRepository.save(slot);
    }

    @Override
    public List<Slot> getSlots() {
        return slotRepository.findAll();
    }

    @Override
    public Slot getSlot(long id) {
        return slotRepository.findOne(id);
    }
}
