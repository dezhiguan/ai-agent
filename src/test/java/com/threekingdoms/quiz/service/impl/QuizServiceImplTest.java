package com.threekingdoms.quiz.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.threekingdoms.quiz.dto.request.BatchAnswerSubmitRequest;
import com.threekingdoms.quiz.entity.QuizRecord;
import com.threekingdoms.quiz.entity.SysUser;
import com.threekingdoms.quiz.service.QuizQuestionService;
import com.threekingdoms.quiz.service.QuizRecordService;
import com.threekingdoms.quiz.service.SysUserService;
import com.threekingdoms.quiz.vo.QuizJudgeResultVo;
import com.threekingdoms.quiz.vo.WrongQuestionExplanationVo;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class QuizServiceImplTest {

  private static final long USER_ID = 1L;

  @Mock private SysUserService sysUserService;

  @Mock private QuizQuestionService quizQuestionService;

  @Mock private QuizRecordService quizRecordService;

  @InjectMocks private QuizServiceImpl quizService;

  @Captor private ArgumentCaptor<Map<Integer, String>> judgeAnswersCaptor;

  @BeforeEach
  void setUp() {
    reset(sysUserService, quizQuestionService, quizRecordService);

    SysUser user = new SysUser();
    user.setId(USER_ID);
    when(sysUserService.getById(USER_ID)).thenReturn(user);

    doAnswer(
            invocation -> {
              QuizRecord record = invocation.getArgument(0);
              record.setId(42L);
              return true;
            })
        .when(quizRecordService)
        .save(any(QuizRecord.class));
  }

  /** 模拟三题全对：总分 30，无错题解析 */
  @Test
  void submitAnswer_simulatesAllCorrect() {
    BatchAnswerSubmitRequest request = requestWithThreeAnswers("A", "B", "C");

    QuizJudgeResultVo judged = new QuizJudgeResultVo();
    judged.setTotalScore(30);
    judged.setCorrectByQuestionId(
        new LinkedHashMap<>(Map.of(1, true, 2, true, 3, true)));
    judged.setWrongExplanations(new ArrayList<>());
    when(quizQuestionService.judgeAnswers(any(), eq(USER_ID))).thenReturn(judged);

    QuizJudgeResultVo result = quizService.submitAnswer(request);

    assertEquals(30, result.getTotalScore());
    assertEquals(42L, result.getRecordId());
    assertTrue(result.getCorrectByQuestionId().values().stream().allMatch(Boolean::booleanValue));

    ArgumentCaptor<QuizRecord> recordCap = ArgumentCaptor.forClass(QuizRecord.class);
    verify(quizRecordService).save(recordCap.capture());
    assertEquals(30, recordCap.getValue().getScore());
    assertEquals(USER_ID, recordCap.getValue().getUserId());

    verify(quizQuestionService).judgeAnswers(judgeAnswersCaptor.capture(), eq(USER_ID));
    Map<Integer, String> expected = Map.of(1, "A", 2, "B", 3, "C");
    assertEquals(expected, judgeAnswersCaptor.getValue());
  }

  /** 模拟三题全错：总分 0，错题解析列表非空 */
  @Test
  void submitAnswer_simulatesAllWrong() {
    BatchAnswerSubmitRequest request = requestWithThreeAnswers("B", "C", "D");

    QuizJudgeResultVo judged = new QuizJudgeResultVo();
    judged.setTotalScore(0);
    judged.setCorrectByQuestionId(
        new LinkedHashMap<>(Map.of(1, false, 2, false, 3, false)));
    judged.setWrongExplanations(
        List.of(
            new WrongQuestionExplanationVo(1, "题1", "解析1"),
            new WrongQuestionExplanationVo(2, "题2", "解析2"),
            new WrongQuestionExplanationVo(3, "题3", "解析3")));
    when(quizQuestionService.judgeAnswers(any(), eq(USER_ID))).thenReturn(judged);

    QuizJudgeResultVo result = quizService.submitAnswer(request);

    assertEquals(0, result.getTotalScore());
    assertEquals(3, result.getWrongExplanations().size());
    assertTrue(result.getCorrectByQuestionId().values().stream().noneMatch(Boolean::booleanValue));

    ArgumentCaptor<QuizRecord> recordCap = ArgumentCaptor.forClass(QuizRecord.class);
    verify(quizRecordService).save(recordCap.capture());
    assertEquals(0, recordCap.getValue().getScore());
  }

  /** 模拟部分正确：两对一错，总分 20 */
  @Test
  void submitAnswer_simulatesPartialCorrect() {
    BatchAnswerSubmitRequest request = requestWithThreeAnswers("A", "B", "D");

    QuizJudgeResultVo judged = new QuizJudgeResultVo();
    judged.setTotalScore(20);
    judged.setCorrectByQuestionId(
        new LinkedHashMap<>(Map.of(1, true, 2, true, 3, false)));
    judged.setWrongExplanations(
        List.of(new WrongQuestionExplanationVo(3, "第三题题干", "第三题解析")));
    when(quizQuestionService.judgeAnswers(any(), eq(USER_ID))).thenReturn(judged);

    QuizJudgeResultVo result = quizService.submitAnswer(request);

    assertEquals(20, result.getTotalScore());
    assertEquals(1, result.getWrongExplanations().size());
    assertEquals(false, result.getCorrectByQuestionId().get(3));

    ArgumentCaptor<QuizRecord> recordCap = ArgumentCaptor.forClass(QuizRecord.class);
    verify(quizRecordService).save(recordCap.capture());
    assertEquals(20, recordCap.getValue().getScore());
  }

  private static BatchAnswerSubmitRequest requestWithThreeAnswers(String a1, String a2, String a3) {
    BatchAnswerSubmitRequest request = new BatchAnswerSubmitRequest();
    request.setUserId(USER_ID);
    request.setDurationSeconds(60);
    List<BatchAnswerSubmitRequest.AnswerItem> answers = new ArrayList<>();
    answers.add(item(1L, a1));
    answers.add(item(2L, a2));
    answers.add(item(3L, a3));
    request.setAnswers(answers);
    return request;
  }

  private static BatchAnswerSubmitRequest.AnswerItem item(long questionId, String answer) {
    BatchAnswerSubmitRequest.AnswerItem i = new BatchAnswerSubmitRequest.AnswerItem();
    i.setQuestionId(questionId);
    i.setAnswer(answer);
    return i;
  }
}
