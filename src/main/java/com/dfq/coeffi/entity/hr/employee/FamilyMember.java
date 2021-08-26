package com.dfq.coeffi.entity.hr.employee;

import com.fasterxml.jackson.annotation.JsonBackReference;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Setter
@Getter
@Entity
@Table(name="family_member")
public class FamilyMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length=25)
    private String name;

    @Column
    @Temporal(TemporalType.DATE)
    private Date dateOfBirth;

    @Column(length=25)
    private String relation;

    @JsonBackReference
    @ApiModelProperty(hidden=true)
    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employee employee;
}
