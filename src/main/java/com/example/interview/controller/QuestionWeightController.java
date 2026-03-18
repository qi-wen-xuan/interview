package com.example.interview.controller;

import com.example.interview.Service.QuestionWeightService;
import com.example.interview.vo.req.WeightSettingCreateReqVO;
import com.example.interview.vo.req.WeightSettingUpdateReqVO;
import com.example.interview.vo.resp.WeightSettingRespVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping()
    public ResponseEntity<List<WeightSettingRespVO>> list(@RequestParam(required = false) String difficulty) {
        return ResponseEntity.ok(service.listByDifficulty(difficulty));
    }



}
