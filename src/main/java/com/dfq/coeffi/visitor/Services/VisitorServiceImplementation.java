package com.dfq.coeffi.visitor.Services;

import com.dfq.coeffi.visitor.Entities.Visitor;
import com.dfq.coeffi.visitor.Repositories.VisitorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Service
public class VisitorServiceImplementation implements VisitorService {

    @Autowired
    private VisitorRepository visitorRepository;


    @Override
    public Visitor saveVisitor(Visitor visitor) {
        return visitorRepository.save(visitor);
    }

    @Override
    public Visitor getVisitorByDepartmentName(String departmentName) {
        return visitorRepository.findByDepartmentName(departmentName);
    }

    @Override
    public List<Visitor> filterByDepartment(Date startDate, Date endDate) {
        return null;
    }

    @Override
    public List<Visitor> filterByTypeOfVisit(Date startDate, Date endDate) {
        return null;
    }

    @Override
    public List<Visitor> filterByDeapartment(Date startDate, Date endDate) {
        return visitorRepository.getVisitorsBwnDate(startDate, endDate);
    }

    @Override
    public List<Visitor> allVisitorsDateWise(Date startDate, Date endDate) {
        return visitorRepository.getVisitorsBwnDate(startDate,endDate);
    }

    @Override
    public List<Visitor> test(Date startDate, Date endDate) {

        System.out.println("start date" + startDate);
        System.out.println("end date" + endDate);

        List<Visitor> visitors = visitorRepository.findAll();
        List<Visitor> visitors1 = new ArrayList<>();
        for (Visitor visitor : visitors) {
            if (visitor.getLoggedOn().after(startDate) || visitor.getLoggedOn().before(endDate)) {
                visitors1.add(visitor);
            }

        }
        return visitors1;
    }

    @Override
    public Visitor getByMobileNumberAndDate(Date date, String mobileNumber) {
        return visitorRepository.findByLoggedOnAndMobileNumber(date,mobileNumber);
    }

    @Override
    public List<Visitor> getByMobileDate(Date loggedOn) {
        return visitorRepository.findByLoggedOn(loggedOn);
    }

    @Override
    public List<Visitor> getByMobileNumberAndDateGEBE(Date date, String mobileNumber) {
        return visitorRepository.findByLoggedOnAndMobileNumberGEBE(date,mobileNumber);
    }

    @Override
    public Visitor getByEmpIdAndDateAndInTime(String mobileId, Date entryDate) {
        return visitorRepository.findByMobileNumberAndInTime(mobileId,entryDate);
    }

    @Override
    public List<Visitor> getAll() {
        return visitorRepository.findAll();
    }

    @Override
    public Visitor getVisitorByMobileNo(String mobileNumber) {
        Visitor visitor = visitorRepository.findByMobileNumber(mobileNumber);
        return visitor;
    }

    @Override
    public Visitor getVisitorByMobileNoforMultivisit(String mobileNumber) {
        List<Visitor> visitor = visitorRepository.findAll();
        List visitors = new ArrayList();
        for (Visitor visitorObj : visitor) {
            if (visitorObj.getMobileNumber().equalsIgnoreCase(mobileNumber)) {
                visitors.add(visitorObj);
            }
        }
        Collections.reverse(visitors);
        return (Visitor) visitors.get(0);
    }

    @Override
    public Visitor getVisitorByIdProofNumber(long idProofNumber) {
        return null;
    }

}
