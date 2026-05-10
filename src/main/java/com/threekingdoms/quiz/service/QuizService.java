package com.threekingdoms.quiz.service;

import com.threekingdoms.quiz.dto.request.BatchAnswerSubmitRequest;
import com.threekingdoms.quiz.vo.QuizJudgeResultVo;

public interface QuizService {

  /**
   * 提交答卷：根据用户答案列表判题、汇总得分，并写入 {@code quiz_record}。
   */
  QuizJudgeResultVo submitAnswer(BatchAnswerSubmitRequest request);
}
