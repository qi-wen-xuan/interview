package com.example.interview.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.interview.entity.Question;
import com.example.interview.enums.Level;
import com.example.interview.service.DrawService;
import com.example.interview.service.QuestionWeightService;
import com.example.interview.mapper.QuestionStorageMapper;
import com.example.interview.vo.resp.QuestionRespVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DrawServiceImpl implements DrawService {

    private final QuestionStorageMapper questionMapper;
    private final QuestionWeightService weightService;
    @Value("${app.max-downgrade-levels:2}")
    private int defaultAllowGrade;
    /**
     * 抽取面试题
     *
     * @param level 职级
     * @param count 抽取数量
     * @param allowLevels 允许降级的级别数，可为null使用默认值
     * @return 抽取的题目列表
     */
    @Override
    @Transactional(readOnly = true)
    public List<QuestionRespVO> draw(Level level, int count, Integer allowLevels) {
        if (level == null || count <= 0) return Collections.emptyList();

        int allowDown = (allowLevels == null) ? defaultAllowGrade : allowLevels;
        Level[] all = Level.values();
        int idx = level.ordinal();  //从0开始 顺序
        int lowest = Math.max(0, idx - allowDown);
        List<String> allowed = new ArrayList<>();
        for (int i = lowest; i <= idx; i++) {
            allowed.add(all[i].name());   //允许的难度集合
        }

        Map<String, Double> raw = weightService.getWeightMapForLevel(level); // category->raw weight
        Map<String, Double> ratio = new LinkedHashMap<>();
        if (raw == null || raw.isEmpty()) {
            List<Question> exist = selectByDifficulties(allowed);
            Set<String> cats = exist.stream().map(Question::getCategory).filter(Objects::nonNull).collect(Collectors.toSet());  //去重
            if (cats.isEmpty()) return Collections.emptyList();
            double r = 1.0 / cats.size();
            for (String c : cats){
                ratio.put(c, r);
            }
        } else {
            double sum = raw.values().stream().mapToDouble(Double::doubleValue).sum();
            if (sum <= 0) {
                List<Question> exist = selectByDifficulties(allowed);     //Stream<Double> 映射为 DoubleStream  d -> d.doubleValue()
                Set<String> cats = exist.stream().map(Question::getCategory).filter(Objects::nonNull).collect(Collectors.toSet());
                if (cats.isEmpty()) return Collections.emptyList();
                double r = 1.0 / cats.size();
                for (String c : cats) {
                    ratio.put(c, r);
                }
            } else {  //Set<Map.Entry<String, Double>>
                for (var e : raw.entrySet()){   //在一次遍历过程中直接读取 key 和 value
                    ratio.put(e.getKey(), e.getValue() / sum); //category   weight  计算出比例
                }
            }
        }


        Map<String, Integer> target = new LinkedHashMap<>();  //每类的目标数量
        int assigned = 0;
        List<String> cats = new ArrayList<>(ratio.keySet()); //返回 Map ratio 中所有键（key）的集合 Set<String>，包含 map 中所有的键（类别名称）
        for (String c : cats) {
            int t = (int) Math.round(ratio.get(c) * count);  //该类别比例应分配的理论题数
            target.put(c, t);
            assigned += t;
        }
        // 简单修正总数，保证 Σtarget == count
        int diff = assigned - count;
        for (int i = 0; diff != 0 && i < cats.size(); i++) {
            String c = cats.get(i);
            int cur = target.get(c);
            if (diff > 0 && cur > 0) {
                target.put(c, cur - 1);
                diff--;
            }
            else if (diff < 0) {
                target.put(c, cur + 1);
                diff++;
            }
        }

        // 按每个分类抽题
        List<Question> selected = new ArrayList<>();  //选中的题目
        Set<Long> used = new HashSet<>();  //选中题目的ID
        Random random = new Random();

        for (var ent : target.entrySet()) {
            String category = ent.getKey();  //类别名和要抽的数
            int need = ent.getValue();
            if (need <= 0) continue;

            // 该分类在 allowed 难度内的所有题
            List<Question> candidates = selectByCategoryAndDifficulties(category, allowed);
            if (candidates.isEmpty()) continue;

            Collections.shuffle(candidates, random);  //随机打乱
            for (Question q : candidates) {
                if (need <= 0) break;
                if (used.contains(q.getId())) continue;
                selected.add(q);
                used.add(q.getId());
                need--;
            }
        }

        // 补位
        int shortage = count - selected.size();
        if (shortage > 0) {
            List<Question> pool = selectByDifficulties(allowed).stream()
                    .filter(q -> !used.contains(q.getId()))
                    .collect(Collectors.toList());
            Collections.shuffle(pool, random);
            int take = Math.min(pool.size(), shortage);  //剩下可用题的数量，还需
            for (int i = 0; i < take; i++) {
                selected.add(pool.get(i));
                used.add(pool.get(i).getId());
            }
        }

        return selected.stream().limit(count).map(this::toResp).collect(Collectors.toList());
    }

    private QuestionRespVO toResp(Question q) {
        QuestionRespVO vo = new QuestionRespVO();
        vo.setId(q.getId());
        vo.setDifficulty(q.getDifficulty());
        vo.setCategory(q.getCategory());
        vo.setQuestion(q.getQuestion());
        vo.setAnswer(q.getAnswer());
        return vo;
    }

    private List<Question> selectByDifficulties(List<String> difficulties) {
        if (difficulties == null || difficulties.isEmpty()) return Collections.emptyList();
        LambdaQueryWrapper<Question> qw = new LambdaQueryWrapper<>();
        qw.in(Question::getDifficulty, difficulties);
        return questionMapper.selectList(qw);
    }

    private List<Question> selectByCategoryAndDifficulties(String category, List<String> difficulties) {
        LambdaQueryWrapper<Question> qw = new LambdaQueryWrapper<>();
        qw.eq(Question::getCategory, category);
        if (difficulties != null && !difficulties.isEmpty()) {
            qw.in(Question::getDifficulty, difficulties);
        }
        return questionMapper.selectList(qw);
    }
}
