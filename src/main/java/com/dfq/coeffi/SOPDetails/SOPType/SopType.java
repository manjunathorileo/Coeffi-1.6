package com.dfq.coeffi.SOPDetails.SOPType;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@Setter
public class SopType {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String sopTypeName;
    private String description;
    @Temporal(TemporalType.DATE)
    private Date createdOn;
    private Boolean status;
}