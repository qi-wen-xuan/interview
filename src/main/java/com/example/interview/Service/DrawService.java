package com.example.interview.Service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.interview.enums.QuestionLevel;
import com.example.interview.vo.req.QuestionCreateReqVO;
import com.example.interview.vo.req.QuestionQueryReqVO;
import com.example.interview.vo.req.QuestionUpdateReqVO;
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
    List<QuestionRespVO> draw(QuestionLevel level, int count, Integer allowDownLevels);

}
