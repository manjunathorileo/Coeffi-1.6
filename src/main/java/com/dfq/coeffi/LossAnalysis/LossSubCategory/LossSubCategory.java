package com.dfq.coeffi.LossAnalysis.LossSubCategory;

import com.dfq.coeffi.LossAnalysis.LossCategory.LossCategory;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;


@Getter
@Setter
@Entity
public class LossSubCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @OneToOne
    private LossCategory lossCategory;

    private String lossSubCategory;

    private String description;
    private  Boolean status;

}
