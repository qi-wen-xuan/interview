package com.example.interview.vo.req;

import com.example.interview.enums.Level;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


@Data
@Schema(description = "问题创建请求")
public class QuestionCreateReqVO {
    @NotNull
    private Level difficulty;
    @NotBlank
    private String category;
    @NotBlank
    private String question;
    @NotBlank
    private String answer;
}
