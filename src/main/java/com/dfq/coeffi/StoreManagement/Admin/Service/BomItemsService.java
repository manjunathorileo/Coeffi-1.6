package com.dfq.coeffi.StoreManagement.Admin.Service;

import com.dfq.coeffi.StoreManagement.Admin.Entity.BomItems;

import java.util.List;

public interface BomItemsService {

    BomItems saveBomItems(BomItems bomItems);
    List<BomItems> getBomItems();
    void deleteBomItems(long id);
}
