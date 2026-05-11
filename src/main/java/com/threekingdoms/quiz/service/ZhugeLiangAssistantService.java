package com.threekingdoms.quiz.service;

import com.threekingdoms.quiz.entity.QuizQuestion;

/**
 * 「诸葛亮智能助手」：在用户答错时，根据题目情境生成三国人物口吻的解析文本。
 *
 * <p>若 AI 不可用或调用失败，返回 {@code null}，由业务层回退到题库原文解析。
 */
public interface ZhugeLiangAssistantService {

  /**
   * @param question 本题实体（须非 null）
   * @param userAnswer 用户所选选项，如 A/B/C/D（可为 null，表示未作答）
   * @param userId 当前答题用户 ID；非空时启用工具调用以查询历史战绩并生成针对性点评
   * @return AI 生成的解析；失败或未启用时返回 null
   */
  String explainWrongAnswer(QuizQuestion question, String userAnswer, Long userId);
}
