package com.example.interview.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.interview.service.QuestionStorageService;
import com.example.interview.vo.req.QuestionCreateReqVO;
import com.example.interview.vo.req.QuestionQueryReqVO;
import com.example.interview.vo.req.QuestionUpdateReqVO;
import com.example.interview.vo.resp.QuestionRespVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/questions")
@RequiredArgsConstructor
@Tag(name = "题目管理", description = "题目的增删改查分页接口")
public class QuestionStorageController {

    private final QuestionStorageService questionStorageService;

    @Operation(summary = "新增题目")
    @PostMapping
    public ResponseEntity<QuestionRespVO> createQuestion(@Valid @RequestBody QuestionCreateReqVO req) {
        QuestionRespVO resp = questionStorageService.createQuestion(req);
        return ResponseEntity.ok(resp);
    }

    @Operation(summary = "分页查询题目")  //参数的中文描述
    @GetMapping
    public ResponseEntity<IPage<QuestionRespVO>> getQuestionPage(@Parameter(description = "查询条件对象")@Valid QuestionQueryReqVO reqVO) {
        IPage<QuestionRespVO> page = questionStorageService.getQuestionPage(reqVO);
        return ResponseEntity.ok(page);
    }


    @Operation(summary = "更新题目")
    @PutMapping("/{id}")
    public ResponseEntity<QuestionRespVO> updateQuestion(@Parameter(description = "题目ID")
                                                             @PathVariable Long id,
                                                         @Valid @RequestBody QuestionUpdateReqVO req) {
        QuestionRespVO resp = questionStorageService.updateQuestion(id, req);
        return ResponseEntity.ok(resp);
    }

    @Operation(summary = "删除题目")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteQuestionById(@Parameter(description = "题目ID")@PathVariable Long id) {
        questionStorageService.deleteQuestionById(id);
        return ResponseEntity.noContent().build();
    }

}
