package com.example.interview.vo.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


@Data
@Schema(description = "问题更新请求")
public class QuestionUpdateReqVO {
    @NotNull
    private Long id;
    @NotBlank
    private String question;
    @NotBlank
    private String answer;
}

