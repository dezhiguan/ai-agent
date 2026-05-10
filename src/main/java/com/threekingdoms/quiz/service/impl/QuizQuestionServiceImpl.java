package com.threekingdoms.quiz.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.threekingdoms.quiz.entity.QuizQuestion;
import com.threekingdoms.quiz.mapper.QuizQuestionMapper;
import com.threekingdoms.quiz.service.QuizQuestionService;
import com.threekingdoms.quiz.service.ZhugeLiangAssistantService;
import com.threekingdoms.quiz.vo.QuestionPageVo;
import com.threekingdoms.quiz.vo.QuizJudgeResultVo;
import com.threekingdoms.quiz.vo.WrongQuestionExplanationVo;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class QuizQuestionServiceImpl extends ServiceImpl<QuizQuestionMapper, QuizQuestion>
    implements QuizQuestionService {

  private static final int SCORE_PER_QUESTION = 10;

  private static final String MISSING_QUESTION_HINT = "题目不存在或已删除";

  private final ZhugeLiangAssistantService zhugeLiangAssistantService;

  @Override
  public Page<QuestionPageVo> pageQuestions(long pageNum, long pageSize, String difficulty) {
    Page<QuizQuestion> page = new Page<>(pageNum, pageSize);
    LambdaQueryWrapper<QuizQuestion> wrapper = new LambdaQueryWrapper<>();
    if (StringUtils.hasText(difficulty)) {
      wrapper.eq(QuizQuestion::getDifficulty, difficulty);
    }
    wrapper.orderByAsc(QuizQuestion::getId);
    Page<QuizQuestion> entityPage = baseMapper.selectPage(page, wrapper);

    Page<QuestionPageVo> voPage = new Page<>(entityPage.getCurrent(), entityPage.getSize(), entityPage.getTotal());
    voPage.setRecords(entityPage.getRecords().stream().map(this::toQuestionPageVo).toList());
    return voPage;
  }

  @Override
  public QuizJudgeResultVo judgeAnswers(Map<Integer, String> answersByQuestionId) {
    QuizJudgeResultVo result = new QuizJudgeResultVo();
    if (answersByQuestionId == null || answersByQuestionId.isEmpty()) {
      return result;
    }

    List<Long> ids =
        answersByQuestionId.keySet().stream()
            .filter(Objects::nonNull)
            .map(Integer::longValue)
            .distinct()
            .collect(Collectors.toList());

    Map<Long, QuizQuestion> questionById =
        CollectionUtils.isEmpty(ids)
            ? Map.of()
            : listByIds(ids).stream()
                .filter(Objects::nonNull)
                .filter(q -> q.getId() != null)
                .collect(Collectors.toMap(QuizQuestion::getId, q -> q, (a, b) -> a));

    int total = 0;
    Map<Integer, Boolean> correctness = new LinkedHashMap<>();

    for (Map.Entry<Integer, String> entry : answersByQuestionId.entrySet()) {
      Integer questionId = entry.getKey();
      if (questionId == null) {
        continue;
      }

      QuizQuestion question = questionById.get(questionId.longValue());
      boolean ok = question != null && matchesChoice(entry.getValue(), question.getCorrectAnswer());
      correctness.put(questionId, ok);
      if (ok) {
        total += SCORE_PER_QUESTION;
      } else {
        if (question != null) {
          String explanation = resolveWrongExplanation(question, entry.getValue());
          result.getWrongExplanations()
              .add(new WrongQuestionExplanationVo(questionId, question.getStem(), explanation));
        } else {
          result.getWrongExplanations()
              .add(new WrongQuestionExplanationVo(questionId, null, MISSING_QUESTION_HINT));
        }
      }
    }

    result.setTotalScore(total);
    result.setCorrectByQuestionId(correctness);
    return result;
  }

  /**
   * 答错时优先使用诸葛亮 AI 解析；若未启用、调用失败或返回空，则回退为题库原文解析。
   */
  private String resolveWrongExplanation(QuizQuestion question, String userAnswer) {
    String ai = zhugeLiangAssistantService.explainWrongAnswer(question, userAnswer);
    if (StringUtils.hasText(ai)) {
      return ai.trim();
    }
    return safeExplanation(question.getExplanation());
  }

  private static String safeExplanation(String explanation) {
    return explanation == null ? "" : explanation;
  }

  /** 比较用户选项与库中正确答案；任意一侧为 null/空白则视为不匹配 */
  private static boolean matchesChoice(String userAnswer, String correctAnswerInDb) {
    String u = normalizeChoice(userAnswer);
    String c = normalizeChoice(correctAnswerInDb);
    return !u.isEmpty() && u.equals(c);
  }

  private static String normalizeChoice(String raw) {
    if (raw == null) {
      return "";
    }
    String t = raw.trim();
    return t.isEmpty() ? "" : t.toUpperCase();
  }

  private QuestionPageVo toQuestionPageVo(QuizQuestion q) {
    QuestionPageVo vo = new QuestionPageVo();
    vo.setId(q.getId());
    vo.setStem(q.getStem());
    vo.setOptionA(q.getOptionA());
    vo.setOptionB(q.getOptionB());
    vo.setOptionC(q.getOptionC());
    vo.setOptionD(q.getOptionD());
    vo.setDifficulty(q.getDifficulty());
    return vo;
  }
}
