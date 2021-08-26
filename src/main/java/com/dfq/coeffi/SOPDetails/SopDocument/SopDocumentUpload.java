package com.dfq.coeffi.SOPDetails.SopDocument;

import com.dfq.coeffi.SOPDetails.SOPCategory.SopCategory;
import com.dfq.coeffi.SOPDetails.SOPType.SopType;
import com.dfq.coeffi.entity.hr.employee.Employee;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;


@Entity
@Getter
@Setter
public class SopDocumentUpload
{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @OneToOne
    private SopType sopType;
    @OneToOne
    private SopCategory sopCategory;
    @Enumerated(EnumType.STRING)
    private SopDocumentType sopDocumentType;
    private String documentFileName;
    private long documentFileSize;
    private String documentFileDescription;
    private String documentFileType;
    @Lob
    private byte[] data;
    private String fileDownloadUri;
    private Boolean status;
    @Temporal(TemporalType.DATE)
    private Date createdOn;
    @OneToOne
    private Employee createdBy;
    private long docVersion;
}
