package com.dfq.coeffi.FeedBackManagement.Entity;

import com.dfq.coeffi.FeedBackManagement.Entity.FeedBackGrades;
import com.dfq.coeffi.FeedBackManagement.Entity.FeedBackParameter;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class FeedBackDto {
    private List<FeedBackGrades> feedBackGradesList;
    private List<FeedBackParameter> feedBackParameterList;
}
