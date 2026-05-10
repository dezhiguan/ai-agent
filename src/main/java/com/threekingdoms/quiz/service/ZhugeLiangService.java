package com.threekingdoms.quiz.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.stereotype.Service;
import java.util.Map;

@Service
public class ZhugeLiangService {
    private final ChatClient chatClient;

    // 通过构造函数注入 Spring AI 的核心客户端 ChatClient
    public ZhugeLiangService(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    public String explainMistake(String questionText, String correctAnswer, String userOption) {
        // 定义诸葛亮的人设和任务模板
        String systemPrompt = """
                你是一名精通三国历史的智者诸葛亮（孔明）。
                你的说话风格要古风盎然，经常引用《三国演义》中的典故，自称'亮'或'吾'。
                当用户答错题目时，你要用幽默且带有教育意义的口吻给出解析，并在最后鼓励用户。
                """;

        // 将具体的题目信息填入模板
        PromptTemplate promptTemplate = new PromptTemplate("""
                亮，吾在答题时遇到一题：【{question}】，
                正确答案是【{correct}】，但吾错选了【{wrong}】。
                请为吾解析此题。
                """);

        Map<String, Object> params = Map.of(
                "question", questionText,
                "correct", correctAnswer,
                "wrong", userOption
        );

        // 组装系统人设和具体提问，调用大模型获取回答
        return chatClient.prompt()
                .system(systemPrompt)
                .user(promptTemplate.render(params))
                .call()
                .content();
    }
}