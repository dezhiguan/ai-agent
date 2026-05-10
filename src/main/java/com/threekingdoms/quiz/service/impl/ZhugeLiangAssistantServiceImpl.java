package com.threekingdoms.quiz.service.impl;

import com.threekingdoms.quiz.config.properties.ZhugeLiangAiProperties;
import com.threekingdoms.quiz.entity.QuizQuestion;
import com.threekingdoms.quiz.service.ZhugeLiangAssistantService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
@Slf4j
public class ZhugeLiangAssistantServiceImpl implements ZhugeLiangAssistantService {

  private static final String SYSTEM_PROMPT =
      """
      你是三国时期的丞相诸葛亮（孔明），精通兵法史传与《三国演义》典故。
      用户在做「三国演义」测验时答错了题。请你用诸葛亮的第一人称口吻作答：
      语气庄重谦和，可略带文言色彩，偶用成语；避免现代网络用语；不要编造与题干无关的情节。
      只输出一段简短解析（建议 80～150 字），直接给出正文，不要标题、不要列表、不要“孔明曰”之类套话。
      """;

  private final ObjectProvider<ChatClient.Builder> chatClientBuilderProvider;

  private final ZhugeLiangAiProperties properties;

  @Override
  public String explainWrongAnswer(QuizQuestion question, String userAnswer) {
    if (!properties.isEnabled()) {
      return null;
    }
    ChatClient.Builder builder = chatClientBuilderProvider.getIfAvailable();
    if (builder == null) {
      log.debug("ChatClient.Builder 未就绪，跳过诸葛亮 AI 解析");
      return null;
    }
    try {
      String userMessage = buildUserMessage(question, userAnswer);
      String content =
          builder
              .build()
              .prompt()
              .system(SYSTEM_PROMPT)
              .user(userMessage)
              .call()
              .content();
      if (content == null || !StringUtils.hasText(content)) {
        return null;
      }
      return content.trim();
    } catch (Exception e) {
      log.warn("诸葛亮智能助手调用失败，将使用题库原文解析: {}", e.getMessage());
      return null;
    }
  }

  private static String buildUserMessage(QuizQuestion q, String userAnswer) {
    String ua = userAnswer == null || userAnswer.isBlank() ? "（未作答或空白）" : userAnswer.trim();
    String ref =
        q.getExplanation() == null || q.getExplanation().isBlank()
            ? "（题库暂无参考解析）"
            : q.getExplanation().trim();
    return """
        【题干】
        %s

        【选项】
        A. %s
        B. %s
        C. %s
        D. %s

        【正确答案】%s
        【用户误选】%s

        【题库参考要点】（请融会贯通后用自己的话写出诸葛亮口吻解析，勿逐句照抄）
        %s
        """
        .formatted(
            q.getStem(),
            nullToDash(q.getOptionA()),
            nullToDash(q.getOptionB()),
            nullToDash(q.getOptionC()),
            nullToDash(q.getOptionD()),
            nullToDash(q.getCorrectAnswer()),
            ua,
            ref);
  }

  private static String nullToDash(String s) {
    return s == null ? "—" : s;
  }
}
