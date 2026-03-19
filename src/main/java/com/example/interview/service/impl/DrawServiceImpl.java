package com.example.interview.service.impl;
import com.example.interview.enums.QuestionLevel;
import com.example.interview.service.DrawService;

import com.example.interview.vo.resp.QuestionRespVO;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@AllArgsConstructor
public class DrawServiceImpl implements DrawService {


    @Override
    public List<QuestionRespVO> draw(QuestionLevel level, int count, Integer allowDownLevels) {
        return List.of();
    }
}
