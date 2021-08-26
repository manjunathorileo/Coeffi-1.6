package com.dfq.coeffi.StoreManagement.Entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import java.util.Date;


@Getter
@Setter
public class ApproveItemsDto {
    private Date inspectionDate;
    private String inspectorName;
}
