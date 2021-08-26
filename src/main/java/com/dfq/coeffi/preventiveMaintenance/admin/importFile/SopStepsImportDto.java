package com.dfq.coeffi.preventiveMaintenance.admin.importFile;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class SopStepsImportDto {

    private int index;
    private String checkPart;
    private String checkPoint;
    private String description;
    private long standardValue;
}
