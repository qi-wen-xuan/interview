package com.example.interview.vo.resp;

import com.example.interview.enums.QuestionLevel;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class WeightSettingRespVO {
    private Long id;
    private QuestionLevel difficulty;
    private String category;
    private String answer;
    private Double weight;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
