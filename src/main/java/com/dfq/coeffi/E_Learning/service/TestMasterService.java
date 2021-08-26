package com.dfq.coeffi.E_Learning.service;

import com.dfq.coeffi.E_Learning.modules.TestMaster;

import java.util.List;

public interface TestMasterService {
    TestMaster saveUpdateTestMaster(TestMaster testMaster, long pid);

    List<TestMaster> getTestMaster();

    TestMaster getTestMasterById(long id);

    List<TestMaster> getTestMasterByProductId(long productId);

    void deActiveStatus(long id);
}

