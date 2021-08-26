package com.dfq.coeffi.vivo.service;

import com.dfq.coeffi.vivo.entity.Recording;

import java.util.List;

public interface RecordingService {
    Recording save(Recording recording);
    List<Recording> getAll();
    Recording get(long id);

}
