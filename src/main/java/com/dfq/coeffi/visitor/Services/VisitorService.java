package com.dfq.coeffi.visitor.Services;

import com.dfq.coeffi.visitor.Entities.Visitor;

import java.util.Date;
import java.util.List;

public interface VisitorService {

    List<Visitor> getAll();

    Visitor getVisitorByMobileNo(String mobileNumber);

    Visitor getVisitorByMobileNoforMultivisit(String mobileNumber);

    Visitor getVisitorByIdProofNumber(long idProofNumber);

    Visitor saveVisitor(Visitor visitor);

    Visitor getVisitorByDepartmentName(String departmentName);

    List<Visitor> filterByDepartment(Date startDate, Date endDate);

    List<Visitor> filterByTypeOfVisit(Date startDate, Date endDate);

    List<Visitor> filterByDeapartment(Date startDate, Date endDate);

    List<Visitor> allVisitorsDateWise(Date startDate, Date endDate);

    List<Visitor> test(Date startDate, Date endDate);

    Visitor getByMobileNumberAndDate(Date date, String mobileNumber);

    List<Visitor> getByMobileDate(Date date);

    List<Visitor> getByMobileNumberAndDateGEBE(Date date, String mobileNumber);

    Visitor getByEmpIdAndDateAndInTime(String mobileId, Date entryDate);

    /* List<Visitor> filterByDepartment(Date startDate, Date endDate,String departmentName);*/
}
