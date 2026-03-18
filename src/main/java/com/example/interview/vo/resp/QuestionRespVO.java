package com.example.interview.vo.resp;

import com.example.interview.enums.QuestionLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuestionRespVO {
    private Long id;
    private QuestionLevel difficulty;
    private String category;
    private String question;
    private String answer;
}
