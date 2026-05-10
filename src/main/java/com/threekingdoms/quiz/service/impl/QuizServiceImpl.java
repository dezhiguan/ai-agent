package com.threekingdoms.quiz.service.impl;

import com.threekingdoms.quiz.common.exception.BusinessException;
import com.threekingdoms.quiz.dto.request.BatchAnswerSubmitRequest;
import com.threekingdoms.quiz.entity.QuizRecord;
import com.threekingdoms.quiz.service.QuizQuestionService;
import com.threekingdoms.quiz.service.QuizRecordService;
import com.threekingdoms.quiz.service.QuizService;
import com.threekingdoms.quiz.service.SysUserService;
import com.threekingdoms.quiz.vo.QuizJudgeResultVo;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class QuizServiceImpl implements QuizService {

  private final SysUserService sysUserService;

  private final QuizQuestionService quizQuestionService;

  private final QuizRecordService quizRecordService;

  @Override
  @Transactional(rollbackFor = Exception.class)
  public QuizJudgeResultVo submitAnswer(BatchAnswerSubmitRequest request) {
    if (sysUserService.getById(request.getUserId()) == null) {
      throw new BusinessException("用户不存在");
    }

    Map<Integer, String> answersByQuestionId = new LinkedHashMap<>();
    for (BatchAnswerSubmitRequest.AnswerItem item : request.getAnswers()) {
      answersByQuestionId.put(Math.toIntExact(item.getQuestionId()), item.getAnswer());
    }

    QuizJudgeResultVo result = quizQuestionService.judgeAnswers(answersByQuestionId);

    QuizRecord record = new QuizRecord();
    record.setUserId(request.getUserId());
    record.setScore(result.getTotalScore());
    record.setAnsweredAt(LocalDateTime.now());
    record.setDurationSeconds(request.getDurationSeconds() != null ? request.getDurationSeconds() : 0);
    quizRecordService.save(record);

    result.setRecordId(record.getId());
    return result;
  }
}
