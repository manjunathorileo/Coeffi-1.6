package com.dfq.coeffi.advanceFoodManagementExtra;

import java.util.Date;
import java.util.List;

public interface EmployeeCanteenDetailsService  {
    void saveEmployeeCanteenDetails(EmployeeCanteenDetails employeeCanteenDetails);
    EmployeeCanteenDetails getEmployeeCanteenDetailsById(long id);
    EmployeeCanteenDetails getEmployeeCanteenDetailsByEmployeeIdAndFoodTypeAndDate(long employeeId, String foodType, Date date);

    List<EmployeeCanteenDetails> getEmployeeCanteenDetailsByCounterId(long counterId);
    List<EmployeeCanteenDetails> getEmployeeCanteenDetailsByCounterByFoodType(long counterId, long foodTypeId);
    EmployeeCanteenDetails  findByMarkedOnAndEmployeeCodeAndFoodTypeName(Date markedOn,String employeeCode,String foodTypeName);

    List<EmployeeCanteenDetails> findByMarkedOnAndEmployeeCode(Date mk, String employeeCode);
}
