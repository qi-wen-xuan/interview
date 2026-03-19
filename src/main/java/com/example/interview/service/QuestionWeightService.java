package com.example.interview.service;

import com.example.interview.enums.QuestionLevel;
import com.example.interview.vo.req.*;
import com.example.interview.vo.resp.WeightSettingRespVO;

import java.util.List;
import java.util.Map;

public interface QuestionWeightService{
    WeightSettingRespVO createWeight(WeightSettingCreateReqVO req);

    WeightSettingRespVO updateWeight(WeightSettingUpdateReqVO req);

    void deleteById(Long id);

    List<WeightSettingRespVO> listByDifficulty(QuestionLevel difficulty);

    List<WeightSettingRespVO> getWeightsByLevel(QuestionLevel difficulty);

    boolean validateWeightSum(QuestionLevel difficulty);

    Map<String, Double> getWeightMapForLevel(QuestionLevel difficulty);
}
