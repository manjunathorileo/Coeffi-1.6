package com.dfq.coeffi.SOPDetails.adherence;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;

@Entity
@Getter
@Setter
public class Adherence
{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private Date date;
    private String remarks;

    private long digitalSopId;
    private String sopCategoryName;
    private long userId;
    private String sopName;

}
