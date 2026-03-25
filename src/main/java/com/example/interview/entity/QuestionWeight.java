package com.example.interview.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.example.interview.enums.Level;
import lombok.Data;


import java.time.LocalDateTime;

@Data
@TableName("question_weight")
public class QuestionWeight {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Level difficulty;
    private String category;
    private Double weight;

    @TableField("created_at")
    private LocalDateTime createdAt;

    @TableField("updated_at")
    private LocalDateTime updatedAt;

    public QuestionWeight(Level difficulty, String category, Double weight) {
        this.difficulty = difficulty;
        this.category = category;
        this.weight = weight;
    }

    public QuestionWeight() {
    }
}