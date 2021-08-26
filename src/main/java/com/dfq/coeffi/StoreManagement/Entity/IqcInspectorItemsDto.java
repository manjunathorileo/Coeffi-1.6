package com.dfq.coeffi.StoreManagement.Entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.util.Date;
import java.util.List;

@Getter
@Setter
public class IqcInspectorItemsDto {
    private String po;
    private String grn;
    private String supplierName;
    private Date receivedDate;
    private Date inspectionDate;
    private long employeeId;
    private String itemType;
    @Enumerated(EnumType.STRING)
    private MaterialsEnum itemStatus;
    private List<IqcInspectorItems> iqcInspectorItemsList;
}
