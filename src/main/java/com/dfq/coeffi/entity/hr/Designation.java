package com.dfq.coeffi.entity.hr;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.Date;

@ToString
@Setter
@Getter
@Entity
@Table(name="designation")
public class Designation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String name;

    private boolean status;

    @OneToOne(cascade=CascadeType.ALL)
    private Department department;

    private Date startDate;
    private Date endDate;
}
