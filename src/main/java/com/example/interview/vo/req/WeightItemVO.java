package com.example.interview.vo.req;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class WeightItemVO {
    @NotBlank
    private String category;
    @NotNull
    @DecimalMin(value = "0.0", inclusive = true)
    private Double weight;
}
