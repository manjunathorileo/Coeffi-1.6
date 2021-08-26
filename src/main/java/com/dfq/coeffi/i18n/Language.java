package com.dfq.coeffi.i18n;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;


@Entity
@Getter
@Setter
public class Language {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String locale;
    private String country;
    private String language;
}
