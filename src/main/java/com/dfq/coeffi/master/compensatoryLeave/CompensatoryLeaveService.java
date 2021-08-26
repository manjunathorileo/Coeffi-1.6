package com.dfq.coeffi.master.compensatoryLeave;

import java.util.List;
import java.util.Optional;

public interface CompensatoryLeaveService {
    CompensatoryLeave saveCompensatoryLeave(CompensatoryLeave compensatoryLeave);
    List<CompensatoryLeave> getAllCompensatoryLeaves();
    List<CompensatoryLeave> getCompensatoryLeavesByEmployee(long employeeId);
    Optional<CompensatoryLeave> getCompensatoryLeaveById(long id);

}
