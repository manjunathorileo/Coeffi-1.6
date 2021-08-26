package com.dfq.coeffi.visitor.Services;


import com.dfq.coeffi.visitor.Entities.VisitorTimeSlot;

import java.util.List;

public interface VisitorTimeSlotService
{
    VisitorTimeSlot saveTime(VisitorTimeSlot visitorTimeSlot);

    List<VisitorTimeSlot> getAllTime();

    void deleteTimeById(long id);
}
