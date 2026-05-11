package com.threekingdoms.quiz.ai;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.threekingdoms.quiz.entity.QuizRecord;
import com.threekingdoms.quiz.service.QuizRecordService;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

/**
 * 供 Spring AI（诸葛亮智能体）在「工具调用 / Tool Calling」时拉取业务数据，类似 LangChain4j 的 Tools。
 *
 * <p>模型通过名称与参数 JSON 请求执行，由 Spring AI 负责在本机调用 Java 方法并将结果回传给模型继续生成。
 */
@Component
@RequiredArgsConstructor
public class ZhugeLiangQuizTools {

  private static final int RECENT_LIMIT = 20;

  private final QuizRecordService quizRecordService;

  @Tool(
      name = "getUserQuizHistorySummary",
      description =
          "查询指定用户在《三国演义》测验中的历史交卷记录摘要。返回最近若干次得分、累计交卷次数、平均分、最高/最低分及得分时间线（新到旧），"
              + "供诸葛亮结合用户真实水平对错题作针对性点评。无记录时表示该用户此前无已落库成绩。")
  public String getUserQuizHistorySummary(
      @ToolParam(description = "用户主键 ID，与 sys_user.id、交卷时的 userId 一致") long userId) {
    long totalSessions = quizRecordService.lambdaQuery().eq(QuizRecord::getUserId, userId).count();

    Page<QuizRecord> page =
        quizRecordService.page(
            new Page<>(1, RECENT_LIMIT),
            new LambdaQueryWrapper<QuizRecord>()
                .eq(QuizRecord::getUserId, userId)
                .orderByDesc(QuizRecord::getAnsweredAt));

    List<QuizRecord> recent = page.getRecords();
    if (recent.isEmpty()) {
      return String.format(
          "用户 %d 暂无历史交卷记录（totalSessions=0）。可能是首次答题，或此前成绩未写入 quiz_record。", userId);
    }

    List<Integer> scores = recent.stream().map(QuizRecord::getScore).collect(Collectors.toList());
    int sum = scores.stream().mapToInt(Integer::intValue).sum();
    double avg = sum / (double) scores.size();
    int max = scores.stream().mapToInt(Integer::intValue).max().orElse(0);
    int min = scores.stream().mapToInt(Integer::intValue).min().orElse(0);

    String scoreTimeline =
        recent.stream()
            .map(r -> r.getScore() + "分@" + r.getAnsweredAt())
            .collect(Collectors.joining("；"));

    return String.format(
        "用户 %d：累计交卷 %d 次（数据库总量）。最近 %d 次统计：平均分 %.1f，最高 %d 分，最低 %d 分。"
            + "最近得分序列（新→旧）：%s",
        userId, totalSessions, recent.size(), avg, max, min, scoreTimeline);
  }
}
