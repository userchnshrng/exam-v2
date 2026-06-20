<template>
  <div class="score-query">
    <!-- 成绩列表 -->
    <el-card v-if="!showDetail" shadow="never" class="table-card">
      <el-table :data="tableData" v-loading="loading" stripe border style="width:100%">
        <el-table-column prop="subject" label="科目" width="140" />
        <el-table-column prop="etScore" label="得分" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="row.etScore >= 60 ? 'success' : 'danger'">{{ row.etScore ?? '-' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="answerDate" label="答题日期" width="120" align="center" />
        <el-table-column label="操作" width="120" align="center">
          <template #default="{ row }">
            <el-button type="primary" size="small" link @click="showAnswers(row)">
              考试详情
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

    <!-- 考试详情 -->
    <div v-else class="detail-view">
      <div class="detail-header">
        <el-button @click="backToList" type="default" size="small">← 返回列表</el-button>
        <span class="detail-title">{{ detailSubject }} — 答题详情</span>
        <div class="detail-summary">
          <el-tag type="success" size="small">✓ {{ correctCount }} 正确</el-tag>
          <el-tag type="danger" size="small">✗ {{ wrongCount }} 错误</el-tag>
          <el-tag type="info" size="small">共 {{ answerData.length }} 题</el-tag>
        </div>
      </div>

      <el-card shadow="never" class="table-card">
        <el-table :data="answerData" v-loading="detailLoading" border
                  style="width:100%" empty-text="暂无答题记录"
                  :row-class-name="answerRowClass">
          <el-table-column label="题目" min-width="300">
            <template #default="{ row }">
              <div class="question-cell">
                <div class="question-head">
                  <el-tag :type="questionTagType(row.questionType)" size="small" effect="plain">
                    {{ questionTypeLabel(row.questionType) }}
                  </el-tag>
                  <span class="question-num">#{{ row.questionId }}</span>
                </div>
                <p class="question-text">{{ row.questionContent || '(题目内容未加载)' }}</p>
                <div v-if="row.optionA" class="question-options">
                  <span v-for="opt in [row.optionA, row.optionB, row.optionC, row.optionD]"
                        :key="opt" class="option-item"
                        :class="{
                          'option-chosen': opt && row.studentAnswer && opt.startsWith(row.studentAnswer + '.'),
                          'option-correct': opt && row.correctAnswer && opt.startsWith(row.correctAnswer + '.')
                        }">{{ opt }}</span>
                </div>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="我的答案" width="100" align="center">
            <template #default="{ row }">
              <span :class="row.isCorrect ? 'answer-ok' : 'answer-bad'">{{ row.studentAnswer || '未作答' }}</span>
            </template>
          </el-table-column>
          <el-table-column label="正确答案" width="100" align="center">
            <template #default="{ row }">
              <span class="correct-answer-text">{{ row.correctAnswer }}</span>
            </template>
          </el-table-column>
          <el-table-column label="判题" width="80" align="center">
            <template #default="{ row }">
              <el-tag :type="row.isCorrect ? 'success' : 'danger'" size="small" effect="dark">
                {{ row.isCorrect ? '✓ 正确' : '✗ 错误' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="score" label="得分" width="70" align="center" />
        </el-table>
      </el-card>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'
import { listScores, type ScoreRecord } from '@/api/score'
import { getExamAnswers, type ExamAnswerRecord } from '@/api/examAnswer'

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
const answerData = ref<ExamAnswerRecord[]>([])

const correctCount = computed(() => answerData.value.filter(a => a.isCorrect).length)
const wrongCount = computed(() => answerData.value.filter(a => !a.isCorrect).length)

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
    case 1: return 'primary'
    case 2: return 'success'
    case 3: return 'warning'
    default: return 'info'
  }
}

function answerRowClass({ row }: { row: ExamAnswerRecord }): string {
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
      answerData.value = res.data.data ?? []
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

onMounted(() => fetchList())
</script>

<style scoped>
.score-query { display: flex; flex-direction: column; gap: 16px; }
.table-card { border-radius: 8px; }
.table-pagination { display: flex; justify-content: flex-end; margin-top: 16px; }

/* ===== 详情视图 ===== */
.detail-view { display: flex; flex-direction: column; gap: 16px; }
.detail-header {
  display: flex;
  align-items: center;
  gap: 16px;
  flex-wrap: wrap;
}
.detail-title {
  font-size: 16px;
  font-weight: 600;
  color: var(--el-text-color-primary);
}
.detail-summary {
  display: flex;
  gap: 8px;
  margin-left: auto;
}

/* 题目单元格 */
.question-cell {
  text-align: left;
  padding: 4px 0;
}
.question-head {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 6px;
}
.question-num {
  font-size: 12px;
  color: var(--color-text-muted);
  font-weight: 600;
}
.question-text {
  margin: 0 0 6px 0;
  line-height: 1.7;
  font-size: 14px;
  color: var(--color-text-title);
}
.question-options {
  display: flex;
  flex-wrap: wrap;
  gap: 6px 16px;
}
.option-item {
  font-size: 13px;
  color: var(--color-text-body);
  padding: 2px 8px;
  border-radius: 4px;
}
.option-chosen {
  background: #fef2f2;
  color: #dc2626;
  font-weight: 600;
}
.option-correct {
  background: #f0fdf4;
  color: #059669;
  font-weight: 600;
}

/* 答案文字 */
.answer-ok { color: #059669; font-weight: 600; }
.answer-bad { color: #dc2626; font-weight: 600; }
.correct-answer-text { font-weight: 600; color: #059669; }

/* 行高亮 */
:deep(.detail-row-correct) { --el-table-tr-bg: #f6fef9; }
:deep(.detail-row-wrong) { --el-table-tr-bg: #fef6f6; }
</style>
