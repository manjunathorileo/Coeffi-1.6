package com.dfq.coeffi.entity.master;

/**
 * @Auther H Kapil Kumar on 6/3/18.
 * @Company Orileo Technologies
 */

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;


@Setter
@Getter
@MappedSuperclass
@JsonIdentityInfo(generator=ObjectIdGenerators.PropertyGenerator.class,property = "id")

public class GeneralMetadata implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @Column(name = "created_by", updatable = false)
    private String createdBy;

    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    @Column(name = "created_on", updatable = false)
    private Date createdOn;

    @Temporal(TemporalType.TIMESTAMP)
    @UpdateTimestamp
    @Column(name = "modified_on")
    private Date modifiedOn;

    @JsonIgnore
    @Column(name = "modified_by")
    private String modifiedBy;
}