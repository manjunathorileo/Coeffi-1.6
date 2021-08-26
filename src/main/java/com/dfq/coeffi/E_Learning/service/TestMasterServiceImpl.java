package com.dfq.coeffi.E_Learning.service;

import com.dfq.coeffi.E_Learning.modules.Product;
import com.dfq.coeffi.E_Learning.modules.TestMaster;
import com.dfq.coeffi.E_Learning.repository.TestMasterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TestMasterServiceImpl implements TestMasterService {

    @Autowired
    TestMasterRepository testMasterRepository;
    @Autowired
    ProductService productService;

    @Override
    public TestMaster saveUpdateTestMaster(TestMaster testMaster, long pid) {
        Optional<Product> product = productService.getProductById(pid);
        testMaster.setProduct(product.get());
        return testMasterRepository.save(testMaster);
    }

    @Override
    public List<TestMaster> getTestMaster() {
        return testMasterRepository.findAll();
    }

    @Override
    public TestMaster getTestMasterById(long id) {
        return testMasterRepository.findOne(id);
    }

    @Override
    public List<TestMaster> getTestMasterByProductId(long productId) {
        return testMasterRepository.findByProductId(productId);
    }

    @Override
    public void deActiveStatus(long id) {
        testMasterRepository.delete(id);

    }
}

