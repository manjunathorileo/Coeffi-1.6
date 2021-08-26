package com.dfq.coeffi.sam.privileges;

import com.dfq.coeffi.sam.module.Module;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;

@Setter
@Getter
@Entity(name="edupod_privileges")
public class Privileges {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String name;

    private String code;

    private String description;

    @OneToOne
    private Module module;
}