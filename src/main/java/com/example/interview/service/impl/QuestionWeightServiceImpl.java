package com.example.interview.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.interview.service.QuestionWeightService;
import com.example.interview.entity.QuestionWeight;
import com.example.interview.enums.QuestionLevel;
import com.example.interview.mapper.QuestionWeightMapper;
import com.example.interview.vo.req.WeightSettingCreateReqVO;
import com.example.interview.vo.req.WeightSettingUpdateReqVO;
import com.example.interview.vo.resp.WeightSettingRespVO;
import lombok.AllArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class QuestionWeightServiceImpl  extends ServiceImpl<QuestionWeightMapper, QuestionWeight> implements QuestionWeightService {

    private final QuestionWeightMapper mapper;
    private static final double EPS = 1e-6;

    @Override
    public WeightSettingRespVO createWeight(WeightSettingCreateReqVO req) {
        LambdaQueryWrapper<QuestionWeight> qw = new LambdaQueryWrapper<>();
        qw.eq(QuestionWeight::getDifficulty, req.getDifficulty())
                .eq(QuestionWeight::getCategory, req.getCategory());
        Long cnt = mapper.selectCount(qw);
        if (cnt != null && cnt > 0) {
            throw new IllegalStateException("该难度和类别的权重配置已存在");
        }

        QuestionWeight entity = new QuestionWeight();
        entity.setDifficulty(req.getDifficulty());
        entity.setCategory(req.getCategory());
        entity.setWeight(req.getWeight());

        try {
            mapper.insert(entity);
        } catch (DuplicateKeyException ex) {
            throw new IllegalStateException("权重已经存在");
        }
        return toResp(entity);

    }

    @Override
    public WeightSettingRespVO updateWeight(WeightSettingUpdateReqVO req) {
        QuestionWeight exist = mapper.selectById(req.getId());
        if (exist == null) {
            throw new IllegalArgumentException("权重记录不存在, id=" + req.getId());
        }
        exist.setWeight(req.getWeight());
        mapper.updateById(exist);
        return toResp(exist);
    }

    @Override
    public void deleteById(Long id) {
        QuestionWeight exist = mapper.selectById(id);
        if (exist == null) {
            throw new IllegalArgumentException("权重记录不存在,id=" + id);
        }
        mapper.deleteById(id);
    }

    @Override
    public List<WeightSettingRespVO> listByDifficulty(QuestionLevel difficulty) {
        LambdaQueryWrapper<QuestionWeight> qw  = new LambdaQueryWrapper<>();
        if (difficulty != null){
            qw.eq(QuestionWeight::getDifficulty,difficulty);
        }
        List<QuestionWeight> list = mapper.selectList(qw);
        return list.stream().map(this::toResp).collect(Collectors.toList());
    }

    @Override
    public List<WeightSettingRespVO> getWeightsByLevel(QuestionLevel difficulty) {
        LambdaQueryWrapper<QuestionWeight> qw = new LambdaQueryWrapper<>();
        if (difficulty != null) {
            qw.eq(QuestionWeight::getDifficulty, difficulty);
        }
        List<QuestionWeight> list = mapper.selectList(qw);
        return list.stream().map(this::toResp).collect(Collectors.toList());
    }

    @Override
    public boolean validateWeightSum(QuestionLevel difficulty) {
        LambdaQueryWrapper<QuestionWeight> qw = new LambdaQueryWrapper<>();
        if (difficulty != null){
            qw.eq(QuestionWeight::getDifficulty, difficulty);
        }
        double sum = mapper.selectList(qw).stream().mapToDouble(QuestionWeight::getWeight).sum();
        return Math.abs(sum - 100.0) < EPS;
    }

    @Override
    public Map<String, Double> getWeightMapForLevel(QuestionLevel difficulty) {
        LambdaQueryWrapper<QuestionWeight> qw = new LambdaQueryWrapper<>();
        if (difficulty != null) {
            qw.eq(QuestionWeight::getDifficulty, difficulty);
        }
        List<QuestionWeight> list = mapper.selectList(qw);
        Map<String, Double> map = new LinkedHashMap<>();
        for (QuestionWeight w : list) {
            map.put(w.getCategory(), w.getWeight());
        }
        return map;
    }


    public WeightSettingRespVO toResp(QuestionWeight e){
        WeightSettingRespVO vo = new WeightSettingRespVO();
        vo.setId(e.getId());
        vo.setDifficulty(e.getDifficulty());
        vo.setCategory(e.getCategory());
        vo.setWeight(e.getWeight());
        vo.setCreatedAt(e.getCreatedAt());
        vo.setUpdatedAt(e.getUpdatedAt());
        return vo;
    }


}
