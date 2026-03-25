package com.example.interview.service.impl;

import com.example.interview.enums.Level;
import com.example.interview.service.QuestionWeightService;
import com.example.interview.vo.req.WeightSettingCreateReqVO;
import com.example.interview.vo.req.WeightSettingUpdateReqVO;
import com.example.interview.vo.resp.WeightSettingRespVO;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;


import java.util.List;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional

public class QuestionWeightServiceImplTest {

    @Autowired
    private QuestionWeightService service;

    @Test
    @DisplayName("创建权重成功")
    void testCreateWeight(){
        WeightSettingCreateReqVO req = new WeightSettingCreateReqVO();
        String category = "java-core" + System.currentTimeMillis();
        req.setCategory(category);
        req.setWeight(30.0);
        req.setDifficulty(Level.A5);

        WeightSettingRespVO resp = service.createWeight(req);
        assertNotNull(resp,"不能为空");
        assertNotNull(resp.getId(),"创建后应返回id");
        assertEquals(req.getCategory(),resp.getCategory());
        assertEquals(req.getWeight(), resp.getWeight());
        assertEquals(req.getDifficulty(), resp.getDifficulty());
    }

    @Test
    @DisplayName("更新权重和不存在时抛异常")
    void testUpdateWeight(){
        WeightSettingCreateReqVO req = new WeightSettingCreateReqVO();
        String category = "java-core" + System.currentTimeMillis();
        req.setCategory(category);
        req.setWeight(20.0);
        req.setDifficulty(Level.C2);

        WeightSettingRespVO created = service.createWeight(req);
        Long id = created.getId();

        WeightSettingUpdateReqVO upd = new WeightSettingUpdateReqVO();
        upd.setId(id);
        upd.setWeight(35.0);
        var updated = service.updateWeight(upd);
        assertNotNull(updated);
        assertEquals(35.0,updated.getWeight());

    }

    @Test
    @DisplayName("删除权重并确认不存在")
    void testDeleteById(){
        String category = "database" + System.currentTimeMillis();

        WeightSettingCreateReqVO req = new WeightSettingCreateReqVO();
        req.setCategory(category);
        req.setWeight(10.0);
        req.setDifficulty(Level.C1);

        var created =  service.createWeight(req);
        Long id = created.getId();
        service.deleteById(id);

        List<WeightSettingRespVO> list = service.listByDifficulty(Level.C1);
        boolean exists = list.stream().anyMatch(r->r.getId().equals(id)||category.equals(r.getCategory()));
        assertFalse(exists);
    }

    @Test
    @DisplayName("按职级列出权重")
    void testListByDifficulty() {
        Level level = Level.A5;
        String cat1 = "cat1" + System.currentTimeMillis();
        String cat2 = "cat2" + System.currentTimeMillis();

        WeightSettingCreateReqVO r1 = new WeightSettingCreateReqVO();
        r1.setDifficulty(level);
        r1.setCategory(cat1);
        r1.setWeight(60.0);
        service.createWeight(r1);

        WeightSettingCreateReqVO r2 = new WeightSettingCreateReqVO();
        r2.setDifficulty(level);
        r2.setCategory(cat2);
        r2.setWeight(40.0);
        service.createWeight(r2);

        List<WeightSettingRespVO> list = service.listByDifficulty(level);

        boolean hasCat1 = list.stream().anyMatch(v -> cat1.equals(v.getCategory()));
        boolean hasCat2 = list.stream().anyMatch(v -> cat2.equals(v.getCategory()));
        assertTrue(hasCat1 && hasCat2);
    }

    @Test
    @DisplayName("检验权重总和")
    void testValidateWeightSum() {
        Level level = Level.B2;
        WeightSettingCreateReqVO r1 = new WeightSettingCreateReqVO();
        r1.setDifficulty(level);
        r1.setCategory("v1-" + System.currentTimeMillis());
        r1.setWeight(40.0);
        WeightSettingCreateReqVO r2 = new WeightSettingCreateReqVO();
        r2.setDifficulty(level);
        r2.setCategory("v2-" + System.currentTimeMillis());
        r2.setWeight(60.0);

        service.createWeight(r1);
        service.createWeight(r2);

        boolean ok = service.validateWeightSum(level);
        assertTrue(ok);

        var list = service.listByDifficulty(level);
        var first = list.stream().filter(v -> v.getCategory().startsWith("v1-")).findFirst().orElse(null);
        assertNotNull(first);
        WeightSettingUpdateReqVO upd = new WeightSettingUpdateReqVO();
        upd.setId(first.getId());
        upd.setWeight(10.0);
        service.updateWeight(upd);

        boolean ok2 = service.validateWeightSum(level);
        assertFalse(ok2);
    }


}
