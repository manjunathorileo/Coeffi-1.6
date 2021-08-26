package com.dfq.coeffi.SOPDetails.adherence;

import com.dfq.coeffi.SOPDetails.Repositories.AdherenceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class AdherenceServiceimpl implements AdherenceService
{

    @Autowired
    AdherenceRepository adherenceRepository;

    //User adherence module submit
    @Override
    public Adherence saveAdherence(Adherence adherence)
    {

        return adherenceRepository.save(adherence);
    }

    //get all adherence
    @Override
    public List<Adherence> getAdherence()
    {
        return adherenceRepository.findAll();
    }

    //get all adherence by id
    @Override
    public Adherence getAdherenceById(long id)
    {
        return adherenceRepository.findOne(id);
    }

    //delete adherence by id
    @Override
    public void deleteAdherenceByid(long id)
    {
        adherenceRepository.delete(id);

    }


    //user complied reports by user id
    @Override
    public List<Adherence> getByUserId(long uid)
    {
        return adherenceRepository.findByUserId(uid);
    }

    //Admin reports by soplist and userid
    @Override
    public List<Adherence> getbyDigitalSopIdAndUserId(long digitalSopId, long userId)
    {
        return adherenceRepository.findByDigitalSopIdAndUserId(digitalSopId,userId);
    }

    //admin reports by soplist and startdate, enddate and userid
    @Override
    public List<Adherence> getByFilter(long sopId, Date startDate, Date endDate, long userId)
    {
        return adherenceRepository.filterAdherence(sopId,startDate,endDate,userId);
    }


}
