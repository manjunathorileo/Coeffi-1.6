package com.dfq.coeffi.StoreManagement.Admin.Service;

import com.dfq.coeffi.StoreManagement.Admin.Entity.Bom;

import java.util.List;

public interface BomService {

    Bom saveBom(Bom bom);
    List<Bom> getBom();
    void deleteBom(long id);
    Bom getBomById(long id);


}
