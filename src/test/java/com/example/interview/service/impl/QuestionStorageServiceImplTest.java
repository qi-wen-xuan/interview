package com.example.interview.service.impl;

import com.example.interview.enums.Level;

import com.example.interview.service.QuestionStorageService;
import com.example.interview.vo.req.QuestionCreateReqVO;
import com.example.interview.vo.req.QuestionQueryReqVO;
import com.example.interview.vo.req.QuestionUpdateReqVO;
import com.example.interview.vo.resp.QuestionRespVO;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.test.context.ActiveProfiles;
import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class QuestionStorageServiceImplTest {

    @Autowired
    private QuestionStorageService questionService;


    @Test
    @DisplayName("创建题目成功")
    void testCreateQuestion() {
        QuestionCreateReqVO req = new QuestionCreateReqVO();
        req.setDifficulty(Level.B1);
        req.setCategory("core-java");
        req.setQuestion("基本数据类型有几种");
        req.setAnswer("8种");

        QuestionRespVO resp = questionService.createQuestion(req);
        assertNotNull(resp);
        assertNotNull(resp.getId(), "创建后应返回 id");
        assertEquals(req.getQuestion(), resp.getQuestion());
        assertEquals(req.getAnswer(), resp.getAnswer());
        assertEquals(req.getDifficulty(), resp.getDifficulty());
        assertEquals(req.getCategory(), resp.getCategory());
    }

    @Test
    @DisplayName("删除题目后通过查询确认不存在")
    void testDeleteQuestionById() {
        QuestionCreateReqVO req = new QuestionCreateReqVO();
        String uniqueQuestion = "Q";
        req.setQuestion(uniqueQuestion);
        req.setAnswer("A");
        req.setDifficulty(Level.B1);
        req.setCategory("core-java");

        QuestionRespVO created = questionService.createQuestion(req);
        Long id = created.getId();
        assertNotNull(id);

        questionService.deleteQuestionById(id);

        QuestionQueryReqVO reqVO = new QuestionQueryReqVO();
        reqVO.setPage(1);
        reqVO.setSize(10);
        reqVO.setQuestion(uniqueQuestion);
        var page = questionService.getQuestionPage(reqVO);//类型推断工具
        boolean exists = page.getRecords().stream().anyMatch(r -> uniqueQuestion.equals(r.getQuestion()));
        assertFalse(exists, "删除后不应能查到该题");
    }

    @Test
    @DisplayName("更新题目成功且不修改 difficulty/category")
    void testUpdateQuestion() {
        QuestionCreateReqVO create = new QuestionCreateReqVO();
        create.setDifficulty(Level.B1);
        create.setCategory("core-java");
        create.setQuestion("Original Q");
        create.setAnswer("Original A");
        QuestionRespVO created = questionService.createQuestion(create);
        assertNotNull(created);
        Long id = created.getId();

        QuestionUpdateReqVO update = new QuestionUpdateReqVO();
        update.setQuestion("Updated Q");
        update.setAnswer("Updated A");

        QuestionRespVO updated = questionService.updateQuestion(id, update);
        assertNotNull(updated);
        assertEquals("Updated Q", updated.getQuestion());
        assertEquals("Updated A", updated.getAnswer());
        assertEquals(Level.B1, updated.getDifficulty());
        assertEquals("core-java", updated.getCategory());
    }

    @Test
    @DisplayName("模糊查询题目")
    void testQueryQuestionWithLike() {
        QuestionCreateReqVO a = new QuestionCreateReqVO();
        a.setDifficulty(Level.C1);
        a.setCategory("springboot");
        a.setQuestion("How does Spring Boot auto-configuration work?");
        a.setAnswer("It uses @EnableAutoConfiguration...");

        QuestionCreateReqVO b = new QuestionCreateReqVO();
        b.setDifficulty(Level.C1);
        b.setCategory("springboot");
        b.setQuestion("What is Spring Boot starter?");
        b.setAnswer("Starter...");

        questionService.createQuestion(a);
        questionService.createQuestion(b);

        QuestionQueryReqVO query = new QuestionQueryReqVO();
        query.setPage(1);
        query.setSize(10);
        query.setQuestion("auto");

        var page = questionService.getQuestionPage(query);
        assertNotNull(page);
        assertFalse(page.getRecords().isEmpty());
        boolean found = page.getRecords().stream().anyMatch(r -> r.getQuestion().toLowerCase().contains("auto"));
        assertTrue(found);
    }
}



