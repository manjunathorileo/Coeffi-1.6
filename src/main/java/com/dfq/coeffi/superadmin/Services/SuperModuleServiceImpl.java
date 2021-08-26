package com.dfq.coeffi.superadmin.Services;

import com.dfq.coeffi.superadmin.Entity.SuperModule;
import com.dfq.coeffi.superadmin.Repository.SuperModelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SuperModuleServiceImpl implements SuperModuleService
{
    @Autowired
    SuperModelRepository superModelRepository;


    @Override
    public SuperModule create(SuperModule superModule)
    {
        return superModelRepository.save(superModule);
    }

    @Override
    public List<SuperModule> getAllModule()
    {
        return superModelRepository.findAll();
    }

    @Override
    public SuperModule getModuleById(long id)
    {
        return superModelRepository.findOne(id);
    }
}
