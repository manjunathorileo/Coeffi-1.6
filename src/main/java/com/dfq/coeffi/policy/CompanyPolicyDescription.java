package com.dfq.coeffi.policy;

import com.dfq.coeffi.policy.document.Document;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Getter
@Setter
public class CompanyPolicyDescription implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(length = 5000)
    private String subPolicyName;

    @Column(length = 10000)
    private String description;

    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    @Column(name = "created_on", updatable = false)
    private Date createdOn;

    private boolean active;

    @OneToOne
    private CompanyPolicy companyPolicy;

    @OneToOne
    private Document document;

}
