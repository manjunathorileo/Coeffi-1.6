package com.dfq.coeffi.visitor.Services;

import com.dfq.coeffi.visitor.Entities.VisitorCategory;
import com.dfq.coeffi.visitor.Entities.VisitorTimeSlot;
import com.dfq.coeffi.visitor.Repositories.VisitorTimeSlotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class VisitorTimeSlotServiceImpl implements VisitorTimeSlotService
{
    @Autowired
    VisitorTimeSlotRepository visitorTimeSlotRepository ;


    @Override
    public VisitorTimeSlot saveTime(VisitorTimeSlot visitorTimeSlot)
    {
        return visitorTimeSlotRepository.save(visitorTimeSlot) ;
    }

    @Override
    public List<VisitorTimeSlot> getAllTime()
    {
        return visitorTimeSlotRepository.findAll();
    }

    @Override
    public void deleteTimeById(long id)
    {
        visitorTimeSlotRepository.delete(id);

    }
}
