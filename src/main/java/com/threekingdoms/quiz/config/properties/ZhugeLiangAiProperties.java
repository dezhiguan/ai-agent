package com.threekingdoms.quiz.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "app.ai.zhugeliang")
public class ZhugeLiangAiProperties {

  /** 是否启用诸葛亮口吻的 AI 错题解析 */
  private boolean enabled = true;
}
