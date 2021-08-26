package com.dfq.coeffi.visitor.Services;

import com.dfq.coeffi.visitor.Entities.VisitorPass;
import com.dfq.coeffi.visitor.Repositories.VisitorPassRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VisitorPassServiceImpl implements VisitorPassService{

    @Autowired
    VisitorPassRepository visitorPassRepository;

    @Override
    public VisitorPass save(VisitorPass visitorPass) {
        return visitorPassRepository.save(visitorPass);
    }

    @Override
    public VisitorPass getVisitor(long id) {
        return visitorPassRepository.findOne(id);
    }

    @Override
    public List<VisitorPass> getAllVisitors() {
        return visitorPassRepository.findAll();
    }

    @Override
    public void deleteVisitor(long id) {
      visitorPassRepository.delete(id);
    }

    @Override
    public VisitorPass getByMobileNumber(String mobileNumber) {
        return visitorPassRepository.findByMobileNumber(mobileNumber);
    }
}
