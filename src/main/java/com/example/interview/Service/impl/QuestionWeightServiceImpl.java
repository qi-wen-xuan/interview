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

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class QuestionWeightServiceImpl  extends ServiceImpl<QuestionWeightMapper, QuestionWeight> implements QuestionWeightService {

    private final QuestionWeightMapper mapper;

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
    public void initDefaultWeights() {
        // X1/A5职级初始化
        saveOrUpdate(new QuestionWeight(QuestionLevel.X1, "core java", 60.0));
        saveOrUpdate(new QuestionWeight(QuestionLevel.X1, "database", 40.0));
        saveOrUpdate(new QuestionWeight(QuestionLevel.A5, "core java", 60.0));
        saveOrUpdate(new QuestionWeight(QuestionLevel.A5, "database", 40.0));

        // B1/B2职级初始化
        saveOrUpdate(new QuestionWeight(QuestionLevel.B1, "core java", 35.0));
        saveOrUpdate(new QuestionWeight(QuestionLevel.B1, "database", 30.0));
        saveOrUpdate(new QuestionWeight(QuestionLevel.B1, "spring boot", 35.0));
        saveOrUpdate(new QuestionWeight(QuestionLevel.B2, "core java", 35.0));
        saveOrUpdate(new QuestionWeight(QuestionLevel.B2, "database", 30.0));
        saveOrUpdate(new QuestionWeight(QuestionLevel.B2, "spring boot", 35.0));

        // C1/C2职级初始化
        saveOrUpdate(new QuestionWeight(QuestionLevel.C1, "java concurrent", 25.0));
        saveOrUpdate(new QuestionWeight(QuestionLevel.C1, "java jvm", 25.0));
        saveOrUpdate(new QuestionWeight(QuestionLevel.C1, "spring boot", 25.0));
        saveOrUpdate(new QuestionWeight(QuestionLevel.C1, "redis", 25.0));
        saveOrUpdate(new QuestionWeight(QuestionLevel.C2, "java concurrent", 25.0));
        saveOrUpdate(new QuestionWeight(QuestionLevel.C2, "java jvm", 25.0));
        saveOrUpdate(new QuestionWeight(QuestionLevel.C2, "spring boot", 25.0));
        saveOrUpdate(new QuestionWeight(QuestionLevel.C2, "redis", 25.0));
    }

    @Override
    public void validateWeightSum(QuestionLevel level) {
        LambdaQueryWrapper<QuestionWeight> qw = new LambdaQueryWrapper<>();
        qw.eq(QuestionWeight::getDifficulty, level);
        double total = list(qw).stream()
                .mapToDouble(QuestionWeight::getWeight).sum();
        if (total > 100.0) {
            throw new IllegalArgumentException(level + "职级总权重超过100%");
        }
    }

    @Override
    public List<Map<String, Object>> getCategoryRate(QuestionLevel level) {
        return list(new LambdaQueryWrapper<QuestionWeight>()
                .eq(QuestionWeight::getDifficulty, level))
                .stream()
                .map(item -> Map.of("category", item.getCategory(), "rate", item.getWeight() / 100))
                .collect(Collectors.toList());
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
