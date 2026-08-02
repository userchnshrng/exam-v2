<template>
  <div class="score-query">
    <!-- ==================== 成绩列表 ==================== -->
    <el-card v-if="!showDetail" class="list-card" shadow="never">
      <template #header>
        <span class="card-header-title">📊 我的成绩</span>
      </template>

      <el-table :data="tableData" v-loading="loading" stripe style="width:100%">
        <el-table-column prop="subject" label="科目" width="160" />
        <el-table-column prop="etScore" label="得分" width="90" align="center">
          <template #default="{ row }">
            <el-tag :type="row.etScore >= 60 ? 'success' : 'danger'" effect="light" round>
              {{ row.etScore ?? '-' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="answerDate" label="答题日期" width="130" align="center" />
        <el-table-column label="操作" width="120" align="center">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="showAnswers(row)">
              考试详情 →
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="table-pagination">
        <el-pagination
            v-model:current-page="page" v-model:page-size="size"
            :total="total" :page-sizes="[5, 10, 20]"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="fetchList" @current-change="fetchList"
        />
      </div>
    </el-card>

    <!-- ==================== 考试详情 ==================== -->
    <div v-else class="detail-view">
      <!-- 概览卡片 -->
      <div class="overview-row">
        <el-button class="back-btn" @click="backToList" text>
          ← 返回列表
        </el-button>
        <span class="detail-subject">{{ detailSubject }}</span>
      </div>

      <div class="stat-cards">
        <div class="stat-card stat-total">
          <div class="stat-card__value">{{ answerData.length }}</div>
          <div class="stat-card__label">总题数</div>
        </div>
        <div class="stat-card stat-correct">
          <div class="stat-card__value">{{ correctCount }}</div>
          <div class="stat-card__label">✓ 正确</div>
        </div>
        <div class="stat-card stat-wrong">
          <div class="stat-card__value">{{ wrongCount }}</div>
          <div class="stat-card__label">✗ 错误</div>
        </div>
        <div class="stat-card stat-rate">
          <div class="stat-card__value">{{ accuracy }}%</div>
          <div class="stat-card__label">正确率</div>
        </div>
      </div>

      <!-- 答题表格 -->
      <el-card class="detail-table-card" shadow="never">
        <el-table :data="answerData" v-loading="detailLoading"
                  style="width:100%" empty-text="暂无答题记录"
                  :row-class-name="answerRowClass">
          <el-table-column label="题目" min-width="300">
            <template #default="{ row }">
              <div class="question-cell">
                <div class="question-head">
                  <el-tag :type="questionTagType(row.questionType)" size="small" effect="plain" round>
                    {{ questionTypeLabel(row.questionType) }}
                  </el-tag>
                </div>
                <p class="question-text">{{ row.questionContent || '(题目内容未加载)' }}</p>
                <div v-if="row.optionA" class="question-options">
                  <span v-for="opt in [row.optionA, row.optionB, row.optionC, row.optionD]"
                        :key="opt" class="option-item"
                        :class="{
                          'option-chosen': opt && row.studentAnswer && opt.startsWith(row.studentAnswer + '.'),
                          'option-correct': opt && row.correctAnswer && opt.startsWith(row.correctAnswer + '.')
                        }">
                    {{ opt }}
                  </span>
                </div>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="我的答案" width="105" align="center">
            <template #default="{ row }">
              <span :class="row.isCorrect ? 'answer-ok' : 'answer-bad'">{{ row.studentAnswer || '未作答' }}</span>
            </template>
          </el-table-column>
          <el-table-column label="正确答案" width="105" align="center">
            <template #default="{ row }">
              <span class="correct-answer-text">{{ row.correctAnswer }}</span>
            </template>
          </el-table-column>
          <el-table-column label="判题" width="85" align="center">
            <template #default="{ row }">
              <el-tag :type="row.isCorrect ? 'success' : 'danger'" size="small" effect="plain" round>
                {{ row.isCorrect ? '✓ 正确' : '✗ 错误' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="score" label="得分" width="70" align="center" />
          <el-table-column label="操作" width="130" align="center" fixed="right">
            <template #default="{ row }">
              <el-button
                  type="warning" size="small" plain
                  :icon="MagicStick"
                  :loading="row._aiLoading"
                  @click="requestAiAnalysis(row)"
              >
                AI 解析
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-card>

      <!-- ==================== AI 解析弹窗 ==================== -->
      <el-dialog
          v-model="aiDialogVisible"
          title="🤖 AI 智能解析"
          :width="dialogWidth"
          :close-on-click-modal="false"
          destroy-on-close
          draggable
          class="ai-dialog"
      >
        <div class="ai-dialog-body" v-loading="aiDialogLoading">
          <template v-if="aiDialogLoading">
            <el-skeleton :rows="6" animated />
          </template>
          <template v-else-if="aiDialogContent">
            <div class="ai-dialog-content" v-html="renderMarkdown(aiDialogContent)"></div>
          </template>
        </div>
        <!-- 缩放拖拽手柄 -->
        <div class="resize-handle" @mousedown.left="startResize">
          <span></span><span></span><span></span>
        </div>
        <template #footer>
          <el-button @click="aiDialogVisible = false">关闭</el-button>
        </template>
      </el-dialog>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { ElMessage } from 'element-plus'
import { MagicStick } from '@element-plus/icons-vue'
import { marked } from 'marked'
import { useUserStore } from '@/stores/user'
import { listScores, type ScoreRecord } from '@/api/score'
import { getExamAnswers, getAiAnalysis, type ExamAnswerRecord } from '@/api/examAnswer'

interface AnswerRow extends ExamAnswerRecord {
  _aiLoading?: boolean
  _aiAnalysis?: string
}

const userStore = useUserStore()

// —— 列表状态 ——
const loading = ref(false)
const tableData = ref<ScoreRecord[]>([])
const total = ref(0)
const page = ref(1)
const size = ref(10)

// —— 详情状态 ——
const showDetail = ref(false)
const detailLoading = ref(false)
const detailSubject = ref('')
const answerData = ref<AnswerRow[]>([])

const correctCount = computed(() => answerData.value.filter(a => a.isCorrect).length)
const wrongCount = computed(() => answerData.value.filter(a => !a.isCorrect).length)
const accuracy = computed(() => {
  if (answerData.value.length === 0) return 0
  return Math.round((correctCount.value / answerData.value.length) * 100)
})

function questionTypeLabel(type: number): string {
  switch (type) {
    case 1: return '选择题'
    case 2: return '填空题'
    case 3: return '判断题'
    default: return `类型${type}`
  }
}

function questionTagType(type: number): string {
  switch (type) {
    case 1: return ''
    case 2: return 'success'
    case 3: return 'warning'
    default: return 'info'
  }
}

function answerRowClass({ row }: { row: AnswerRow }): string {
  return row.isCorrect ? 'detail-row-correct' : 'detail-row-wrong'
}

async function fetchList() {
  loading.value = true
  try {
    const studentId = userStore.userId
    if (!studentId) {
      ElMessage.error('无法获取学生信息，请重新登录')
      loading.value = false
      return
    }
    const res = await listScores(studentId, page.value, size.value)
    if (res.data.code === 0 && res.data.data) {
      tableData.value = res.data.data.records
      total.value = res.data.data.total
    }
  } finally {
    loading.value = false
  }
}

async function showAnswers(row: ScoreRecord) {
  showDetail.value = true
  detailSubject.value = row.subject
  detailLoading.value = true
  try {
    const res = await getExamAnswers(row.examCode)
    if (res.data.code === 0) {
      answerData.value = (res.data.data ?? []).map(a => ({
        ...a,
        _aiLoading: false,
        _aiAnalysis: undefined
      }))
    } else {
      ElMessage.error(res.data.message || '获取答题详情失败')
    }
  } catch {
    ElMessage.error('获取答题详情失败')
  } finally {
    detailLoading.value = false
  }
}

function backToList() {
  showDetail.value = false
  answerData.value = []
  detailSubject.value = ''
}

// —— AI 解析 ——
const aiDialogVisible = ref(false)
const aiDialogLoading = ref(false)
const aiDialogContent = ref('')
const dialogWidth = ref('720px')

// —— 拖拽缩放 ——
let resizeCleanup: (() => void) | null = null

function startResize(e: MouseEvent) {
  const dialogEl = (e.target as HTMLElement).closest('.el-dialog') as HTMLElement | null
  if (!dialogEl) return

  const startX = e.clientX
  const startY = e.clientY
  const startW = dialogEl.offsetWidth
  const startH = dialogEl.offsetHeight

  document.body.style.cursor = 'nwse-resize'
  document.body.style.userSelect = 'none'

  const onMove = (ev: MouseEvent) => {
    const newW = Math.max(520, Math.min(window.innerWidth - 40, startW + ev.clientX - startX))
    const newH = Math.max(320, Math.min(window.innerHeight - 60, startH + ev.clientY - startY))
    dialogWidth.value = `${newW}px`
    dialogEl.style.setProperty('--el-dialog-height', `${newH}px`)
  }

  const onUp = () => {
    document.removeEventListener('mousemove', onMove)
    document.removeEventListener('mouseup', onUp)
    document.body.style.cursor = ''
    document.body.style.userSelect = ''
    resizeCleanup = null
  }

  document.addEventListener('mousemove', onMove)
  document.addEventListener('mouseup', onUp)
  resizeCleanup = onUp
  e.preventDefault()
}

onUnmounted(() => {
  resizeCleanup?.()
  document.body.style.cursor = ''
  document.body.style.userSelect = ''
})

function renderMarkdown(text: string): string {
  return marked.parse(text, { breaks: true, gfm: true }) as string
}

async function requestAiAnalysis(row: AnswerRow) {
  // 如果已有缓存，直接用缓存
  if (row._aiAnalysis) {
    aiDialogContent.value = row._aiAnalysis
    aiDialogVisible.value = true
    return
  }

  aiDialogVisible.value = true
  aiDialogLoading.value = true
  aiDialogContent.value = ''
  row._aiLoading = true

  try {
    const res = await getAiAnalysis(row.answerId)
    if (res.data.code === 0 && res.data.data) {
      row._aiAnalysis = res.data.data.analysis
      aiDialogContent.value = row._aiAnalysis!
    } else {
      ElMessage.error(res.data.message || '获取 AI 解析失败')
      aiDialogVisible.value = false
    }
  } catch {
    ElMessage.error('AI 解析请求失败，请稍后重试')
    aiDialogVisible.value = false
  } finally {
    aiDialogLoading.value = false
    row._aiLoading = false
  }
}

onMounted(() => fetchList())
</script>

<style scoped>
/* ============================================
   基础布局
   ============================================ */
.score-query {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

/* —— 列表卡片 —— */
.list-card {
  border-radius: 12px;
  border: none;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
}
.list-card :deep(.el-card__header) {
  padding: 16px 20px;
  border-bottom: 1px solid #f0f0f0;
}
.card-header-title {
  font-size: 16px;
  font-weight: 600;
  color: #2c3e50;
}

.table-pagination {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
}

/* ============================================
   详情视图
   ============================================ */
.detail-view {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.overview-row {
  display: flex;
  align-items: center;
  gap: 12px;
}
.back-btn {
  font-size: 14px;
  color: #7c8db5;
}
.detail-subject {
  font-size: 18px;
  font-weight: 700;
  color: #1e293b;
}

/* —— 概览大数字卡片 —— */
.stat-cards {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
}
.stat-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 24px 16px;
  border-radius: 14px;
  box-shadow: 0 2px 16px rgba(0, 0, 0, 0.05);
  transition: transform 0.2s;
}
.stat-card:hover {
  transform: translateY(-2px);
}
.stat-card__value {
  font-size: 36px;
  font-weight: 800;
  line-height: 1.2;
  letter-spacing: -1px;
}
.stat-card__label {
  font-size: 13px;
  font-weight: 500;
  margin-top: 4px;
  opacity: 0.8;
}

.stat-total {
  background: linear-gradient(135deg, #eff6ff, #dbeafe);
  color: #3b82f6;
}
.stat-correct {
  background: linear-gradient(135deg, #edf7ee, #d4edda);
  color: #4caf50;
}
.stat-wrong {
  background: linear-gradient(135deg, #fef0ef, #fde2e2);
  color: #e57373;
}
.stat-rate {
  background: linear-gradient(135deg, #f5f3ff, #ede9fe);
  color: #8b5cf6;
}

/* —— 详情表格卡片 —— */
.detail-table-card {
  border-radius: 12px;
  border: none;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
}

/* ============================================
   AI 解析弹窗
   ============================================ */
/* 对话框高度跟随缩放 */
.ai-dialog :deep(.el-dialog) {
  height: var(--el-dialog-height, auto);
  display: flex;
  flex-direction: column;
}
.ai-dialog :deep(.el-dialog__body) {
  flex: 1;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  position: relative;
}
.ai-dialog-body {
  flex: 1;
  min-height: 120px;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}
.ai-dialog-content {
  font-size: 15px;
  line-height: 2;
  color: #374151;
  word-break: break-word;
  overflow-wrap: break-word;
  flex: 1;
  overflow-y: auto;
  padding: 4px 0;
}

/* —— 缩放手柄 —— */
.resize-handle {
  position: absolute;
  right: 8px;
  bottom: 8px;
  width: 22px;
  height: 22px;
  cursor: nwse-resize;
  z-index: 100;
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  justify-content: flex-end;
  gap: 3px;
  padding: 3px;
  opacity: 0.35;
  transition: opacity 0.2s;
  border-radius: 2px;
}
.resize-handle:hover {
  opacity: 0.8;
}
.resize-handle:active {
  opacity: 1;
}
.resize-handle span {
  display: block;
  height: 1.5px;
  background: #6b7280;
  border-radius: 1px;
}
.resize-handle span:nth-child(1) { width: 7px; }
.resize-handle span:nth-child(2) { width: 12px; }
.resize-handle span:nth-child(3) { width: 17px; }

/* Markdown 渲染元素样式 */
.ai-dialog-content :deep(p) {
  margin: 0 0 12px 0;
}
.ai-dialog-content :deep(strong) {
  font-weight: 700;
  color: #1e293b;
}
.ai-dialog-content :deep(em) {
  font-style: italic;
  color: #64748b;
}
.ai-dialog-content :deep(ul),
.ai-dialog-content :deep(ol) {
  margin: 8px 0 12px 0;
  padding-left: 24px;
}
.ai-dialog-content :deep(li) {
  margin-bottom: 4px;
}
.ai-dialog-content :deep(code) {
  background: #f1f5f9;
  padding: 2px 6px;
  border-radius: 4px;
  font-size: 13px;
  font-family: 'JetBrains Mono', 'Fira Code', monospace;
  color: #e11d48;
}
.ai-dialog-content :deep(pre) {
  background: #1e293b;
  color: #e2e8f0;
  padding: 12px 16px;
  border-radius: 8px;
  overflow-x: auto;
  margin: 8px 0 12px 0;
  font-size: 13px;
  line-height: 1.6;
}
.ai-dialog-content :deep(pre code) {
  background: none;
  color: inherit;
  padding: 0;
  font-size: inherit;
}
.ai-dialog-content :deep(blockquote) {
  border-left: 3px solid #93c5fd;
  padding: 4px 16px;
  margin: 16px 0;
  color: #64748b;
  background: #f8fafc;
  border-radius: 0 6px 6px 0;
}
.ai-dialog-content :deep(h1),
.ai-dialog-content :deep(h2),
.ai-dialog-content :deep(h3),
.ai-dialog-content :deep(h4) {
  margin: 16px 0 8px 0;
  color: #1e293b;
  line-height: 1.4;
}
.ai-dialog-content :deep(h2) { font-size: 17px; }
.ai-dialog-content :deep(h3) { font-size: 16px; }
.ai-dialog-content :deep(hr) {
  border: none;
  border-top: 1px solid #e2e8f0;
  margin: 16px 0;
}

/* ============================================
   题目单元格
   ============================================ */
.question-cell {
  text-align: left;
  padding: 4px 0;
}
.question-head {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
}
.question-text {
  margin: 0 0 8px 0;
  line-height: 1.8;
  font-size: 14px;
  color: #1e293b;
}
.question-options {
  display: flex;
  flex-wrap: wrap;
  gap: 8px 20px;
}
.option-item {
  position: relative;
  font-size: 13px;
  color: #64748b;
  padding: 6px 12px;
  border-radius: 8px;
  border: 1.5px solid transparent;
  transition: all 0.2s;
}

/* 选中的选项 — 莫兰迪淡红 */
.option-chosen {
  background: #fef0f0;
  border-color: #f4c2c2;
  color: #b45353;
  font-weight: 600;
}

/* 正确的选项 — 莫兰迪淡绿 */
.option-correct {
  background: #f0faf4;
  border-color: #b7d9c2;
  color: #3d8b5e;
  font-weight: 600;
}

/* 同时选中且正确 */
.option-chosen.option-correct {
  background: #dcfce7;
  border-color: #86dba8;
  color: #16a34a;
}

/* 答案文字 */
.answer-ok {
  color: #4caf50;
  font-weight: 700;
  font-size: 14px;
}
.answer-bad {
  color: #e57373;
  font-weight: 700;
  font-size: 14px;
}
.correct-answer-text {
  font-weight: 700;
  font-size: 14px;
  color: #4caf50;
}

/* ============================================
   行背景 — 莫兰迪淡色系
   ============================================ */
:deep(.detail-row-correct) {
  --el-table-tr-bg: #f6fbf8;
}
:deep(.detail-row-correct:hover) {
  --el-table-tr-bg: #ecf7f0;
}
:deep(.detail-row-wrong) {
  --el-table-tr-bg: #fdf7f7;
}
:deep(.detail-row-wrong:hover) {
  --el-table-tr-bg: #fbeeee;
}

/* 响应式 */
@media (max-width: 768px) {
  .stat-cards {
    grid-template-columns: repeat(2, 1fr);
  }
}
</style>
