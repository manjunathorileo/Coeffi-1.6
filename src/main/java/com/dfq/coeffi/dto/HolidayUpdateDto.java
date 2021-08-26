package com.dfq.coeffi.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
public class HolidayUpdateDto {

    private long holidayDefinationId;
    private long holidayTypeId;
    private long id;
    private Boolean isActive;
    private Boolean isFixed;
    private Date startDate;
    private Date endDate;
}
