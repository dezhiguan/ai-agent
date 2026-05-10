package com.threekingdoms.quiz.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.threekingdoms.quiz.entity.QuizQuestion;
import com.threekingdoms.quiz.vo.QuestionPageVo;
import com.threekingdoms.quiz.vo.QuizJudgeResultVo;
import java.util.Map;

public interface QuizQuestionService extends IService<QuizQuestion> {

  Page<QuestionPageVo> pageQuestions(long pageNum, long pageSize, String difficulty);

  /**
   * 批量判题：Key 为题目 ID，Value 为用户选项（A/B/C/D）。
   *
   * @param answersByQuestionId 可为 null（按空答卷处理，避免 NPE）
   */
  QuizJudgeResultVo judgeAnswers(Map<Integer, String> answersByQuestionId);
}
