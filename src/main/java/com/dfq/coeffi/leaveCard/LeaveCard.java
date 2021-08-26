package com.dfq.coeffi.leaveCard;


import com.dfq.coeffi.employeePermanentContract.entities.EmpPermanentContract;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@Setter
public class LeaveCard {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String month;
    private String year;
    @Lob
    private byte[] data;
    private long empId;
    private String fileType;
    @CreationTimestamp
    private Date createdOn;
    @OneToOne
    private EmpPermanentContract empPermanentContract;
}
