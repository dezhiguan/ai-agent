package com.threekingdoms.quiz.vo;

import lombok.Data;

/** 分页列表展示用（不包含正确答案与解析，防止泄题） */
@Data
public class QuestionPageVo {

  private Long id;

  private String stem;

  private String optionA;

  private String optionB;

  private String optionC;

  private String optionD;

  private String difficulty;
}
