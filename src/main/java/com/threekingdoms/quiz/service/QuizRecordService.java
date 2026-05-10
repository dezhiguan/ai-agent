package com.threekingdoms.quiz.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.threekingdoms.quiz.entity.QuizRecord;
import com.threekingdoms.quiz.vo.QuizRecordVo;

public interface QuizRecordService extends IService<QuizRecord> {

  /** 按用户分页查询答题记录，最近提交的在前 */
  Page<QuizRecordVo> pageRecordsByUser(long userId, long pageNum, long pageSize);
}
