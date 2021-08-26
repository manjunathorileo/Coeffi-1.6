package com.dfq.coeffi.FeedBackManagement.Services;

import com.dfq.coeffi.CanteenManagement.Entity.FoodImage;
import com.dfq.coeffi.FeedBackManagement.Entity.GradeImage;
import org.springframework.web.multipart.MultipartFile;

public interface GradeImageService {
    GradeImage saveGradeFile(MultipartFile file);

    GradeImage getByGradeFileId(long fileId);
}
