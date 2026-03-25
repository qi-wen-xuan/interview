package com.example.interview.service.impl;

import com.example.interview.enums.Level;
import com.example.interview.service.DrawService;
import com.example.interview.service.QuestionStorageService;
import com.example.interview.service.QuestionWeightService;
import com.example.interview.vo.req.QuestionCreateReqVO;
import com.example.interview.vo.req.WeightSettingCreateReqVO;
import com.example.interview.vo.resp.QuestionRespVO;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
@Transactional

public class DrawServiceImplTest {
    @Autowired
    private DrawService drawService;

    @Autowired
    private QuestionStorageService questionStorageService;

    @Autowired
    private QuestionWeightService weightService;

    @Test
    @DisplayName("按权重抽题（权重存在、题库充足）")
    void testDraw_byWeights() {
        Level level = Level.C1;
        QuestionCreateReqVO q1 = new QuestionCreateReqVO();
        q1.setDifficulty(Level.C1);
        q1.setCategory("core-java");
        q1.setQuestion("Q-core-1");
        q1.setAnswer("A1");
        questionStorageService.createQuestion(q1);

        QuestionCreateReqVO q2 = new QuestionCreateReqVO();
        q2.setDifficulty(Level.B1);
        q2.setCategory("database");
        q2.setQuestion("Q-db-1");
        q2.setAnswer("A2");
        questionStorageService.createQuestion(q2);

        WeightSettingCreateReqVO w1 = new WeightSettingCreateReqVO();
        w1.setDifficulty(level);
        w1.setCategory("core-java");
        w1.setWeight(60.0);
        weightService.createWeight(w1);

        WeightSettingCreateReqVO w2 = new WeightSettingCreateReqVO();
        w2.setDifficulty(level);
        w2.setCategory("database");
        w2.setWeight(40.0);
        weightService.createWeight(w2);

        List<QuestionRespVO> res = drawService.draw(level, 2, null);
        assertNotNull(res);
        assertTrue(res.size() <= 2);
        // 确保难度在允许范围内（B1..C1）
        for (QuestionRespVO vo : res) {
            assertTrue(vo.getDifficulty().ordinal() <= level.ordinal());
            assertTrue(vo.getCategory().equals("core-java") || vo.getCategory().equals("database"));
        }
    }

    @Test
    @DisplayName("权重不存在时回退到题库均分")
    void testDraw_Equal() {
        Level level = Level.B1;
        QuestionCreateReqVO a = new QuestionCreateReqVO();
        a.setDifficulty(Level.B1);
        a.setCategory("core-java");
        a.setQuestion("Q-core");
        a.setAnswer("A");
        questionStorageService.createQuestion(a);

        QuestionCreateReqVO b = new QuestionCreateReqVO();
        b.setDifficulty(Level.B1);
        b.setCategory("database");
        b.setQuestion("Q-db");
        b.setAnswer("B");
        questionStorageService.createQuestion(b);

        var map = weightService.getWeightMapForLevel(level);
        var res = drawService.draw(level, 2, null);
        assertNotNull(res);
        assertTrue(res.size() <= 2);
    }

    @Test
    @DisplayName("权重最多降级 N 级，不抽更高级别题")
    void testDraw_respectAllowDown() {
        Level level = Level.B1;
        QuestionCreateReqVO high = new QuestionCreateReqVO();
        high.setDifficulty(Level.C2);
        high.setCategory("core-java");
        high.setQuestion("111");
        high.setAnswer("A");
        questionStorageService.createQuestion(high);

        QuestionCreateReqVO ok = new QuestionCreateReqVO();
        ok.setDifficulty(Level.B1);
        ok.setCategory("core-java");
        ok.setQuestion("222");
        ok.setAnswer("B");
        questionStorageService.createQuestion(ok);

        WeightSettingCreateReqVO w = new WeightSettingCreateReqVO();
        w.setDifficulty(level);
        w.setCategory("core-java");
        w.setWeight(1.0);
        weightService.createWeight(w);

        //不允许降级
        List<QuestionRespVO> res = drawService.draw(level, 2, 0);
        for (var vo : res) {
            assertTrue(vo.getDifficulty().ordinal() <= level.ordinal());
        }
    }

    @Test
    @DisplayName("分类不足时从其他分类且相同适用级别补位")
    void testDraw_fillIn() {
        Level level = Level.C1;
        QuestionCreateReqVO a = new QuestionCreateReqVO();
        a.setDifficulty(Level.C1);
        a.setCategory("core-java");
        a.setQuestion("111");
        a.setAnswer("A");
        questionStorageService.createQuestion(a);

        WeightSettingCreateReqVO w1 = new WeightSettingCreateReqVO();
        w1.setDifficulty(level);
        w1.setCategory("core-java");
        w1.setWeight(70.0);
        weightService.createWeight(w1);

        WeightSettingCreateReqVO w2 = new WeightSettingCreateReqVO();
        w2.setDifficulty(level);
        w2.setCategory("database");
        w2.setWeight(30.0);
        weightService.createWeight(w2);

        var res = drawService.draw(level, 2, null);
        assertTrue(res.size() <= 2);
        for (var vo : res){
            assertEquals("core-java", vo.getCategory());
        }

    }
}