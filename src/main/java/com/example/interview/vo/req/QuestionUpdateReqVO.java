package com.example.interview.vo.req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


@Data
public class QuestionUpdateReqVO {
    @NotNull
    private Long id;
    @NotBlank
    private String question;
    @NotBlank
    private String answer;
}

