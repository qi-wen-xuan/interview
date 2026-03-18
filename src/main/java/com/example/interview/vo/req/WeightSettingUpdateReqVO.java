package com.example.interview.vo.req;

import com.example.interview.enums.QuestionLevel;
import lombok.Data;

import jakarta.validation.constraints.NotNull;


@Data
public class WeightSettingUpdateReqVO {
    @NotNull(message = "id 不能为空")
    private Long id;

    @NotNull(message = "weight 不能为空")
    private Double weight;
}
