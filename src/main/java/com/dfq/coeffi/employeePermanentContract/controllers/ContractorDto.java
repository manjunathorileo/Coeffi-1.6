package com.dfq.coeffi.employeePermanentContract.controllers;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;
import java.util.List;

@Setter
@Getter
public class ContractorDto {
    private String contractorName;
//    private Date fromDate;
//    private Date toDate;
    private Date dueDate;
    private long totalEmployees;
    List<DepartmanentWiseDto> departmanentWiseDtoList;
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern="yyyy-MM-dd")
    public Date startDate;

    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern="yyyy-MM-dd")
    public Date endDate;
}
