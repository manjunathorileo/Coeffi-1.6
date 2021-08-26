/**
 * Class to track all the operation done by logged user at application level
 */

package com.dfq.coeffi.auditlog.log;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@Entity
public class ApplicationLog implements Serializable {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;

    private String description;

    private String operationDoneBy;

    private long loggedUserId;

    private String operationType;

    private Date loggedOn;

    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    @Column(updatable = false)
    private Date systemTime;
}