package com.dfq.coeffi.advanceFoodManagementExtra;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EmpRechargeMasterRawServiceImpl implements EmpRechargeMasterRawService {
    @Autowired
    EmpRechargeMasterRawRepository empRechargeMasterRawRepository;
    @Override
    public void save(EmpRechargeMasterRaw empRechargeMasterRaw) {
        empRechargeMasterRawRepository.save(empRechargeMasterRaw);
    }

    @Override
    public EmpRechargeMasterRaw getById(long id) {
        return empRechargeMasterRawRepository.findOne(id);
    }
}
