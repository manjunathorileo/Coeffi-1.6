package com.dfq.coeffi.LossAnalysis.oeeReport;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OeeProductionDetailsDto {

    private long defaultProductionRate;
    private long actualProductionRate;
    private long goodProduction;
    private long nonQualityProduction;
}
