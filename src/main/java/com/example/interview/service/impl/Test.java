package com.example.interview.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.interview.entity.Question;
import com.example.interview.mapper.QuestionStorageMapper;
import com.example.interview.vo.req.QuestionCreateReqVO;
import com.example.interview.vo.req.QuestionQueryReqVO;
import com.example.interview.vo.req.QuestionUpdateReqVO;
import com.example.interview.vo.resp.QuestionRespVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class QuestionStorageServiceImplTest {

    @Mock
    private QuestionStorageMapper questionStorageMapper;

    @InjectMocks
    private QuestionStorageServiceImpl service;

    @BeforeEach
    void setUp() {
        // nothing special
    }

    private Question createQuestionEntity(Long id, String difficulty, String category, String text) {
        Question q = new Question();
        q.setId(id);
        // assuming your Question.difficulty is enum; here using String to keep compile if field type differs adjust as needed
        // if difficulty is enum, convert accordingly: QuestionLevel.C1 etc.
        q.setDifficulty(null); // set null or appropriate enum if exists
        q.setCategory(category);
        q.setQuestion(text);
        q.setAnswer("answer-" + id);
        q.setCreatedAt(LocalDateTime.now());
        return q;
    }

    @Test
    void testCreateQuestion() {
        QuestionCreateReqVO req = new QuestionCreateReqVO();
        req.setCategory("core-java");
        req.setQuestion("What is volatile?");
        req.setAnswer("Memory visibility");

        // mock insert: MyBatis-Plus insert will set id; simulate by setting id on entity after insert
        doAnswer(invocation -> {
            Question arg = invocation.getArgument(0);
            arg.setId(123L);
            return 1;
        }).when(questionStorageMapper).insert(any(Question.class));

        QuestionRespVO resp = service.createQuestion(req);

        assertNotNull(resp);
        // createQuestion copies from request to resp (your implementation). Check fields
        assertEquals(req.getQuestion(), resp.getQuestion());
        assertEquals(req.getAnswer(), resp.getAnswer());
        // mapper.insert was called
        verify(questionStorageMapper, times(1)).insert(any(Question.class));
    }

    @Test
    void testUpdateQuestion_Success() {
        Long id = 10L;
        Question existing = createQuestionEntity(id, null, "core-java", "old Q");
        existing.setAnswer("old A");

        when(questionStorageMapper.selectById(id)).thenReturn(existing);
        doAnswer(invocation -> {
            Question arg = invocation.getArgument(0);
            // pretend update successful
            return 1;
        }).when(questionStorageMapper).updateById(any(Question.class));

        QuestionUpdateReqVO updateReq = new QuestionUpdateReqVO();
        updateReq.setQuestion("new Q");
        updateReq.setAnswer("new A");

        QuestionRespVO resp = service.updateQuestion(id, updateReq);

        assertNotNull(resp);
        assertEquals("new Q", resp.getQuestion());
        assertEquals("new A", resp.getAnswer());
        verify(questionStorageMapper, times(1)).selectById(id);
        verify(questionStorageMapper, times(1)).updateById(any(Question.class));
    }

    @Test
    void testUpdateQuestion_NotFound() {
        Long id = 99L;
        when(questionStorageMapper.selectById(id)).thenReturn(null);
        QuestionUpdateReqVO updateReq = new QuestionUpdateReqVO();
        updateReq.setQuestion("x");

        Exception ex = assertThrows(IllegalArgumentException.class, () -> service.updateQuestion(id, updateReq));
        assertTrue(ex.getMessage().contains("没发现该问题"));
        verify(questionStorageMapper, times(1)).selectById(id);
        verify(questionStorageMapper, never()).updateById(any());
    }

    @Test
    void testDeleteQuestionById() {
        Long id = 5L;
        doReturn(1).when(questionStorageMapper).deleteById(id);
        service.deleteQuestionById(id);
        verify(questionStorageMapper, times(1)).deleteById(id);
    }

    @Test
    void testGetQuestionPage() {
        QuestionQueryReqVO reqVO = new QuestionQueryReqVO();
        reqVO.setPage(1);
        reqVO.setSize(2);
        // set some filters if needed:
        // reqVO.setCategory("core-java");

        // prepare page result
        Page<Question> pageResult = new Page<>(1, 2);
        List<Question> records = new ArrayList<>();
        records.add(createQuestionEntity(1L, null, "core-java", "q1"));
        records.add(createQuestionEntity(2L, null, "database", "q2"));
        pageResult.setRecords(records);
        pageResult.setTotal(2);

        // Mock mapper.selectPage: when called with any Page and any wrapper, return our pageResult
        when(questionStorageMapper.selectPage(any(Page.class), any())).thenReturn(pageResult);

        IPage<QuestionRespVO> respPage = service.getQuestionPage(reqVO);

        assertNotNull(respPage);
        assertEquals(2, respPage.getTotal());
        assertEquals(2, respPage.getRecords().size());
        QuestionRespVO first = respPage.getRecords().get(0);
        assertEquals("q1", first.getQuestion());
        verify(questionStorageMapper, times(1)).selectPage(any(Page.class), any());
    }
}
