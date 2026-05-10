package com.threekingdoms.quiz.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@TableName("quiz_record")
public class QuizRecord {

  @TableId(type = IdType.AUTO)
  private Long id;

  @TableField("user_id")
  private Long userId;

  private Integer score;

  @TableField("answered_at")
  private LocalDateTime answeredAt;

  @TableField("duration_seconds")
  private Integer durationSeconds;
}
