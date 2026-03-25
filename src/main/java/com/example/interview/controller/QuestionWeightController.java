package com.example.interview.controller;

import com.example.interview.service.QuestionWeightService;
import com.example.interview.enums.Level;
import com.example.interview.vo.req.WeightSettingCreateReqVO;
import com.example.interview.vo.req.WeightSettingUpdateReqVO;
import com.example.interview.vo.resp.WeightSettingRespVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/weight")
@RequiredArgsConstructor
@Tag(name = "权重配置", description = "题目权重的增删改查与校验接口")
public class QuestionWeightController {

    private final QuestionWeightService service;

    @Operation(summary = "创建权重配置")
    @PostMapping
    public ResponseEntity<WeightSettingRespVO> createWeight(@Valid @RequestBody WeightSettingCreateReqVO req){
        WeightSettingRespVO vo = service.createWeight(req);
        return ResponseEntity.ok(vo);
    }

    @Operation(summary = "更新权重配置")
    @PutMapping("/{id}")
    public ResponseEntity<WeightSettingRespVO> update(@Parameter(description = "配置ID") @Valid @RequestBody WeightSettingUpdateReqVO req) {
        WeightSettingRespVO vo = service.updateWeight(req);
        return ResponseEntity.ok(vo);
    }

    @Operation(summary = "删除权重配置")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@Parameter(description = "配置ID") @PathVariable Long id) {
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "根据级别列出权重配置")
    @GetMapping
    public ResponseEntity<List<WeightSettingRespVO>> list(@Parameter(description = "级别") @RequestParam(required = false) Level difficulty) {
        return ResponseEntity.ok(service.listByDifficulty(difficulty));
    }

    @Operation(summary = "校验某难度的权重和是否为 1")
    @GetMapping("/level/{difficulty}/validate")
    public ResponseEntity<Map<String, Object>> validateWeightSum(@Parameter(description = "难度", required = true)@PathVariable("difficulty") Level difficulty) {
        boolean ok = service.validateWeightSum(difficulty);
        return ResponseEntity.ok(Map.of("level", difficulty, "valid", ok));
    }

    @Operation(summary = "获取某级别的权重映射")
    @GetMapping("/level/{difficulty}/map")
    public ResponseEntity<Map<String, Double>> map(@Parameter(description = "难度", required = true)@PathVariable("difficulty") Level difficulty) {
        Map<String, Double> map = service.getWeightMapForLevel(difficulty);
        return ResponseEntity.ok(map);
    }



}
