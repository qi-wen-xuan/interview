package com.example.interview.service;

import com.example.interview.vo.req.QuestionCreateReqVO;
import com.example.interview.vo.req.QuestionQueryReqVO;
import com.example.interview.vo.req.QuestionUpdateReqVO;
import com.example.interview.vo.resp.QuestionRespVO;

import com.baomidou.mybatisplus.core.metadata.IPage;

public interface QuestionStorageService{
    QuestionRespVO createQuestion(QuestionCreateReqVO question);

    void deleteQuestionById (Long id);

    QuestionRespVO updateQuestion(Long id, QuestionUpdateReqVO questionUpdateReqVO);

    IPage<QuestionRespVO> getQuestionPage(QuestionQueryReqVO reqVO);

}
