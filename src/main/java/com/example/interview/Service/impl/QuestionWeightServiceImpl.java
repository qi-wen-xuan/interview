package com.example.interview.Service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.interview.Service.QuestionWeightService;
import com.example.interview.entity.QuestionWeight;
import com.example.interview.enums.QuestionLevel;
import com.example.interview.mapper.QuestionWeightMapper;
import com.example.interview.vo.req.WeightSettingCreateReqVO;
import com.example.interview.vo.req.WeightSettingUpdateReqVO;
import com.example.interview.vo.resp.WeightSettingRespVO;
import lombok.AllArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
    public List<WeightSettingRespVO> listByDifficulty(String difficulty) {
        LambdaQueryWrapper<QuestionWeight> qw  = new LambdaQueryWrapper<>();
        if (difficulty != null && !difficulty.isBlank()){
            qw.eq(QuestionWeight::getDifficulty,difficulty);
        }
        List<QuestionWeight> list = mapper.selectList(qw);
        return list.stream().map(this::toResp).collect(Collectors.toList());
    }



    @Override
    public WeightSettingRespVO addOrUpdateWeight(QuestionLevel difficulty, String category, Double weight) {
        Objects.requireNonNull(difficulty, "difficulty 不能为空");
        if (!StringUtils.hasText(category)) throw new IllegalArgumentException("category 不能为空");
        if (weight == null || weight < 0.0) throw new IllegalArgumentException("weight 必须 >= 0");

        LambdaQueryWrapper<QuestionWeight> qw = new LambdaQueryWrapper<>();
        qw.eq(QuestionWeight::getDifficulty, difficulty)
                .eq(QuestionWeight::getCategory, category);
        QuestionWeight exist = mapper.selectOne(qw);
        if (exist == null) {
            QuestionWeight e = new QuestionWeight();
            e.setDifficulty(difficulty);
            e.setCategory(category);
            e.setWeight(weight);
            mapper.insert(e);
            return toResp(e);
        } else {
            exist.setWeight(weight);
            mapper.updateById(exist);
            return toResp(exist);
        }
    }

    /**
     * 获取某职级的全部权重配置（按实体）
     */
    @Override
    public List<WeightSettingRespVO> getWeightsByLevel(QuestionLevel difficulty) {
        Objects.requireNonNull(difficulty, "difficulty 不能为空");
        LambdaQueryWrapper<QuestionWeight> qw = new LambdaQueryWrapper<>();
        qw.eq(QuestionWeight::getDifficulty, difficulty);
        List<QuestionWeight> list = mapper.selectList(qw);
        return list.stream().map(this::toResp).collect(Collectors.toList());
    }

    /**
     * 校验某职级的权重总和是否等于 100（允许微小误差）
     */
    @Override
    public boolean validateWeightSum(QuestionLevel difficulty) {
        Objects.requireNonNull(difficulty, "difficulty 不能为空");
        LambdaQueryWrapper<QuestionWeight> qw = new LambdaQueryWrapper<>();
        qw.eq(QuestionWeight::getDifficulty, difficulty);
        double sum = mapper.selectList(qw).stream().mapToDouble(QuestionWeight::getWeight).sum();
        return Math.abs(sum - 100.0) < EPS;
    }

    /**
     * 返回某职级的原始映射 category -> weight（raw）
     */
    @Override
    public Map<String, Double> getWeightMapForLevel(QuestionLevel difficulty) {
        Objects.requireNonNull(difficulty, "difficulty 不能为空");
        LambdaQueryWrapper<QuestionWeight> qw = new LambdaQueryWrapper<>();
        qw.eq(QuestionWeight::getDifficulty, difficulty);
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
