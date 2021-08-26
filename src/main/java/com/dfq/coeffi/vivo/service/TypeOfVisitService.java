package com.dfq.coeffi.vivo.service;

import com.dfq.coeffi.vivo.entity.TypeOfVisit;

import java.util.List;
import java.util.Optional;

public interface TypeOfVisitService
{
    TypeOfVisit saveVisitor(TypeOfVisit typeOfVisit);

    List<TypeOfVisit> getAllVisitors();

    Optional<TypeOfVisit> getVisitorsById(long id);

    void deleteVisitorsByid(long id);
}
