package com.dfq.coeffi.superadmin.Services;

import com.dfq.coeffi.superadmin.Entity.SuperModule;

import java.util.List;

public interface SuperModuleService
{
    SuperModule create(SuperModule superModule);

    List<SuperModule> getAllModule();

    SuperModule getModuleById(long id);
}
