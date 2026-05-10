package com.threekingdoms.quiz.vo;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.Data;

/** 批量判题结果：总分、每题是否正确、错题解析 */
@Data
public class QuizJudgeResultVo {

  /** 总得分 */
  private int totalScore;

  /**
   * 每道题是否正确；Key 与入参题目 ID 一致。
   * 使用 {@link LinkedHashMap} 时在构造结果处保持插入顺序（若入参为 LinkedHashMap）。
   */
  private Map<Integer, Boolean> correctByQuestionId;

  /** 仅包含答错的题目解析（便于复习）；题目不存在时也会有对应条目 */
  private List<WrongQuestionExplanationVo> wrongExplanations;

  /** 本次提交写入库后的答题记录主键；仅在一次完整提交并落库后存在 */
  private Long recordId;

  public QuizJudgeResultVo() {
    this.correctByQuestionId = new LinkedHashMap<>();
    this.wrongExplanations = new ArrayList<>();
  }
}
