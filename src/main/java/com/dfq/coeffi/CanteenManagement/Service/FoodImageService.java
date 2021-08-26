package com.dfq.coeffi.CanteenManagement.Service;

import com.dfq.coeffi.CanteenManagement.Entity.FoodImage;
import org.springframework.web.multipart.MultipartFile;


public interface FoodImageService {
    FoodImage saveMenuFile(MultipartFile file);

    FoodImage getByMenuFileId(long fileId);
}
