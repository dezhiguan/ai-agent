import { http, unwrap, type ApiResult } from './http'

export interface QuestionPageVo {
  id: number
  stem: string
  optionA: string
  optionB: string
  optionC: string
  optionD: string
  difficulty: string
}

export interface PagePayload<T> {
  records: T[]
  total: number
  current: number
  size: number
}

export interface QuizJudgeResultVo {
  totalScore: number
  correctByQuestionId: Record<string, boolean>
  wrongExplanations: Array<{
    questionId: number
    stem: string | null
    explanation: string
    /** ai=诸葛亮大模型（可含历史战绩工具）；db=题库原文；missing=题目不存在 */
    source?: 'ai' | 'db' | 'missing'
  }>
  recordId: number | null
}

export async function fetchQuestionPage(page = 1, size = 100) {
  return unwrap(
    http.get<ApiResult<PagePayload<QuestionPageVo>>>('/quiz/questions', {
      params: { page, size },
    }),
  )
}

export async function submitAnswers(body: {
  userId: number
  answers: Array<{ questionId: number; answer: string }>
  durationSeconds?: number
}) {
  return unwrap(http.post<ApiResult<QuizJudgeResultVo>>('/quiz/questions/answers', body))
}

export interface QuizRecordVo {
  id: number
  userId: number
  score: number
  answeredAt: string
  durationSeconds: number
}

/** 分页查询当前用户的答题历史（按交卷时间倒序） */
export async function fetchQuizRecords(userId: number, page = 1, size = 10) {
  return unwrap(
    http.get<ApiResult<PagePayload<QuizRecordVo>>>('/quiz/records', {
      params: { userId, page, size },
    }),
  )
}
