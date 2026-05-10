package com.threekingdoms.quiz;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class ApplicationContextLoadsTest {

  @Autowired private ObjectProvider<ChatClient.Builder> chatClientBuilderProvider;

  @Test
  void contextLoads_andDeepSeekMappedSpringAiBeansExist() {
    assertNotNull(chatClientBuilderProvider.getIfAvailable(), "ChatClient.Builder 应存在（Spring AI OpenAI 已配置）");
  }
}
