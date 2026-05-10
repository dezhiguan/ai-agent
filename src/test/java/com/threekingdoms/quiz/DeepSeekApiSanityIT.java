package com.threekingdoms.quiz;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.StringUtils;

/**
 * 仅在设置 {@code DEEPSEEK_API_KEY} 时启用：请求真实 DeepSeek，验证密钥与 base-url/model 可用。
 * 本地执行：{@code DEEPSEEK_API_KEY=sk-xxx mvn test -Dtest=DeepSeekApiSanityIT}
 */
@SpringBootTest
@ActiveProfiles("test")
@EnabledIfEnvironmentVariable(named = "DEEPSEEK_API_KEY", matches = ".+")
class DeepSeekApiSanityIT {

  @Autowired private ChatClient.Builder chatClientBuilder;

  @Test
  void deepSeekReturnsNonEmptyReply() {
    String content =
        chatClientBuilder
            .build()
            .prompt()
            .user("只回复一个字：好")
            .call()
            .content();
    assertTrue(StringUtils.hasText(content));
  }
}
