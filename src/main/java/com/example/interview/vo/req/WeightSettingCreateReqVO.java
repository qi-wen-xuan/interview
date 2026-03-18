package com.example.interview.vo.req;

import com.example.interview.enums.QuestionLevel;
import lombok.Data;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;

@Data
public class WeightSettingCreateReqVO {
    @NotNull(message = "difficulty 不能为空")
    private QuestionLevel difficulty;

    @NotBlank(message = "category 不能为空")
    private String category;


    @NotNull(message = "weight 不能为空")
    private Double weight;
}
