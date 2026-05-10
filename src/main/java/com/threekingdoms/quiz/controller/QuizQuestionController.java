package com.threekingdoms.quiz.controller;

import com.threekingdoms.quiz.common.Result;
import com.threekingdoms.quiz.dto.request.BatchAnswerSubmitRequest;
import com.threekingdoms.quiz.service.QuizQuestionService;
import com.threekingdoms.quiz.service.QuizService;
import com.threekingdoms.quiz.vo.QuizJudgeResultVo;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/quiz/questions")
@RequiredArgsConstructor
public class QuizQuestionController {

  private final QuizQuestionService quizQuestionService;

  private final QuizService quizService;

  /**
   * 题目分页查询（不含正确答案与解析）。
   *
   * @param page 页码，从 1 开始
   * @param size 每页条数
   * @param difficulty 可选：简单 / 中等 / 困难
   */
  @GetMapping
  public Result page(
      @RequestParam(defaultValue = "1") long page,
      @RequestParam(defaultValue = "10") long size,
      @RequestParam(required = false) String difficulty) {
    return Result.success(quizQuestionService.pageQuestions(page, size, difficulty));
  }

  /** 提交答卷（多题），返回总分、每题对错、错题解析，并落库答题记录 */
  @PostMapping("/answers")
  public Result submitAnswer(@Valid @RequestBody BatchAnswerSubmitRequest request) {
    QuizJudgeResultVo vo = quizService.submitAnswer(request);
    return Result.success(vo);
  }
}
