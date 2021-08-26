package com.dfq.coeffi.resource;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
public class ExpenseResource
{
    private long id;
    private BigDecimal amount;
    private String title;
    private String description;
    private boolean approval;
    private long categoryId;
    private long subCategoryId;

}