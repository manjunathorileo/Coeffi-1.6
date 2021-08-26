package com.dfq.coeffi.E_Learning.modules;

import com.dfq.coeffi.entity.user.User;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@Setter
public class AnswerSheet {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @OneToOne
    private User user;
    private String questionTitle;
    private String questionDescription;
    private String questionOption1;
    private String questionOption2;
    private String questionOption3;
    private String questionOption4;
    private String rightOption;
    private String selectedOption;
    private long score;
    public boolean status;
    public boolean valid;
    @OneToOne
    private Product product;
    @CreationTimestamp
    private Date createdOn;
    @UpdateTimestamp
    private Date updatedOn;
}