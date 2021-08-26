package com.dfq.coeffi.entity.hr.employee;
/*
 * @author Ashvini B
 */

import com.fasterxml.jackson.annotation.JsonBackReference;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Setter
@Getter
@Entity
@Table(name = "previous_employement")
public class PreviousEmployement implements Serializable {

    private static final long serialVersionUID = -7041686848315413176L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String employeeName;

    private String position;

    @Column(length = 20)
    private double experienceInYear;

    @Column(length = 20)
    private double experienceInMonth;

    @Column(length = 10)
    private double salary;

    @Column(length = 20)
    private String employementType;

    @Column
    private Date startDate;

    @Column
    private Date endDate;

    @JsonBackReference
    @ApiModelProperty(hidden = true)
    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employee employee;

    public PreviousEmployement() {
        //de
    }
}