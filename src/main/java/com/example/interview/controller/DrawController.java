package com.example.interview.controller;

import com.example.interview.service.DrawService;
import com.example.interview.enums.QuestionLevel;
import com.example.interview.vo.resp.QuestionRespVO;
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
public class DrawController {
    private final DrawService drawService;

    @PostMapping
    public ResponseEntity<List<QuestionRespVO>> draw(
            @RequestParam QuestionLevel level,
            @RequestParam int count,
            @RequestParam(required = false) Integer allowDowngradeLevels) {
        List<QuestionRespVO> list = drawService.draw(level, count, allowDowngradeLevels);
        return ResponseEntity.ok(list);
    }
}
