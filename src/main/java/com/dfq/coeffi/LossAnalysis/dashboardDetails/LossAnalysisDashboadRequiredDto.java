package com.dfq.coeffi.LossAnalysis.dashboardDetails;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class LossAnalysisDashboadRequiredDto {
 private Date fromDate;
 private Date toDate;
 private long shiftId;
 private long productionLineId;
}