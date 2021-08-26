package com.dfq.coeffi.superadmin.Entity;

import lombok.Data;

import java.util.Date;

@Data
public class DailyReportDto {

    private boolean dailyReport;

    private long dailyReportDepartmentId;

    private String dailyReportEmail;

    private String dailyReportEmployeeName;

    private boolean dailyReportVisitor;

    private boolean dailyReportVehicle;

    private boolean dailyReportEmployee;

    private boolean dailyReportPermanentContract;

    private boolean dailyReportContract;

    private boolean lateExitReport;

    private long lateExitReportDepartmentId;

    private String lateExitReportEmail;

    private String lateExitReportEmployeeName;

    private boolean lateExitReportVisitor;

    private boolean lateExitReportVehicle;

    private boolean lateExitReportEmployee;

    private boolean lateExitReportPermanentContract;

    private boolean lateExitReportContract;

    private String lateExitTime;

    private Date trialDate;

    private long trialDays;

    private String customerName;

    private long numberOfPersons;

    private String commercialEmail;

}
