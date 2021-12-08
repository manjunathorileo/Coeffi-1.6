package com.dfq.coeffi.aboutCoeffi;

import com.dfq.coeffi.policy.document.Document;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
public class About {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String productName;
    private String version;
    private Date releaseDate;
    private String description;
    private String modules;
    @ManyToMany
    private List<Document> documentIds;

}
