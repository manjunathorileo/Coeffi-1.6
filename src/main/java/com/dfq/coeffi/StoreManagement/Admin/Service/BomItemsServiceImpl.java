package com.dfq.coeffi.StoreManagement.Admin.Service;

import com.dfq.coeffi.StoreManagement.Admin.Entity.BomItems;
import com.dfq.coeffi.StoreManagement.Admin.Repository.BomItemsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class BomItemsServiceImpl implements BomItemsService {

    @Autowired
    BomItemsRepository bomItemsRepository;
    @Override
    public BomItems saveBomItems(BomItems bomItems) {
        return bomItemsRepository.save(bomItems);
    }

    @Override
    public List<BomItems> getBomItems() {
        return bomItemsRepository.findAll();
    }

    @Override
    public void deleteBomItems(long id) {
        bomItemsRepository.delete(id);
    }
}
