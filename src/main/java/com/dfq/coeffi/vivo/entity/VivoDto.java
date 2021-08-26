package com.dfq.coeffi.vivo.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class VivoDto {
    private String company;

    private String ghfas;

    private long two_wheeler;

    private long three_wheeler;

    private long four_wheeler;

    private long others;

    private double grossWeight;

    private double tareWeight;

    private String description;

    private String bayNum;

    private String slotNum;

    //---Utilization----
    private String bayNumber;
    private String vehicleType;
    private long totalSlots;
    private long availableSlots;
    private long utilisedSlots;
}
