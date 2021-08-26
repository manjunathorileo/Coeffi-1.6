package com.dfq.coeffi.Expenses.Entities;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

public class EmployeeExpenseDto
{
    @JsonFormat(pattern="yyyy-MM-dd")
    private Date startDate;

    @JsonFormat(pattern="yyyy-MM-dd")
    private Date endDate;
}
