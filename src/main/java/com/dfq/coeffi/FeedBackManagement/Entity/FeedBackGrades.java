package com.dfq.coeffi.FeedBackManagement.Entity;

import com.dfq.coeffi.CanteenManagement.Entity.FoodImage;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
public class FeedBackGrades {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String gradingName;
    private long fileId;
    @OneToOne
    GradeImage gradeImages;
}
