package com.example.interview.vo.req;

import com.example.interview.enums.Level;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "问题chaxun请求")
public class QuestionQueryReqVO {
    private Integer page = 1;

    private Integer size = 20;

    private Level difficulty;

    private String category;

    private String question;

}
