package com.dfq.coeffi.vivo.service;

import com.dfq.coeffi.vivo.entity.TypeOfVisit;
import com.dfq.coeffi.vivo.repository.TypeOfVisitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TypeOfVisitServiceImpl implements TypeOfVisitService
{
    @Autowired
    TypeOfVisitRepository typeOfVisitRepository;

    @Override
    public TypeOfVisit saveVisitor(TypeOfVisit typeOfVisit)
    {
        return typeOfVisitRepository.save(typeOfVisit);
    }

    @Override
    public List<TypeOfVisit> getAllVisitors()
    {
        return typeOfVisitRepository.findAll();
    }

    @Override
    public Optional<TypeOfVisit> getVisitorsById(long id)
    {
        return typeOfVisitRepository.findById(id);
    }

    @Override
    public void deleteVisitorsByid(long id)
    {
        typeOfVisitRepository.deleteById(id);
    }
}
