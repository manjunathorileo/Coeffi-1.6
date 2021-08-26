package com.dfq.coeffi.StoreManagement.Entity;

import com.dfq.coeffi.master.shift.Shift;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
public class ProductionItemQualityCheckDto {

    private Date createdOn;
    private String productionEmployee;
    private long productionLineMasters;
    private long batchMaster;
    private long factorys;
    private long shift;
    private long employeeId;
    private List<ProductionMaster> productionMasters;

}
