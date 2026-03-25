package com.example.interview.service;

import com.example.interview.enums.Level;
import com.example.interview.vo.req.*;
import com.example.interview.vo.resp.WeightSettingRespVO;

import java.util.List;
import java.util.Map;

public interface QuestionWeightService{
    WeightSettingRespVO createWeight(WeightSettingCreateReqVO req);

    WeightSettingRespVO updateWeight(WeightSettingUpdateReqVO req);

    void deleteById(Long id);

    List<WeightSettingRespVO> listByDifficulty(Level difficulty);

    boolean validateWeightSum(Level difficulty);

    Map<String, Double> getWeightMapForLevel(Level difficulty);
}
