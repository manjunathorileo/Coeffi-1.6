package com.dfq.coeffi.sam.module;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ModuleServiceImpl implements ModuleService {

    @Autowired
    private ModuleRepository moduleRepository;

    @Override
    public Module create(Module module) {
        return moduleRepository.save(module);
    }

    @Override
    public List<Module> getModules() {
        return moduleRepository.findByStatus(true);
    }

    @Override
    public Module getModule(long id) {
        return moduleRepository.findOne(id);
    }

    @Override
    public void isModuleExists(String name) {

    }

}