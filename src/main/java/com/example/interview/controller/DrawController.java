package com.example.interview.controller;

import com.example.interview.service.DrawService;
import com.example.interview.enums.QuestionLevel;
import com.example.interview.vo.resp.QuestionRespVO;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/draw")
@RequiredArgsConstructor
@Tag(name = "抽题接口", description = "根据级别抽取题目相关的接口")
public class DrawController {
    private final DrawService drawService;

    @PostMapping
    public ResponseEntity<List<QuestionRespVO>> draw(
            @Parameter(description = "级别", required = true)
            @RequestParam Level level,

            @Parameter(description = "抽取题目数量（必填）", required = true, example = "5")
            @RequestParam int count,

            @Parameter(description = "允许的降级等级数（可选）", required = false, example = "1")
            @RequestParam(required = false) Integer allowDowngradeLevels) {
        List<QuestionRespVO> list = drawService.draw(level, count, allowDowngradeLevels);
        return ResponseEntity.ok(list);
    }
}
