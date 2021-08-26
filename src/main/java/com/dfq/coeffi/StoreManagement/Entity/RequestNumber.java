package com.dfq.coeffi.StoreManagement.Entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
@Entity
@Getter
@Setter
public class RequestNumber {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private char costCenter;
    private String dateStr;

    private int number;
}
