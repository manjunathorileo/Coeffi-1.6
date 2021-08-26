package com.dfq.coeffi.E_Learning.modules;

import com.dfq.coeffi.entity.hr.employee.Employee;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
public class UserTest {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private long totalQuestions;

    private long attemptedQuestions;

    @CreationTimestamp
    private Date testDate;

    private long level;

    private boolean status;

    @ManyToMany
    private List<AnswerSheet> answerSheets;

    @OneToOne
    private Employee employee;

    @OneToOne
    private TestMaster testMaster;

    private long score;

    private String finalResult;

    private long productId;

    private long docId;
}
