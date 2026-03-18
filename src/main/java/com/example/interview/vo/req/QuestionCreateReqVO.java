package com.example.interview.vo.req;

import com.example.interview.enums.QuestionLevel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


@Data
public class QuestionCreateReqVO {
    @NotNull
    private QuestionLevel difficulty;
    @NotBlank
    private String category;
    @NotBlank
    private String question;
    @NotBlank
    private String answer;
}
