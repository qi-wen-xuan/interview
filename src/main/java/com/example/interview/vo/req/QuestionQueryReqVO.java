package com.example.interview.vo.req;

import com.example.interview.enums.QuestionLevel;
import lombok.Data;

@Data
public class QuestionQueryReqVO {
    private Integer page = 1;

    private Integer size = 20;

    private QuestionLevel difficulty;

    private String category;

    private String question;

}
