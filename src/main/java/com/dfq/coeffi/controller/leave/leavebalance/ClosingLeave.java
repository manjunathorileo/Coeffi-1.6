package com.dfq.coeffi.controller.leave.leavebalance;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.math.BigDecimal;

@Getter
@Setter
@Entity
@ToString
public class ClosingLeave {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private BigDecimal earnLeave;

    private BigDecimal clearanceLeave;

    private BigDecimal medicalLeave;

    private BigDecimal totalLeave;
}
