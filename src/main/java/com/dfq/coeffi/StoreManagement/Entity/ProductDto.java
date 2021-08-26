package com.dfq.coeffi.StoreManagement.Entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.util.Date;

@Getter
@Setter
public class ProductDto {
    private String productionName;
    private String clientName;
    private Date createdOn;
    @Enumerated(EnumType.STRING)
    private MaterialStatus productionLqcItemStatus;
    @Enumerated(EnumType.STRING)
    private MaterialStatus productionFqcItemStatus;
}
