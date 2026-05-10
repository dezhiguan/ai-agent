<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import {
  HistoryOutlined,
  LeftOutlined,
  RightOutlined,
  SendOutlined,
} from '@ant-design/icons-vue'
import { message, Modal } from 'ant-design-vue'
import {
  fetchQuestionPage,
  submitAnswers,
  type QuestionPageVo,
  type QuizJudgeResultVo,
} from '@/api/quiz'
import { RouterLink } from 'vue-router'

type Choice = 'A' | 'B' | 'C' | 'D'

const questions = ref<QuestionPageVo[]>([])
const currentIndex = ref(0)
const answers = reactive<Record<number, Choice>>({})
const loading = ref(true)
const submitting = ref(false)
const startedAt = ref(0)

/** 最近一次交卷结果（含错题解析） */
const lastResult = ref<QuizJudgeResultVo | null>(null)
const resultModalOpen = ref(false)

const userId = Number(import.meta.env.VITE_USER_ID ?? 1)

const current = computed(() => questions.value[currentIndex.value] ?? null)

const optionRows = computed(() => {
  const q = current.value
  if (!q) return []
  return [
    { key: 'A' as const, text: q.optionA },
    { key: 'B' as const, text: q.optionB },
    { key: 'C' as const, text: q.optionC },
    { key: 'D' as const, text: q.optionD },
  ]
})

const progressLabel = computed(() => {
  const n = questions.value.length
  if (!n) return ''
  return `第 ${currentIndex.value + 1} 题 / 共 ${n} 题`
})

const canPrev = computed(() => currentIndex.value > 0)
const canNext = computed(() => currentIndex.value < questions.value.length - 1)

function pick(key: Choice) {
  const q = current.value
  if (!q) return
  answers[q.id] = key
}

function prev() {
  if (canPrev.value) currentIndex.value--
}

function next() {
  if (canNext.value) currentIndex.value++
}

function buildAnswersPayload() {
  const list: Array<{ questionId: number; answer: string }> = []
  for (const q of questions.value) {
    const a = answers[q.id]
    if (a) list.push({ questionId: q.id, answer: a })
  }
  return list
}

async function loadQuestions() {
  loading.value = true
  try {
    const page = await fetchQuestionPage(1, 200)
    questions.value = page.records ?? []
    startedAt.value = Date.now()
  } catch (e) {
    message.error(e instanceof Error ? e.message : '题目加载失败')
  } finally {
    loading.value = false
  }
}

function closeResultModal() {
  resultModalOpen.value = false
  lastResult.value = null
}

function confirmSubmit() {
  const payload = buildAnswersPayload()
  if (payload.length === 0) {
    message.warning('请至少作答一题后再提交')
    return
  }
  Modal.confirm({
    title: '确认交卷',
    content: `已作答 ${payload.length} / ${questions.value.length} 题，提交后生成成绩记录。`,
    okText: '提交',
    cancelText: '取消',
    centered: true,
    async onOk() {
      submitting.value = true
      try {
        const durationSeconds = Math.max(
          0,
          Math.round((Date.now() - startedAt.value) / 1000),
        )
        const res = await submitAnswers({
          userId,
          answers: payload,
          durationSeconds,
        })
        lastResult.value = res
        resultModalOpen.value = true
      } catch (e) {
        message.error(e instanceof Error ? e.message : '提交失败')
      } finally {
        submitting.value = false
      }
    },
  })
}

onMounted(loadQuestions)
</script>

<template>
  <div class="quiz-page">
    <header class="quiz-header">
      <div class="title-wrap">
        <h1 class="title">三国争霸</h1>
        <p class="subtitle">三国演义 · 闯关答题</p>
      </div>
      <div v-if="questions.length" class="progress">{{ progressLabel }}</div>
    </header>

    <main class="quiz-main">
      <a-spin :spinning="loading" size="large">
        <a-card v-if="current" class="question-card" :bordered="false">
          <div class="difficulty" v-if="current.difficulty">
            <span class="tag">{{ current.difficulty }}</span>
          </div>
          <p class="stem">{{ current.stem }}</p>

          <div class="options">
            <a-button
              v-for="opt in optionRows"
              :key="opt.key"
              class="option-btn"
              :type="answers[current!.id] === opt.key ? 'primary' : 'default'"
              block
              size="large"
              @click="pick(opt.key)"
            >
              <span class="opt-key">{{ opt.key }}</span>
              <span class="opt-body">{{ opt.text }}</span>
            </a-button>
          </div>
        </a-card>

        <a-empty
          v-else-if="!loading"
          class="empty"
          description="暂无题目，请确认后端服务与题库数据"
        />
      </a-spin>
    </main>

    <footer class="quiz-footer">
      <a-space :size="16" wrap>
        <a-button size="large" :disabled="!canPrev" @click="prev">
          <template #icon><LeftOutlined /></template>
          上一题
        </a-button>
        <a-button size="large" :disabled="!canNext" @click="next">
          下一题
          <template #icon><RightOutlined /></template>
        </a-button>
        <a-button
          type="primary"
          size="large"
          :loading="submitting"
          :disabled="loading || questions.length === 0"
          @click="confirmSubmit"
        >
          <template #icon><SendOutlined /></template>
          提交
        </a-button>
      </a-space>
    </footer>

    <a-modal
      v-model:open="resultModalOpen"
      title="答题结果"
      :footer="null"
      width="min(92vw, 560px)"
      centered
      destroy-on-close
      @cancel="closeResultModal"
    >
      <template v-if="lastResult">
        <div class="result-summary">
          <p class="result-score">
            得分 <strong>{{ lastResult.totalScore }}</strong> 分
          </p>
          <p v-if="lastResult.recordId != null" class="result-meta">
            答题记录 ID：{{ lastResult.recordId }}
          </p>
        </div>

        <a-divider style="margin: 12px 0" />

        <template v-if="(lastResult.wrongExplanations?.length ?? 0) > 0">
          <p class="result-section-title">错题解析</p>
          <a-list
            class="wrong-list"
            size="small"
            :data-source="lastResult.wrongExplanations"
          >
            <template #renderItem="{ item }">
              <a-list-item class="wrong-item">
                <a-list-item-meta>
                  <template #title>
                    <span class="wrong-stem-label">第 {{ item.questionId }} 题</span>
                    <span v-if="item.stem" class="wrong-stem">{{
                      item.stem
                    }}</span>
                  </template>
                  <template #description>
                    <div class="wrong-explanation">{{ item.explanation }}</div>
                  </template>
                </a-list-item-meta>
              </a-list-item>
            </template>
          </a-list>
        </template>
        <a-alert v-else type="success" show-icon message="全部正确，暂无错题解析。" />
      </template>
    </a-modal>
  </div>
</template>

<style scoped>
.quiz-page {
  min-height: 100%;
  display: flex;
  flex-direction: column;
  padding: clamp(1rem, 4vw, 2rem);
  max-width: 720px;
  margin: 0 auto;
}

.quiz-header {
  text-align: center;
  margin-bottom: 1.5rem;
  position: relative;
}

.header-actions {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 0.75rem;
  margin-top: 1rem;
}

.nav-history {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 6px 14px;
  font-size: 0.9rem;
  color: #f0e6d8;
  border: 1px solid rgba(185, 58, 47, 0.45);
  border-radius: 999px;
  background: rgba(26, 18, 14, 0.35);
  text-decoration: none;
  letter-spacing: 0.08em;
  transition:
    background 0.2s,
    border-color 0.2s;
}

.nav-history:hover {
  color: #fff;
  border-color: rgba(240, 230, 216, 0.45);
  background: rgba(185, 58, 47, 0.28);
}

.title-wrap {
  padding: 0.5rem 0 1rem;
  border-bottom: 2px solid rgba(185, 58, 47, 0.45);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.25);
}

.title {
  margin: 0;
  font-family: 'Ma Shan Zheng', 'Noto Serif SC', serif;
  font-size: clamp(2.25rem, 6vw, 3.25rem);
  font-weight: 400;
  letter-spacing: 0.12em;
  color: #f0e6d8;
  text-shadow:
    0 2px 0 rgba(26, 18, 14, 0.9),
    0 0 40px rgba(185, 58, 47, 0.35);
}

.subtitle {
  margin: 0.35rem 0 0;
  font-size: 0.95rem;
  color: rgba(232, 226, 216, 0.55);
  letter-spacing: 0.25em;
}

.progress {
  margin-top: 1rem;
  font-size: 0.9rem;
  color: rgba(232, 226, 216, 0.65);
  letter-spacing: 0.08em;
}

.quiz-main {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.question-card {
  background: linear-gradient(145deg, #f7efe6 0%, #ebe2d6 100%) !important;
  color: #2a221c;
  border-radius: 14px !important;
  box-shadow:
    0 4px 0 rgba(42, 34, 28, 0.12),
    0 24px 48px rgba(0, 0, 0, 0.35),
    inset 0 1px 0 rgba(255, 255, 255, 0.65);
  border: 1px solid rgba(58, 48, 40, 0.15) !important;
}

.question-card :deep(.ant-card-body) {
  padding: clamp(1.25rem, 4vw, 1.75rem);
}

.difficulty {
  margin-bottom: 0.75rem;
}

.tag {
  display: inline-block;
  padding: 0.15rem 0.65rem;
  font-size: 0.75rem;
  letter-spacing: 0.15em;
  color: #8b2e22;
  background: rgba(185, 58, 47, 0.12);
  border: 1px solid rgba(185, 58, 47, 0.35);
  border-radius: 4px;
}

.stem {
  margin: 0 0 1.25rem;
  font-size: 1.1rem;
  line-height: 1.75;
  font-weight: 600;
}

.options {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.option-btn {
  height: auto !important;
  min-height: 52px;
  padding: 10px 14px !important;
  text-align: left;
  border-radius: 10px !important;
  border: 1px solid rgba(58, 48, 40, 0.2) !important;
  background: rgba(255, 255, 255, 0.55) !important;
  color: #2a221c !important;
}

.option-btn:hover {
  border-color: rgba(185, 58, 47, 0.55) !important;
  color: #2a221c !important;
}

.option-btn.ant-btn-primary {
  border-color: #b93a2f !important;
  background: linear-gradient(180deg, #c94a3e 0%, #b93a2f 100%) !important;
  color: #fff !important;
}

.opt-key {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 28px;
  height: 28px;
  margin-right: 12px;
  flex-shrink: 0;
  border-radius: 6px;
  font-weight: 700;
  font-size: 0.85rem;
  background: rgba(42, 34, 28, 0.08);
  border: 1px solid rgba(42, 34, 28, 0.15);
}

.option-btn.ant-btn-primary .opt-key {
  background: rgba(255, 255, 255, 0.2);
  border-color: rgba(255, 255, 255, 0.35);
  color: #fff;
}

.opt-body {
  flex: 1;
  font-size: 0.98rem;
  line-height: 1.5;
}

.empty {
  margin: 3rem 0;
  color: rgba(232, 226, 216, 0.55);
}

.quiz-footer {
  margin-top: auto;
  padding-top: 1.75rem;
  display: flex;
  justify-content: center;
}

.quiz-footer :deep(.ant-btn) {
  min-width: 118px;
}

.result-summary {
  text-align: center;
}

.result-score {
  margin: 0;
  font-size: 1.15rem;
  color: #2a221c;
}

.result-score strong {
  color: #b93a2f;
  font-size: 1.35rem;
}

.result-meta {
  margin: 0.35rem 0 0;
  font-size: 0.85rem;
  color: rgba(42, 34, 28, 0.55);
}

.result-section-title {
  margin: 0 0 0.75rem;
  font-weight: 600;
  color: #5c4033;
  letter-spacing: 0.06em;
}

.wrong-list :deep(.ant-list-item) {
  padding: 10px 0;
  border-block-end-color: rgba(58, 48, 40, 0.12);
}

.wrong-stem-label {
  font-weight: 600;
  margin-right: 8px;
  color: #8b2e22;
}

.wrong-stem {
  font-weight: normal;
  color: #2a221c;
}

.wrong-explanation {
  margin-top: 6px;
  line-height: 1.65;
  color: #3d342c;
  white-space: pre-wrap;
}
</style>
