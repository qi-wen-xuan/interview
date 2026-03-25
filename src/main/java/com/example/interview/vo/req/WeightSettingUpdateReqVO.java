package com.example.interview.vo.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotNull;


@Data
@Schema(description = "权重更新请求")
public class WeightSettingUpdateReqVO {
    @NotNull(message = "id 不能为空")
    private Long id;

    @NotNull(message = "weight 不能为空")
    private Double weight;
}
