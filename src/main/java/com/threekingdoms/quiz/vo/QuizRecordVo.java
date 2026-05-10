package com.threekingdoms.quiz.vo;

import java.time.LocalDateTime;
import lombok.Data;

/** 答题记录（列表展示） */
@Data
public class QuizRecordVo {

  private Long id;

  private Long userId;

  private Integer score;

  private LocalDateTime answeredAt;

  private Integer durationSeconds;
}
