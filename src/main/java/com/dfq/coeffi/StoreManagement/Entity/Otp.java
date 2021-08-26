package com.dfq.coeffi.StoreManagement.Entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Setter
@Getter
public class Otp {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String stringValue1;
    private int numberValue;
    private String stringValue2;
    private long numberValue2;
    private int f;
    private int s;
    private int l;
    private int f1;
    private int l1;
    private char charOtp1;
    private char charOtp2;
    private char charOtp3;
    private char charOtp4;
    private char charOtp5;
}
