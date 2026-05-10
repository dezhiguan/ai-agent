<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { HistoryOutlined, HomeOutlined } from '@ant-design/icons-vue'
import { message } from 'ant-design-vue'
import type { TablePaginationConfig } from 'ant-design-vue'
import { fetchQuizRecords, type QuizRecordVo } from '@/api/quiz'
import { RouterLink } from 'vue-router'

const userId = Number(import.meta.env.VITE_USER_ID ?? 1)

const loading = ref(true)
const dataSource = ref<QuizRecordVo[]>([])

const pagination = reactive<TablePaginationConfig>({
  current: 1,
  pageSize: 10,
  total: 0,
  showSizeChanger: true,
  pageSizeOptions: ['10', '20', '50'],
  showTotal: (total) => `共 ${total} 条记录`,
})

const columns = [
  { title: '记录 ID', dataIndex: 'id', key: 'id', width: 100 },
  { title: '得分', dataIndex: 'score', key: 'score', width: 90 },
  { title: '交卷时间', dataIndex: 'answeredAt', key: 'answeredAt' },
  {
    title: '耗时',
    dataIndex: 'durationSeconds',
    key: 'durationSeconds',
    width: 110,
  },
]

const summaryLine = computed(() => {
  const t = pagination.total ?? 0
  if (t === 0) return '暂无答题记录，先去首页答题吧。'
  return `当前用户（ID ${userId}）共有 ${t} 次交卷记录，按时间从新到旧排列。`
})

function formatAnsweredAt(raw: string) {
  if (!raw) return '—'
  const d = new Date(raw)
  if (Number.isNaN(d.getTime())) return raw
  return d.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit',
    hour12: false,
  })
}

async function loadRecords() {
  loading.value = true
  try {
    const page = await fetchQuizRecords(
      userId,
      pagination.current ?? 1,
      pagination.pageSize ?? 10,
    )
    dataSource.value = page.records ?? []
    pagination.total = page.total ?? 0
  } catch (e) {
    message.error(e instanceof Error ? e.message : '加载历史记录失败')
    dataSource.value = []
  } finally {
    loading.value = false
  }
}

function onTableChange(pag: TablePaginationConfig) {
  if (pag?.current != null) pagination.current = pag.current
  if (pag?.pageSize != null) pagination.pageSize = pag.pageSize
  loadRecords()
}

onMounted(loadRecords)
</script>

<template>
  <div class="history-page">
    <header class="history-header">
      <div class="title-wrap">
        <h1 class="title">历史成绩</h1>
        <p class="subtitle">历次交卷得分与用时</p>
      </div>
      <RouterLink to="/" class="back-home">
        <HomeOutlined />
        返回答题
      </RouterLink>
    </header>

    <p class="hint">{{ summaryLine }}</p>

    <a-card class="table-card" :bordered="false">
      <template #title>
        <span class="card-title">
          <HistoryOutlined />
          答题记录
        </span>
      </template>
      <a-table
        row-key="id"
        :columns="columns"
        :data-source="dataSource"
        :loading="loading"
        :pagination="pagination"
        size="middle"
        @change="onTableChange"
      >
        <template #bodyCell="{ column, text, record }">
          <template v-if="column.key === 'answeredAt'">
            {{ formatAnsweredAt(record.answeredAt) }}
          </template>
          <template v-else-if="column.key === 'durationSeconds'">
            {{
              record.durationSeconds != null ? `${record.durationSeconds} 秒` : '—'
            }}
          </template>
          <template v-else>{{ text }}</template>
        </template>
      </a-table>
    </a-card>
  </div>
</template>

<style scoped>
.history-page {
  min-height: 100%;
  padding: clamp(1rem, 4vw, 2rem);
  max-width: 960px;
  margin: 0 auto;
}

.history-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 1rem;
  flex-wrap: wrap;
  margin-bottom: 1rem;
}

.title-wrap {
  padding: 0.5rem 0 1rem;
  border-bottom: 2px solid rgba(185, 58, 47, 0.45);
}

.title {
  margin: 0;
  font-family: 'Ma Shan Zheng', 'Noto Serif SC', serif;
  font-size: clamp(1.75rem, 5vw, 2.5rem);
  font-weight: 400;
  letter-spacing: 0.1em;
  color: #f0e6d8;
  text-shadow:
    0 2px 0 rgba(26, 18, 14, 0.9),
    0 0 32px rgba(185, 58, 47, 0.35);
}

.subtitle {
  margin: 0.35rem 0 0;
  font-size: 0.9rem;
  color: rgba(232, 226, 216, 0.55);
  letter-spacing: 0.2em;
}

.back-home {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 8px 14px;
  border-radius: 10px;
  color: #f0e6d8;
  border: 1px solid rgba(185, 58, 47, 0.45);
  background: rgba(26, 18, 14, 0.35);
  text-decoration: none;
  transition:
    background 0.2s,
    border-color 0.2s;
}

.back-home:hover {
  color: #fff;
  border-color: rgba(240, 230, 216, 0.45);
  background: rgba(185, 58, 47, 0.25);
}

.hint {
  margin: 0 0 1.25rem;
  font-size: 0.9rem;
  color: rgba(232, 226, 216, 0.65);
  line-height: 1.6;
}

.table-card {
  background: linear-gradient(145deg, #f7efe6 0%, #ebe2d6 100%) !important;
  border-radius: 14px !important;
  box-shadow:
    0 4px 0 rgba(42, 34, 28, 0.12),
    0 24px 48px rgba(0, 0, 0, 0.35),
    inset 0 1px 0 rgba(255, 255, 255, 0.65);
  border: 1px solid rgba(58, 48, 40, 0.15) !important;
}

.table-card :deep(.ant-card-head-title) {
  padding: 12px 0;
}

.card-title {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  font-weight: 600;
  color: #5c4033;
  letter-spacing: 0.06em;
}

.table-card :deep(.ant-table) {
  color: #2a221c;
}

.table-card :deep(.ant-pagination) {
  margin-bottom: 0;
}
</style>
