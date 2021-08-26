package com.dfq.coeffi.entity.communication;

import com.dfq.coeffi.entity.hr.employee.Employee;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @Auther H Kapil Kumar on 7/3/18.
 * @Company Orileo Technologies
 */

@Setter
@Getter
@Entity
public class Event implements Serializable {
    private static final long serialVersionUID = -109791703091650816L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String eventName;
    private String message1;
    private String message2;
    private String message3;
    private String message4;


    @Column(name = "start_date")
    private Date eventDate;

    @Column(name = "end_date")
    private Date endDate;

    @Column(name = "start_time")
    private String startTime;

    @Column(name = "end_time")
    private String endTime;

    private String message;

    private String eventIncharge;

    private String venue;

    private boolean isEmail;

    private boolean isSMS;

    private boolean isPushNotification;

    @ElementCollection
    @LazyCollection(LazyCollectionOption.FALSE)
    @Embedded
    private List<EventPhotos> eventPhotos;

/*    @ApiModelProperty(hidden = true)
    @OneToOne
    private Employee employee;*/
    @ManyToMany(cascade = CascadeType.ALL)
    private List<Employee> employeeForEvent;


    @Column
    @ElementCollection
    private List<Long> employeeId;
}