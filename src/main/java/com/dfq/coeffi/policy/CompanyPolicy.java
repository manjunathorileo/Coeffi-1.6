package com.dfq.coeffi.policy;

import com.dfq.coeffi.policy.document.Document;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@ToString
public class CompanyPolicy  {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column
    private String policyName;

    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    private Date createdOn;

    private boolean active;

    @OneToOne(cascade = CascadeType.ALL)
    private Document document;

}
