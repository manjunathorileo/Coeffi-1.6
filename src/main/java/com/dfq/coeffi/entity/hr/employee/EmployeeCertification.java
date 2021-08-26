package com.dfq.coeffi.entity.hr.employee;

import com.fasterxml.jackson.annotation.JsonBackReference;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@Entity
@Table(name="employeeCertification")
public class EmployeeCertification implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String certificationName;

    private String instituteName;

    @Column
    private Date startDate;

    @Column
    private Date endDate;

    @JsonBackReference
    @ApiModelProperty(hidden=true)
    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employee employee;
}
