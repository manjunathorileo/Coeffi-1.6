package com.dfq.coeffi.SOPDetails.adherence;

import com.dfq.coeffi.SOPDetails.adherence.Adherence;

import java.util.Date;
import java.util.List;

public interface AdherenceService
{
    //User adherence module submit
    Adherence saveAdherence(Adherence adherence);

    //get all adherence
    List<Adherence> getAdherence();

    //get all adherence by id
    Adherence getAdherenceById(long id);

    //delete adherence by id
    void deleteAdherenceByid(long id);

    //user complied reports by user id
    List<Adherence> getByUserId(long uid);

    //Admin reports by soplist and userid
    List<Adherence> getbyDigitalSopIdAndUserId(long digitalSopId, long userId);

    //admin reports by soplist and startdate, enddate and userid
    List<Adherence> getByFilter(long sopId, Date startDate, Date endDate, long userId);
}
