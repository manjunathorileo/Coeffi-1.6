package com.dfq.coeffi.compOffManagement;

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
public class DatesForCompOff {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private Date generatedDates;
}
