package com.threekingdoms.quiz.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WrongQuestionExplanationVo {

  private Integer questionId;

  /** 题干摘要，题目不存在时可能为 null */
  private String stem;

  /** 解析说明；题目不存在时为提示文案 */
  private String explanation;
}
