package com.dfq.coeffi.visitor.Services;

import com.dfq.coeffi.visitor.Entities.AssignSecurity;
import com.dfq.coeffi.visitor.Repositories.AssignSecurityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class AssignSecurityServiceImpl implements AssignSecurityService
{
    @Autowired
    AssignSecurityRepository assignSecurityRepository;

    @Override
    public AssignSecurity saveSecurity(AssignSecurity assignSecurity)
    {
        return assignSecurityRepository.save(assignSecurity);
    }

    @Override
    public List<AssignSecurity> getAllSecurity()
    {
        return assignSecurityRepository.findAll();
    }

    @Override
    public void deleteSecurityById(long id)
    {
        assignSecurityRepository.delete(id);

    }
}
