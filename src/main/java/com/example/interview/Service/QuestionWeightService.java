package com.example.interview.Service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.interview.entity.QuestionWeight;
import com.example.interview.enums.QuestionLevel;
import com.example.interview.vo.req.*;
import com.example.interview.vo.resp.WeightSettingRespVO;

import java.util.List;
import java.util.Map;

public interface QuestionWeightService{
    WeightSettingRespVO createWeight(WeightSettingCreateReqVO req);

    WeightSettingRespVO updateWeight(WeightSettingUpdateReqVO req);

    void deleteById(Long id);

    List<WeightSettingRespVO> listByDifficulty(String difficulty);

    WeightSettingRespVO addOrUpdateWeight(QuestionLevel difficulty, String category, Double weight);

    List<WeightSettingRespVO> getWeightsByLevel(QuestionLevel difficulty);

    boolean validateWeightSum(QuestionLevel difficulty);

    Map<String, Double> getWeightMapForLevel(QuestionLevel difficulty);
}
