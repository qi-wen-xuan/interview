package com.example.interview.vo.req;

import lombok.Data;

@Data
public class QuestionQueryReqVO {
    private Integer page = 1;

    private Integer size = 20;

    private String difficulty;

    private String category;

    private String question;

}
