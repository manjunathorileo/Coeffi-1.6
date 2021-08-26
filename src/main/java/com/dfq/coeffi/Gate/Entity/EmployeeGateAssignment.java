package com.dfq.coeffi.Gate.Entity;

import com.dfq.coeffi.entity.hr.employee.Employee;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@Entity
public class EmployeeGateAssignment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @OneToOne
    private Employee employee;
    @OneToOne
    private Gate inGate;
    @OneToOne
    private Gate outGate;
    private String inGateNumbersList;
    private String outGateNumbersList;
    @ManyToMany
    private List<Gate> inGates;
    @ManyToMany
    private List<Gate> outGates;



}
