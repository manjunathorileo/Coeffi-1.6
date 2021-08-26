package com.dfq.coeffi.entity.finance.expense;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * @Auther H Kapil Kumar on 19/3/18.
 * @Company Orileo Technologies
 */

@Embeddable
@Setter
@Getter
@ToString
public class ExpenseRejection
{

    @NotNull
    private String reason;

    @NotNull
    private String rejectedBy;

    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    @JsonFormat(pattern ="yyyy-MM-dd")
    @Column(name = "created_on", updatable = false)
    private Date rejectedOn;
}