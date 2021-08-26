package com.dfq.coeffi.SOPDetails.SopDocument.video;

import com.dfq.coeffi.SOPDetails.SOPCategory.SopCategory;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface VideoService
{
    //video file save API
    Video saveVideo(MultipartFile file, String desc, long did);

    //video file view by id
    Video getVideoFileById(long fileId);

    //view all video files
    List<Video> getAllVideo();

    //get video file by sopid
    Video getVideoBySopId(SopCategory SOPCategory);

    //delete video file by id
    void deleteVideoByid(long id);
}
