package com.dfq.coeffi.vivo.service;

import com.dfq.coeffi.vivo.entity.Slot;

import java.util.List;

public interface SlotService {
    Slot save(Slot slot);

    List<Slot> getSlots();

    Slot getSlot(long id);



}
