package com.dfq.coeffi.master.compensatoryLeave;


import com.dfq.coeffi.entity.hr.employee.Employee;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Setter
@Getter
@Entity
public class CompensatoryLeave implements Serializable {

    private static final long serialVersionUID = 6331902193138497864L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Temporal(TemporalType.DATE)
    @CreationTimestamp
    private Date generatedOn;

    private double openingBalance;

    private double earnedLeave;

    private double closedBalance;

    private String monthName;

    @OneToOne
    private Employee employee;
}
