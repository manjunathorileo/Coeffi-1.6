package com.dfq.coeffi.sam.privileges;

import com.dfq.coeffi.sam.module.Module;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class PrivilegesServiceImpl implements PrivilegesService {

    @Autowired
    private PrivilegesRepository privilegesRepository;

    @Override
    public Privileges create(Privileges privileges) {
        return privilegesRepository.save(privileges);
    }

    @Override
    public List<Privileges> getPrivileges() {
        return privilegesRepository.findAll();
    }

    @Override
    public Privileges getPrivilege(long id) {
        return privilegesRepository.findOne(id);
    }

    @Override
    public void isPrivilegeExists(String name) {}

    @Override
    public List<Privileges> getPrivilegesByModule(Module module) {
        return privilegesRepository.findByModule(module);
    }
}