package com.dfq.coeffi.StoreManagement.Admin.Service;

import com.dfq.coeffi.StoreManagement.Admin.Entity.Bom;
import com.dfq.coeffi.StoreManagement.Admin.Repository.BomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
@Service
public class BomServiceImpl implements BomService{

    @Autowired
    BomRepository bomRepository;

    @Override
    public Bom saveBom(Bom bom) {
        return bomRepository.save(bom);
    }

    @Override
    public List<Bom> getBom() {
        return bomRepository.findAll();
    }

    @Override
    public void deleteBom(long id) {
        bomRepository.delete(id);
    }

    @Override
    public Bom getBomById(long id) {
        return bomRepository.findOne(id);
    }
}
