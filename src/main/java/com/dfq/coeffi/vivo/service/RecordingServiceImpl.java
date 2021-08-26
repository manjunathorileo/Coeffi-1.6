package com.dfq.coeffi.vivo.service;

import com.dfq.coeffi.vivo.entity.Recording;
import com.dfq.coeffi.vivo.repository.RecordingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RecordingServiceImpl implements RecordingService {

    @Autowired
    RecordingRepository recordingRepository;

    @Override
    public Recording save(Recording recording) {
        return recordingRepository.save(recording);
    }

    @Override
    public List<Recording> getAll() {
        return recordingRepository.findAll();
    }

    @Override
    public Recording get(long id) {
        return recordingRepository.findOne(id);
    }
}
