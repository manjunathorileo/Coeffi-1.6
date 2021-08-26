package com.dfq.coeffi.entity.finance.income;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
@Entity
public class Income implements Serializable {


    /**
     *
     */
    private static final long serialVersionUID = -689190872017757781L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(length = 25)
    private BigDecimal amount;

    @Column(length = 25)
    private String title;

    @Column(length = 25)
    private String description;

    @Column(length = 25)
    private boolean approval;

    @Column(length = 25)
    private String approvedBy;

    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date createdOn;

    @OneToOne
    private IncomeCategory incomeCategory;
}
