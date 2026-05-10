package com.threekingdoms.quiz.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.util.List;
import lombok.Data;

@Data
public class BatchAnswerSubmitRequest {

  @NotNull(message = "用户ID不能为空")
  private Long userId;

  /** 每题的作答选项（与题目 ID 对应），顺序不影响判分；可为空数组表示交白卷 */
  @NotNull(message = "答案列表不能为 null")
  @Valid
  private List<AnswerItem> answers;

  /** 答题耗时（秒），不传则记为 0 */
  private Integer durationSeconds;

  @Data
  public static class AnswerItem {

    @NotNull(message = "题目ID不能为空")
    private Long questionId;

    @NotBlank(message = "答案不能为空")
    @Pattern(regexp = "^[ABCD]$", message = "答案必须是 A、B、C、D 之一")
    private String answer;
  }
}
