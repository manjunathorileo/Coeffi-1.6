package com.dfq.coeffi.evacuationApi;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;

@Entity
@Getter
@Setter
public class InCsvRaw {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String name;
    private String ssoId;
    private String type;
    private Date accessDate;
    private String bId;
    @UpdateTimestamp
    private Date recordedOn;
    private String cId;
    private long logId;
    private boolean processed;

}
