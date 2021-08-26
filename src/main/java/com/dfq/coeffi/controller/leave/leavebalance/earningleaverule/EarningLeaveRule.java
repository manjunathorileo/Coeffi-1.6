package com.dfq.coeffi.controller.leave.leavebalance.earningleaverule;

import com.dfq.coeffi.entity.leave.LeaveType;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
@Entity
public class EarningLeaveRule {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column
    @Enumerated(EnumType.STRING)
    private LeaveType leaveType;

    private String operand;

    private BigDecimal value;

    private BigDecimal repeated;

    @Temporal(TemporalType.DATE)
    @CreationTimestamp
    @JsonFormat(pattern ="yyyy-MM-dd")
    private Date lastUpdated;

    private Boolean isForward;
}
