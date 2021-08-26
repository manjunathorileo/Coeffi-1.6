package com.dfq.coeffi.servicesimpl.hr;

import com.dfq.coeffi.entity.hr.employee.Qualification;
import com.dfq.coeffi.repository.hr.QualificationRepository;
import com.dfq.coeffi.service.hr.QualificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class QualificationServiceImpl implements QualificationService {

    @Autowired
    private QualificationRepository qualificationRepository;

    @Override
    public Qualification getQualification(long id) {
        return qualificationRepository.findOne(id);
    }
}
