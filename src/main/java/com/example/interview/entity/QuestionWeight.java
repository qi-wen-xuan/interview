package com.example.interview.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.example.interview.enums.QuestionLevel;
import lombok.Data;


import java.time.LocalDateTime;

@Data
@TableName("weight_setting")
public class QuestionWeight {
    @TableId(type = IdType.AUTO)
    private Long id;
    private QuestionLevel difficulty;
    private String category;
    private String answer;

    private Double weight;

    @TableField("created_at")
    private LocalDateTime createdAt;

    @TableField("update_at")
    private LocalDateTime updatedAt;

    public QuestionWeight(QuestionLevel difficulty, String category, Double weight) {
        this.difficulty = difficulty;
        this.category = category;
        this.weight = weight;
    }

    public QuestionWeight() {
    }
}