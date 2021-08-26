package com.dfq.coeffi.visitor.Services;

import com.dfq.coeffi.visitor.Entities.AssignSecurity;
import com.dfq.coeffi.visitor.Entities.VisitorTimeSlot;

import java.util.List;

public interface AssignSecurityService
{
    AssignSecurity saveSecurity(AssignSecurity assignSecurity);

    List<AssignSecurity> getAllSecurity();

    void deleteSecurityById(long id);
}
