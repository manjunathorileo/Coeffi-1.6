package com.dfq.coeffi.StoreManagement.Service;

import com.dfq.coeffi.Expenses.Entities.Expenses;
import com.dfq.coeffi.StoreManagement.Entity.Materials;

import java.util.List;

public interface MaterialsService {
    Materials saveMaterial(Materials materials);
    Materials getMaterial(long id);
    List<Materials> getMaterials();
    List<Materials> save(List<Materials> materials);
    List<Materials> getMaterialsByEmployee(long employeeId);
}
