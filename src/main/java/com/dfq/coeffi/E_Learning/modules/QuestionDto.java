package com.dfq.coeffi.E_Learning.modules;


import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class QuestionDto {
    private long id;
    private String questionTitle;
    private String questionDescription;
    private String questionOption1;
    private String questionOption2;
    private String questionOption3;
    private String questionOption4;
    private String rightOption;
    private long productId;
    private Product product;
}

