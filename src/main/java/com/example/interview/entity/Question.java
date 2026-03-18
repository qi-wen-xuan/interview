package com.example.interview.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.example.interview.enums.QuestionLevel;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("question_simple")
public class Question {
    @TableId(type = IdType.AUTO)
    private Long id;

    private QuestionLevel difficulty;
    private String category;

    private String question;
    private String answer;

    @TableField("created_at")
    private LocalDateTime createdAt;
}

