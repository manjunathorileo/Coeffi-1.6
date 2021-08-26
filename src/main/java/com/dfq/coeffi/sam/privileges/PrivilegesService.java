package com.dfq.coeffi.sam.privileges;

import com.dfq.coeffi.sam.module.Module;
import java.util.List;

public interface PrivilegesService {

    Privileges create(Privileges privileges);
    List<Privileges> getPrivileges();
    Privileges getPrivilege(long id);
    void isPrivilegeExists(String name);
    List<Privileges> getPrivilegesByModule(Module module);
}