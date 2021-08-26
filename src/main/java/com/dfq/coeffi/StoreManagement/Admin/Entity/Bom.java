package com.dfq.coeffi.StoreManagement.Admin.Entity;

import com.dfq.coeffi.StoreManagement.Entity.Materials;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
public class Bom {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String bomNumber;
    private String bomName;
    private Date creationDate;

    @OneToMany(cascade = {CascadeType.ALL})
    private List<BomItems> bomItemsList;

}
