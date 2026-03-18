package com.example.interview.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.interview.Service.QuestionStorageService;
import com.example.interview.vo.req.QuestionCreateReqVO;
import com.example.interview.vo.req.QuestionQueryReqVO;
import com.example.interview.vo.req.QuestionUpdateReqVO;
import com.example.interview.vo.resp.QuestionRespVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/questions")
@RequiredArgsConstructor
public class QuestionStorageController {

    private final QuestionStorageService questionStorageService;

    @PostMapping
    public ResponseEntity<QuestionRespVO> createQuestion(@Valid @RequestBody QuestionCreateReqVO req) {
        QuestionRespVO resp = questionStorageService.createQuestion(req);
        return ResponseEntity.ok(resp);
    }

    @GetMapping
    public ResponseEntity<IPage<QuestionRespVO>> getQuestionPage(@Valid QuestionQueryReqVO reqVO) {
        IPage<QuestionRespVO> page = questionStorageService.getQuestionPage(reqVO);
        return ResponseEntity.ok(page);
    }


    @PutMapping("/{id}")
    public ResponseEntity<QuestionRespVO> updateQuestion(@PathVariable Long id,
                                                         @Valid @RequestBody QuestionUpdateReqVO req) {
        QuestionRespVO resp = questionStorageService.updateQuestion(id, req);
        return ResponseEntity.ok(resp);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteQuestionById(@PathVariable Long id) {
        questionStorageService.deleteQuestionById(id);
        return ResponseEntity.noContent().build();
    }

}
