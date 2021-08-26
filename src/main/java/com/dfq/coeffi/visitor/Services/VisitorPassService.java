package com.dfq.coeffi.visitor.Services;

import com.dfq.coeffi.visitor.Entities.Visitor;
import com.dfq.coeffi.visitor.Entities.VisitorPass;
import com.dfq.coeffi.vivo.entity.VivoPass;

import java.util.List;

public interface VisitorPassService {
    VisitorPass save(VisitorPass visitorPass);

    VisitorPass getVisitor(long id);

    List<VisitorPass> getAllVisitors();

    void deleteVisitor(long id);

    VisitorPass getByMobileNumber(String mobileNumber);
}
