package com.example.interview.vo.req;

import com.example.interview.enums.Level;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;

@Data
@Schema(description = "权重创建请求")
public class WeightSettingCreateReqVO {
    @NotNull(message = "difficulty 不能为空")
    private Level difficulty;

    @NotBlank(message = "category 不能为空")
    private String category;

    @NotNull(message = "weight 不能为空")
    private Double weight;
}
