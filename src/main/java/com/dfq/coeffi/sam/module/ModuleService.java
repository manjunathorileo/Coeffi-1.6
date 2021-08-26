package com.dfq.coeffi.sam.module;

import java.util.List;

public interface ModuleService {

    Module create(Module module);
    List<Module> getModules();
    Module getModule(long id);
    void isModuleExists(String name);
}