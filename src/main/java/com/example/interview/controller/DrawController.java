package com.example.interview.controller;

import com.example.interview.service.DrawService;
import com.example.interview.enums.Level;
import com.example.interview.vo.resp.QuestionRespVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/draw")
@RequiredArgsConstructor
@Tag(name = "抽题接口", description = "根据级别抽取题目相关的接口")
public class DrawController {
    private final DrawService drawService;

    @Operation(summary = "抽取题目", description = "根据级别(level)和数量(count)抽取题目，可选允许降级的等级数")
    @GetMapping
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
