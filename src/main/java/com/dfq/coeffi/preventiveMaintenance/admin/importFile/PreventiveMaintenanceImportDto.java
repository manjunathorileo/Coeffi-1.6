package com.dfq.coeffi.preventiveMaintenance.admin.importFile;

import com.dfq.coeffi.SOPDetails.SOPCategory.SopCategory;
import com.dfq.coeffi.SOPDetails.SOPType.SopType;
import com.dfq.coeffi.entity.hr.Department;
import com.dfq.coeffi.entity.hr.employee.Employee;
import com.dfq.coeffi.preventiveMaintenance.durationType.DurationType;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class PreventiveMaintenanceImportDto {

    private int index;
    private long sopTypeId;
    private long DigitalSOPId;
    private List<SopStepsImportDto> sopStepsImportDtos;
    private String checklistFileName;
    private String checkListFileUrl;
    private long checkListSlNo;
    private DurationType durationType;
    private long durationValue;
    private SopType sopType;
    private SopCategory sopCategory;
    private Department department;
    private List<Employee> employee;

}
