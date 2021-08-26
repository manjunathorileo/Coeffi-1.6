package com.dfq.coeffi.Oqc.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OqcUserServiceImpl implements OqcUserService {

    @Autowired
    private OqcUserRepository oqcUserRepository;

    @Override
    public OqcUser createOqcUser(OqcUser oqcUser) {
        return oqcUserRepository.save(oqcUser);
    }

    @Override
    public List<OqcUser> getAllOqcUser() {
        return oqcUserRepository.findAll();
    }

    @Override
    public OqcUser getOqcUser(long id) {
        return oqcUserRepository.findOne(id);
    }

    @Override
    public List<OqcUser> getOqcUserByProductByProductionLine(long productId, long productionLineId) {
        return oqcUserRepository.findByProductByProductionLine(productId, productionLineId);
    }
}
