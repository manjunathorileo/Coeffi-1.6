package com.dfq.coeffi.entity.user;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Setter
@Getter
@Entity
public class LoggedUser {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private long id;

    @Column
    private String name;

    @Column
    private String refName;

    @Column
    private String token;
}
