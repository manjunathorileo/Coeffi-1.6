package com.dfq.coeffi.entity.communication;

import com.dfq.coeffi.entity.hr.employee.Employee;
import com.dfq.coeffi.policy.document.Document;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
public class Circular implements Serializable {

    private static final long serialVersionUID = -1176300447154774961L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    @ElementCollection
    private List<Long> studentId;

    /*@Column
    @ElementCollection
    private List<Long> employeeId;*/

    @ManyToMany(cascade = CascadeType.ALL)
    private List<Employee> employeeForCirculars;

    @Column
    private String title;

    private String status;

    @Column(length = 500)
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(length = 45)
    private CircularLevel circularLevel;

    private boolean isEmail;

    private boolean isSMS;

    private boolean isPushNotification;

    private boolean approveStatus;

    private long firstManager;

    private long secondManager;

    @OneToOne
    private Document document;

    @Column
    private Date date;

    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    @Column(name = "created_on", updatable = false)
    private Date createdOn;


}
