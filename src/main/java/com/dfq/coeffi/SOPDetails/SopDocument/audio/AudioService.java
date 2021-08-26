package com.dfq.coeffi.SOPDetails.SopDocument.audio;

import com.dfq.coeffi.SOPDetails.SOPCategory.SopCategory;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface AudioService
{
    //Audio file save API
    Audio saveAudio(MultipartFile file, String desc, long did);

    //Audio file view by id
    Audio getAudioFileById(long fileId);

    //view all Audio files
    List<Audio> getAllAudio();

    //get Audio file by sopid
    Audio getAudioBySopId(SopCategory SOPCategory);

    //delete audio file by id
    void deleteWAudioByid(long id);
}
