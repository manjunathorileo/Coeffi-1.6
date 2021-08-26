package com.dfq.coeffi.claims;


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
public class TravelAllowance {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date createdOn;

    private String travellingTo;

    private String travellingDate;

    private long numberOfDays;

    private String tourPurpose;

    private BigDecimal tourAdvance;

    @Enumerated(EnumType.STRING)
    private TravellingApprovalStatus travellingApprovalStatus;

    private long employeeId;
}
