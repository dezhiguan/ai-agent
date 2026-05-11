package com.threekingdoms.quiz.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class WrongQuestionExplanationVo {

  private Integer questionId;

  /** 题干摘要，题目不存在时可能为 null */
  private String stem;

  /** 解析说明 */
  private String explanation;

  /**
   * 解析来源：{@code ai} 诸葛亮大模型（可含工具调用查历史）；{@code db} 题库原文；{@code missing}
   * 题目不存在时的提示。
   */
  private String source;

  public WrongQuestionExplanationVo(Integer questionId, String stem, String explanation) {
    this(questionId, stem, explanation, "db");
  }

  public WrongQuestionExplanationVo(
      Integer questionId, String stem, String explanation, String source) {
    this.questionId = questionId;
    this.stem = stem;
    this.explanation = explanation;
    this.source = source;
  }
}
