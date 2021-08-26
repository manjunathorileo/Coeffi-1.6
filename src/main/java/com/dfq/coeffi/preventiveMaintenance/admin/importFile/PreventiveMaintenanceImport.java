package com.dfq.coeffi.preventiveMaintenance.admin.importFile;


import com.dfq.coeffi.SOPDetails.SOPCategory.SopCategory;
import com.dfq.coeffi.SOPDetails.SOPCategory.SopCategoryService;
import com.dfq.coeffi.SOPDetails.SOPType.SopType;
import com.dfq.coeffi.SOPDetails.SOPType.SopTypeService;
import com.dfq.coeffi.entity.hr.Department;
import com.dfq.coeffi.entity.hr.employee.Employee;
import com.dfq.coeffi.preventiveMaintenance.durationType.DurationType;
import com.dfq.coeffi.service.hr.EmployeeService;
import lombok.Getter;
import lombok.Setter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@Component
public class PreventiveMaintenanceImport {

    private final SopTypeService sopTypeService;
    private final SopCategoryService SOPCategoryService;
    private final EmployeeService employeeService;

    @Autowired
    public PreventiveMaintenanceImport(SopTypeService sopTypeService, SopCategoryService SOPCategoryService, EmployeeService employeeService) {
        this.sopTypeService = sopTypeService;
        this.SOPCategoryService = SOPCategoryService;
        this.employeeService = employeeService;
    }

    public static PreventiveMaintenanceImportDto importPreventiveMaintenanceSheet(MultipartFile file, SopType sopType, SopCategory SOPCategory, DurationType durationType, long durationValue, List<Employee> employeeList) {

        PreventiveMaintenanceImportDto dto = new PreventiveMaintenanceImportDto();
        try {
            Workbook workbook = new XSSFWorkbook(file.getInputStream());
            Sheet sheet = workbook.getSheetAt(0);
            int rowNumber = sheet.getPhysicalNumberOfRows();
            if (rowNumber > 1) {
                List<SopStepsImportDto> sopStepsImportDtos = new ArrayList<>();
                for (int i = 1; i < rowNumber; i++) {
                    Row row = sheet.getRow(i);
                    SopStepsImportDto clDto = new SopStepsImportDto();
                    clDto.setIndex(i);
                    clDto.setCheckPart(row.getCell(0).getStringCellValue());
                    clDto.setCheckPoint(row.getCell(1).getStringCellValue());
                    clDto.setDescription(row.getCell(2).getStringCellValue());
                    long standardValueLong = (long) row.getCell(3).getNumericCellValue();
                    clDto.setStandardValue(standardValueLong);
                    sopStepsImportDtos.add(clDto);
                }
                dto.setSopType(sopType);
                dto.setSopCategory(SOPCategory);
                dto.setDurationType(durationType);
                dto.setDurationValue(durationValue);
                dto.setEmployee(employeeList);
                dto.setSopStepsImportDtos(sopStepsImportDtos);
            } else {
                throw new EntityNotFoundException("No data in excel sheet.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return dto;
    }
}
