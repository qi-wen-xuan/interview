package com.example.interview.Service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.interview.Service.QuestionStorageService;
import com.example.interview.entity.Question;
import com.example.interview.enums.QuestionLevel;
import com.example.interview.mapper.QuestionStorageMapper;
import com.example.interview.vo.req.QuestionCreateReqVO;
import com.example.interview.vo.req.QuestionQueryReqVO;
import com.example.interview.vo.resp.QuestionRespVO;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class QuestionStorageServiceImpl extends ServiceImpl<QuestionStorageMapper, Question> implements QuestionStorageService {

    private final QuestionStorageMapper questionStorageMapper;


    @Override
    public QuestionRespVO createQuestion(QuestionCreateReqVO question) {
        Question questionEntity = new Question();
        BeanUtils.copyProperties(question, questionEntity);
        questionStorageMapper.insert(questionEntity);
        QuestionRespVO resp = new QuestionRespVO();
        BeanUtils.copyProperties(question, resp);
        return resp;
    }

    @Override
    public void deleteQuestionById(Long id) {
        questionStorageMapper.deleteById(id);
    }

    @Override
    public QuestionRespVO updateQuestion(Long id, QuestionCreateReqVO questionCreateReqVO) {
        Question question = questionStorageMapper.selectById(id);
        if (question == null) {
            throw new IllegalArgumentException("Question not found, id=" + id);
        }
        if (questionCreateReqVO.getQuestion() != null) question.setQuestion(questionCreateReqVO.getQuestion());
        if (questionCreateReqVO.getAnswer() != null) question.setAnswer(questionCreateReqVO.getAnswer());

        questionStorageMapper.updateById(question);

        QuestionRespVO resp = new QuestionRespVO();
        BeanUtils.copyProperties(question, resp);
        return resp;
    }

    @Override
    public IPage<QuestionRespVO> getQuestionPage(QuestionQueryReqVO reqVO) {
        int page = reqVO.getPage() == null ? 1 : Math.max(1, reqVO.getPage());
        int size = reqVO.getSize() == null ? 20 : Math.max(1, reqVO.getSize());
        Page<Question> pager = new Page<>(page, size);

        LambdaQueryWrapper<Question> wrapper = new LambdaQueryWrapper<>();

        if (reqVO.getDifficulty() != null && !reqVO.getDifficulty().trim().isEmpty()) {
            wrapper.eq(Question::getDifficulty, reqVO.getDifficulty().trim());
        }

        if (reqVO.getCategory() != null && !reqVO.getCategory().trim().isEmpty()) {
            wrapper.eq(Question::getCategory, reqVO.getCategory().trim());
        }

        if (reqVO.getQuestion() != null && !reqVO.getQuestion().trim().isEmpty()) {
            wrapper.like(Question::getQuestion, reqVO.getQuestion().trim());
        }

        IPage<Question> result = questionStorageMapper.selectPage(pager, wrapper);

        Page<QuestionRespVO> pageResp = new Page<>(result.getCurrent(), result.getSize(), result.getTotal());
        List<QuestionRespVO> records = result.getRecords().stream().map(entity -> {
            QuestionRespVO vo = new QuestionRespVO();
            BeanUtils.copyProperties(entity, vo);
            return vo;
        }).collect(Collectors.toList());
        pageResp.setRecords(records);

        return pageResp;
    }

}
