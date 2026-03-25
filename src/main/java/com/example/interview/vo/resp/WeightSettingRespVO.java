package com.example.interview.vo.resp;

import com.example.interview.enums.Level;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class WeightSettingRespVO {
    private Long id;
    private Level difficulty;
    private String category;
    private Double weight;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
