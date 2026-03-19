package com.example.interview.Service.impl;

import com.example.interview.dto.QuestionDto;
import com.example.interview.entity.Question;
import com.example.interview.entity.QuestionWeight;
import com.example.interview.enums.QuestionLevel;
import com.example.interview.mapper.QuestionMapper;
import com.example.interview.mapper.QuestionStorageMapper;
import com.example.interview.mapper.QuestionWeightMapper;
import com.example.interview.service.DrawService;
import com.example.interview.vo.req.QuestionCreateReqVO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class DrawServiceImpl implements DrawService {

    private final QuestionStorageMapper questionMapper;
    private final QuestionWeightMapper weightMapper;
    private final int defaultAllowDowngrade;

    // level order for downgrading
    private static final List<QuestionLevel> ORDER = List.of(QuestionLevel.X1, QuestionLevel.A5, QuestionLevel.B1,
            QuestionLevel.B2, QuestionLevel.C1, QuestionLevel.C2);
    private static final Map<QuestionLevel, Integer> ORDER_MAP = new HashMap<>();
    static { for (int i=0;i<ORDER.size();i++) ORDER_MAP.put(ORDER.get(i), i); }

    public DrawServiceImpl(QuestionStorageMapper questionMapper,
                           QuestionWeightMapper weightMapper,
                           @Value("${app.max-downgrade-levels:2}") int defaultAllowDowngrade) {
        this.questionMapper = questionMapper;
        this.weightMapper = weightMapper;
        this.defaultAllowDowngrade = defaultAllowDowngrade;
    }

    @Override
    @Transactional(readOnly = true)
    public List<QuestionCreateReqVO> draw(QuestionLevel level, int count, Integer allowDowngradeLevelsOpt) {
        if (level == null) throw new IllegalArgumentException("level 不能为空");
        if (count <= 0) return Collections.emptyList();
        int allowDown = (allowDowngradeLevelsOpt == null) ? this.defaultAllowDowngrade : allowDowngradeLevelsOpt;
        int idx = ORDER_MAP.get(level);
        int lowest = Math.max(0, idx - allowDown);
        // allowed levels as strings for SQL (e.g., "B1","B2","C1")
        List<String> allowedLevels = ORDER.subList(lowest, idx + 1).stream().map(Enum::name).collect(Collectors.toList());

        // 1. load weights for this level
        List<QuestionWeight> weights = weightMapper.selectByDifficulty(level.name()); // assume mapper method exists
        Map<String, Double> weightMap = new LinkedHashMap<>();
        if (weights == null || weights.isEmpty()) {
            // fallback: find categories from questions in allowedLevels
            List<Question> existent = questionMapper.selectByDifficulties(allowedLevels);
            Set<String> cats = existent.stream().map(Question::getCategory).collect(Collectors.toSet());
            if (cats.isEmpty()) return Collections.emptyList();
            double w = 1.0 / cats.size();
            for (String c : cats) weightMap.put(c, w);
        } else {
            double sum = weights.stream().mapToDouble(QuestionWeight::getWeight).sum();
            if (sum <= 0) {
                // fallback same as above
                List<Question> existent = questionMapper.selectByDifficulties(allowedLevels);
                Set<String> cats = existent.stream().map(Question::getCategory).collect(Collectors.toSet());
                if (cats.isEmpty()) return Collections.emptyList();
                double w = 1.0 / cats.size();
                for (String c : cats) weightMap.put(c, w);
            } else {
                for (QuestionWeight w : weights) {
                    weightMap.put(w.getCategory(), w.getWeight() / sum); // normalized to ratio
                }
            }
        }

        // 2. compute target counts per category by floor + remainder
        Map<String, Integer> targets = new LinkedHashMap<>();
        Map<String, Double> remainders = new HashMap<>();
        int assigned = 0;
        for (Map.Entry<String, Double> e : weightMap.entrySet()) {
            double exact = e.getValue() * count;
            int base = (int)Math.floor(exact);
            targets.put(e.getKey(), base);
            remainders.put(e.getKey(), exact - base);
            assigned += base;
        }
        int remain = count - assigned;
        List<String> byRem = remainders.entrySet().stream()
                .sorted((a,b) -> Double.compare(b.getValue(), a.getValue()))
                .map(Map.Entry::getKey).collect(Collectors.toList());
        for (int i=0;i<remain;i++) {
            String key = byRem.get(i % byRem.size());
            targets.put(key, targets.get(key) + 1);
        }

        // 3. for each category, try to fetch target from same level first, then lower allowed levels
        List<Question> selected = new ArrayList<>();
        Random rnd = new Random();
        Set<Long> chosenIds = new HashSet<>();

        for (Map.Entry<String, Integer> entry : targets.entrySet()) {
            String cat = entry.getKey();
            int need = entry.getValue();
            if (need <= 0) continue;

            // candidates in allowedLevels for this category
            List<Question> candidates = questionMapper.selectByCategoryAndDifficulties(cat, allowedLevels);
            // prefer those with difficulty == requested level
            List<Question> primary = candidates.stream()
                    .filter(q -> q.getDifficulty().name().equals(level.name()))
                    .filter(q -> !chosenIds.contains(q.getId()))
                    .collect(Collectors.toList());
            Collections.shuffle(primary, rnd);
            int take = Math.min(primary.size(), need);
            for (int i=0;i<take;i++){
                selected.add(primary.get(i));
                chosenIds.add(primary.get(i).getId());
            }
            need -= take;
            if (need <= 0) continue;

            // take from other candidates (other allowed levels), prefer closer levels
            List<Question> others = candidates.stream()
                    .filter(q -> !q.getDifficulty().name().equals(level.name()))
                    .filter(q -> !chosenIds.contains(q.getId()))
                    .sorted((a,b) -> Integer.compare(ORDER_MAP.get(b.getDifficulty()), ORDER_MAP.get(a.getDifficulty()))) // closer higher order first
                    .collect(Collectors.toList());
            Collections.shuffle(others, rnd); // add randomness
            int take2 = Math.min(others.size(), need);
            for (int i=0;i<take2;i++){
                selected.add(others.get(i));
                chosenIds.add(others.get(i).getId());
            }
            need -= take2;
            // if still need > 0, we will fill from general pool later
        }

        // 4. collect remaining pool from allowedLevels excluding already chosen and fill shortages
        int totalShortage = count - selected.size();
        if (totalShortage > 0) {
            List<Question> pool = questionMapper.selectByDifficulties(allowedLevels).stream()
                    .filter(q -> !chosenIds.contains(q.getId()))
                    .collect(Collectors.toList());
            Collections.shuffle(pool, rnd);
            int take = Math.min(pool.size(), totalShortage);
            for (int i=0;i<take;i++){
                selected.add(pool.get(i));
                chosenIds.add(pool.get(i).getId());
            }
        }

        // 5. map to DTO and limit to count
        return selected.stream().limit(count).map(this::toDto).collect(Collectors.toList());
    }

    private QuestionDto toDto(Question q) {
        QuestionDto d = new QuestionDto();
        d.setId(q.getId());
        d.setDifficulty(q.getDifficulty());
        d.setCategory(q.getCategory());
        d.setTitle(q.getTitle());
        d.setAnswer(q.getAnswer());
        return d;
    }
}
