package com.dfq.coeffi.service.hr;

import com.dfq.coeffi.entity.hr.employee.Qualification;
import org.springframework.stereotype.Service;

@Service
public interface QualificationService {
    Qualification getQualification(long id);
}
