package com.threekingdoms.quiz.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.threekingdoms.quiz.entity.QuizRecord;
import com.threekingdoms.quiz.mapper.QuizRecordMapper;
import com.threekingdoms.quiz.service.QuizRecordService;
import com.threekingdoms.quiz.vo.QuizRecordVo;
import org.springframework.stereotype.Service;

@Service
public class QuizRecordServiceImpl extends ServiceImpl<QuizRecordMapper, QuizRecord>
    implements QuizRecordService {

  @Override
  public Page<QuizRecordVo> pageRecordsByUser(long userId, long pageNum, long pageSize) {
    Page<QuizRecord> page = new Page<>(pageNum, pageSize);
    LambdaQueryWrapper<QuizRecord> wrapper = new LambdaQueryWrapper<>();
    wrapper.eq(QuizRecord::getUserId, userId);
    wrapper.orderByDesc(QuizRecord::getAnsweredAt);
    Page<QuizRecord> entityPage = page(page, wrapper);

    Page<QuizRecordVo> voPage =
        new Page<>(entityPage.getCurrent(), entityPage.getSize(), entityPage.getTotal());
    voPage.setRecords(entityPage.getRecords().stream().map(this::toVo).toList());
    return voPage;
  }

  private QuizRecordVo toVo(QuizRecord r) {
    QuizRecordVo vo = new QuizRecordVo();
    vo.setId(r.getId());
    vo.setUserId(r.getUserId());
    vo.setScore(r.getScore());
    vo.setAnsweredAt(r.getAnsweredAt());
    vo.setDurationSeconds(r.getDurationSeconds());
    return vo;
  }
}
