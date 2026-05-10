package com.threekingdoms.quiz.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("quiz_question")
public class QuizQuestion {

  @TableId(type = IdType.AUTO)
  private Long id;

  private String stem;

  @TableField("option_a")
  private String optionA;

  @TableField("option_b")
  private String optionB;

  @TableField("option_c")
  private String optionC;

  @TableField("option_d")
  private String optionD;

  @TableField("correct_answer")
  private String correctAnswer;

  private String difficulty;

  private String explanation;
}
