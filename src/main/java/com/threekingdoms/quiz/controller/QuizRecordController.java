package com.threekingdoms.quiz.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.threekingdoms.quiz.common.Result;
import com.threekingdoms.quiz.service.QuizRecordService;
import com.threekingdoms.quiz.vo.QuizRecordVo;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/quiz/records")
@RequiredArgsConstructor
public class QuizRecordController {

  private final QuizRecordService quizRecordService;

  /**
   * 分页查询某用户的答题历史（按交卷时间倒序）。
   *
   * @param userId 用户 ID（当前项目未接登录，与前端 VITE_USER_ID 一致）
   */
  @GetMapping
  public Result pageByUser(
      @RequestParam long userId,
      @RequestParam(defaultValue = "1") long page,
      @RequestParam(defaultValue = "10") long size) {
    Page<QuizRecordVo> voPage = quizRecordService.pageRecordsByUser(userId, page, size);
    return Result.success(voPage);
  }
}
