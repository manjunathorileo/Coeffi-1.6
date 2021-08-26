package com.dfq.coeffi.SOPDetails.dto;

import com.dfq.coeffi.SOPDetails.Event.eventMaster.AttachType;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;


@Getter
@Setter
public class Completiondto {
    private double eventNumber;
    private String eventDescription;
    private Date eventDate;
    private AttachType triggeredBy;
    private String sopType;
    private String sopCategory;
    private String executedBy;
    private Date executedOn;
    private String status;
}
