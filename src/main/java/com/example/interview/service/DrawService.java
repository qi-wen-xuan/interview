package com.example.interview.service;

import com.example.interview.enums.Level;
import com.example.interview.vo.resp.QuestionRespVO;

import java.util.List;

public interface DrawService {
    /**
     * 抽题主接口
     * @param level 请求的职级（如 C1）
     * @param count 想抽取的题目数量
     * @param allowDownLevels 最多可以向下降多少级（null 则使用配置）
     * @return 列表，大小最多为 count（当题库不足时可能小于 count）
     */
    List<QuestionRespVO> draw(Level level, int count, Integer allowDownLevels);

}
