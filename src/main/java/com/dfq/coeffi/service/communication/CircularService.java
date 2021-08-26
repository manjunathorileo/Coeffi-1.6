package com.dfq.coeffi.service.communication;

import com.dfq.coeffi.entity.communication.Circular;

import java.util.List;

public interface CircularService {

    //list all homeworks
    List<Circular> listAllCirculars();

    Circular saveCircular(Circular circular);

    //delete homework by id
    void deleteCircular(Long id);

    // delete all homeworks
    void deleteAllCirculars();

    //get home work by id
    Circular getCircularById(Long id);


    //List<Circular> listAllCircularsByEmployeeId(long employeeId);

    List<Circular> getUnapprovalCircular(long managerId);

    List<Circular> getAllCircularsByStatus(boolean status);
    List<Circular> getAllCircularByApprovedStatus(boolean approveStatus);

}
