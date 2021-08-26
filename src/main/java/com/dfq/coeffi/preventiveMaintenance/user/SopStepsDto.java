package com.dfq.coeffi.preventiveMaintenance.user;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SopStepsDto {
    private String checkListFileName;
    private String checkListUrl;
    private long slNo;
    private long fileSize;
}
