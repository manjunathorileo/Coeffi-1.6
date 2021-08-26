package com.dfq.coeffi.StoreManagement.Service;

import com.dfq.coeffi.StoreManagement.Entity.Materials;
import com.dfq.coeffi.StoreManagement.Repository.MaterialsRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MaterialsServiceImpl implements MaterialsService  {
   @Autowired
    MaterialsRequestRepository materialsRequestRepository;
    @Override
    public Materials saveMaterial(Materials materials) {
        return materialsRequestRepository.save(materials) ;
    }

    @Override
    public Materials getMaterial(long id) {
        return materialsRequestRepository.findOne(id) ;
    }

    @Override
    public List<Materials> getMaterials() {
        return materialsRequestRepository.findAll();
    }

    @Override
    public List<Materials> save(List<Materials> materials) {
        return materialsRequestRepository.save(materials) ;
    }

    @Override
    public List<Materials> getMaterialsByEmployee(long employeeId) {
        return materialsRequestRepository.findByEmployeeId(employeeId) ;
    }
}
