package com.example.interview.controller;

import com.example.interview.Service.QuestionWeightService;
import com.example.interview.enums.QuestionLevel;
import com.example.interview.vo.req.WeightSettingCreateReqVO;
import com.example.interview.vo.req.WeightSettingUpdateReqVO;
import com.example.interview.vo.resp.WeightSettingRespVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/weight")
@RequiredArgsConstructor
public class QuestionWeightController {

    private final QuestionWeightService service;

    @PostMapping
    public ResponseEntity<WeightSettingRespVO> createWeight(@Valid @RequestBody WeightSettingCreateReqVO req){
        WeightSettingRespVO vo = service.createWeight(req);
        return ResponseEntity.ok(vo);
    }

    @PutMapping
    public ResponseEntity<WeightSettingRespVO> update(@Valid @RequestBody WeightSettingUpdateReqVO req) {
        WeightSettingRespVO vo = service.updateWeight(req);
        return ResponseEntity.ok(vo);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<WeightSettingRespVO>> list(@RequestParam(required = false) String difficulty) {
        return ResponseEntity.ok(service.listByDifficulty(difficulty));
    }

    @PostMapping("/{difficulty}/category/{category}")
    public ResponseEntity<WeightSettingRespVO> addOrUpdate(
            @PathVariable("difficulty") QuestionLevel difficulty,
            @PathVariable("category") String category,
            @RequestParam("weight") Double weight) {
        WeightSettingRespVO vo = service.addOrUpdateWeight(difficulty, category, weight);
        return ResponseEntity.ok(vo);
    }


    @GetMapping("/level/{difficulty}")
    public ResponseEntity<List<WeightSettingRespVO>> listByLevel(@PathVariable("difficulty") QuestionLevel difficulty) {
        List<WeightSettingRespVO> list = service.getWeightsByLevel(difficulty);
        return ResponseEntity.ok(list);
    }

    @GetMapping("/level/{difficulty}/validate")
    public ResponseEntity<Map<String, Object>> validate(@PathVariable("difficulty") QuestionLevel difficulty) {
        boolean ok = service.validateWeightSum(difficulty);
        return ResponseEntity.ok(Map.of("level", difficulty, "valid", ok));
    }

    @GetMapping("/level/{difficulty}/map")
    public ResponseEntity<Map<String, Double>> map(@PathVariable("difficulty") QuestionLevel difficulty) {
        Map<String, Double> map = service.getWeightMapForLevel(difficulty);
        return ResponseEntity.ok(map);
    }



}
