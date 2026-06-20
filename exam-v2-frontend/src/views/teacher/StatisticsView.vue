<template>
  <div class="statistics">
    <el-row :gutter="16">
      <!-- ====== 成绩分布饼图 ====== -->
      <el-col :span="12">
        <el-card shadow="never" class="chart-card">
          <template #header>
            <div class="card-header-row">
              <span>成绩分布</span>
              <el-select v-model="selectedPieExam" placeholder="全部考试" clearable
                         @change="onPieExamChange" style="width:220px" size="small">
                <el-option v-for="e in examOptions" :key="e.examCode"
                           :label="e.examName" :value="e.examCode" />
              </el-select>
            </div>
          </template>
          <v-chart :option="pieOption" style="height:350px" v-if="pieOption" />
          <el-empty v-else description="暂无数据" :image-size="80" />
          <div class="chart-summary">
            <el-tag>总人次：{{ distribution?.totalCount || 0 }}</el-tag>
            <el-tag type="success">平均分：{{ distribution?.average || 0 }}</el-tag>
          </div>
        </el-card>
      </el-col>

      <!-- ====== 各考试对比柱状图 ====== -->
      <el-col :span="12">
        <el-card shadow="never" class="chart-card">
          <template #header><span>各考试平均分对比</span></template>
          <v-chart :option="barOption" style="height:350px" v-if="barOption" />
          <el-empty v-else description="暂无数据" :image-size="80" />
        </el-card>
      </el-col>
    </el-row>

    <!-- ====== 按考试查学生成绩 ====== -->
    <el-card shadow="never" class="chart-card" style="margin-top:16px">
      <template #header>
        <span>学生成绩明细</span>
        <el-select v-model="selectedExam" placeholder="选择考试" @change="loadStudentScores"
                   style="width:280px;margin-left:16px" clearable>
          <el-option v-for="e in examOptions" :key="e.examCode"
                     :label="e.examName" :value="e.examCode" />
        </el-select>
      </template>
      <el-table :data="studentScores" stripe border v-loading="studentLoading" max-height="400">
        <el-table-column prop="studentName" label="姓名" width="100" align="center" />
        <el-table-column prop="studentId" label="学号" width="100" align="center" />
        <el-table-column prop="subject" label="科目" width="150" />
        <el-table-column prop="score" label="得分" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.score >= 60 ? 'success' : 'danger'">{{ row.score }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="date" label="日期" width="120" align="center" />
        <el-table-column label="操作" width="240" align="center" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" link @click="viewAnswers(row)">
              查看详情
            </el-button>
            <el-button type="warning" size="small" link @click="openEdit(row)">
              修改
            </el-button>
            <el-popconfirm title="确定删除该学生的本场考试记录？此操作不可恢复" @confirm="deleteRecord(row)">
              <template #reference>
                <el-button type="danger" size="small" link>删除</el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- ====== 答题详情对话框 ====== -->
    <el-dialog v-model="detailVisible" width="1000px" destroy-on-close>
      <template #header>
        <div class="detail-dialog-header">
          <span class="detail-dialog-title">答题详情</span>
          <div class="detail-dialog-meta">
            <el-tag type="info" size="small">{{ detailStudentName }}</el-tag>
            <span class="meta-sep">·</span>
            <el-tag type="info" size="small">{{ detailSubject }}</el-tag>
          </div>
        </div>
      </template>
      <el-table :data="detailAnswers" v-loading="detailLoading" border
                max-height="480" empty-text="暂无答题记录"
                :row-class-name="detailRowClass">
        <el-table-column label="题目" min-width="280">
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
        <el-table-column label="学生答案" width="100" align="center">
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
    </el-dialog>

    <!-- ====== 修改答题对话框 ====== -->
    <el-dialog v-model="editVisible" width="1000px" destroy-on-close @close="clearEdit" class="edit-answers-dialog">
      <template #header>
        <div class="edit-dialog-header">
          <span class="edit-dialog-title">修改答题</span>
          <div class="edit-dialog-meta">
            <el-tag type="info" size="small">{{ editStudentName }}</el-tag>
            <span class="meta-sep">·</span>
            <el-tag type="info" size="small">{{ editSubject }}</el-tag>
          </div>
        </div>
      </template>

      <el-table :data="editAnswers" v-loading="editLoading" stripe border
                max-height="480" empty-text="暂无答题记录"
                :row-class-name="editRowClass">
        <el-table-column label="题型" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="questionTagType(row.questionType)" size="small" effect="plain">
              {{ questionTypeLabel(row.questionType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="questionId" label="题号" width="60" align="center" />
        <el-table-column label="学生答案" min-width="130" align="center">
          <template #default="{ row }">
            <el-input v-model="row.studentAnswer" size="small"
                      :class="{ 'is-wrong-input': !row.isCorrect && row.studentAnswer }" />
          </template>
        </el-table-column>
        <el-table-column prop="correctAnswer" label="正确答案" min-width="130" align="center">
          <template #default="{ row }">
            <span class="correct-answer-text">{{ row.correctAnswer }}</span>
          </template>
        </el-table-column>
        <el-table-column label="判题" width="100" align="center">
          <template #default="{ row }">
            <div class="judge-toggle">
              <span :class="['judge-label', row.isCorrect ? 'judge-right' : 'judge-wrong']">
                {{ row.isCorrect ? '✓ 正确' : '✗ 错误' }}
              </span>
              <el-switch v-model="row.isCorrect" size="small"
                         :active-value="true" :inactive-value="false" />
            </div>
          </template>
        </el-table-column>
        <el-table-column label="得分" width="90" align="center">
          <template #default="{ row }">
            <el-input-number v-model="row.score" :min="0" :max="100" size="small"
                             controls-position="right" style="width:85px" />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="70" align="center" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" link @click="saveAnswer(row)">保存</el-button>
          </template>
        </el-table-column>
      </el-table>

      <template #footer>
        <div class="edit-dialog-footer">
          <span class="edit-footer-hint">修改后请逐行点击"保存"</span>
          <el-button @click="editVisible = false">关闭</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import VChart from 'vue-echarts'
import { use } from 'echarts/core'
import { PieChart, BarChart } from 'echarts/charts'
import { TitleComponent, TooltipComponent, LegendComponent, GridComponent } from 'echarts/components'
import { CanvasRenderer } from 'echarts/renderers'
import {
  getScoreDistribution, getExamComparison, getExamStudentScores, getExamOptions,
  type ScoreDistribution, type ExamComparisonItem, type StudentScoreItem, type ExamOption
} from '@/api/statistics'
import {
  getStudentExamAnswers, updateExamAnswer, resetExamRecord,
  type ExamAnswerRecord
} from '@/api/examAnswer'

use([PieChart, BarChart, TitleComponent, TooltipComponent, LegendComponent, GridComponent, CanvasRenderer])

const distribution = ref<ScoreDistribution | null>(null)
const examComparison = ref<ExamComparisonItem[]>([])
const studentScores = ref<StudentScoreItem[]>([])
const examOptions = ref<ExamOption[]>([])
const selectedExam = ref<number | null>(null)
const selectedPieExam = ref<number | null>(null)
const studentLoading = ref(false)

const pieOption = ref<any>(null)
const barOption = ref<any>(null)

// ===== 详情对话框 =====
const detailVisible = ref(false)
const detailLoading = ref(false)
const detailStudentName = ref('')
const detailSubject = ref('')
const detailAnswers = ref<ExamAnswerRecord[]>([])

// ===== 修改对话框 =====
const editVisible = ref(false)
const editLoading = ref(false)
const editStudentName = ref('')
const editSubject = ref('')
const editAnswers = ref<ExamAnswerRecord[]>([])
let editExamCode = 0
let editStudentId = 0

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

function detailRowClass({ row }: { row: ExamAnswerRecord }): string {
  return row.isCorrect ? 'detail-row-correct' : 'detail-row-wrong'
}

function editRowClass({ row }: { row: ExamAnswerRecord }): string {
  return row.isCorrect ? 'edit-row-correct' : 'edit-row-wrong'
}

async function loadDistribution(examCode?: number) {
  try {
    const res = await getScoreDistribution(examCode)
    if (res.data.code === 0) {
      distribution.value = res.data.data
      const d = res.data.data.distribution
      if (!d || d.length === 0) {
        pieOption.value = null
        return
      }
      pieOption.value = {
        tooltip: { trigger: 'item' },
        legend: { bottom: 0 },
        series: [{
          type: 'pie', radius: ['30%', '60%'], center: ['50%', '45%'],
          data: d, label: { formatter: '{b}\n{c}人' },
          itemStyle: { borderRadius: 4 }
        }]
      }
    }
  } catch { /* ignore */ }
}

function onPieExamChange() {
  loadDistribution(selectedPieExam.value ?? undefined)
}

async function loadExamComparison() {
  try {
    const res = await getExamComparison()
    if (res.data.code === 0) {
      examComparison.value = res.data.data || []
      if (examComparison.value.length > 0) {
        barOption.value = {
          tooltip: {
            trigger: 'axis',
            axisPointer: { type: 'shadow' }
          },
          legend: {
            data: ['平均分', '最高分', '最低分'],
            top: 0,
            right: 0,
            orient: 'horizontal',
            textStyle: { fontSize: 12 }
          },
          grid: {
            left: 40,
            right: 20,
            top: 35,
            bottom: 70
          },
          xAxis: {
            type: 'category',
            data: examComparison.value.map(e => e.examName),
            axisLabel: {
              rotate: 20,
              fontSize: 11,
              interval: 0,
              overflow: 'truncate',
              width: 80
            },
            axisTick: { alignWithLabel: true }
          },
          yAxis: {
            type: 'value',
            max: 100,
            minInterval: 1
          },
          series: [
            {
              name: '平均分', type: 'bar',
              data: examComparison.value.map(e => e.avgScore),
              barMaxWidth: 28,
              itemStyle: { color: '#3370ff', borderRadius: [4, 4, 0, 0] }
            },
            {
              name: '最高分', type: 'bar',
              data: examComparison.value.map(e => e.maxScore),
              barMaxWidth: 28,
              itemStyle: { color: '#00b42a', borderRadius: [4, 4, 0, 0] }
            },
            {
              name: '最低分', type: 'bar',
              data: examComparison.value.map(e => e.minScore),
              barMaxWidth: 28,
              itemStyle: { color: '#f53f3f', borderRadius: [4, 4, 0, 0] }
            }
          ]
        }
      }
    }
  } catch { /* ignore */ }
}

async function loadExamOptions() {
  try {
    const res = await getExamOptions()
    if (res.data.code === 0) examOptions.value = res.data.data || []
  } catch { /* ignore */ }
}

async function loadStudentScores() {
  if (!selectedExam.value) { studentScores.value = []; return }
  studentLoading.value = true
  try {
    const res = await getExamStudentScores(selectedExam.value)
    if (res.data.code === 0) studentScores.value = res.data.data || []
  } finally { studentLoading.value = false }
}

// ===== 查看答题详情 =====
async function viewAnswers(row: StudentScoreItem) {
  detailVisible.value = true
  detailStudentName.value = row.studentName
  detailSubject.value = row.subject
  detailLoading.value = true
  try {
    const res = await getStudentExamAnswers(row.examCode, row.studentId)
    if (res.data.code === 0) {
      detailAnswers.value = res.data.data ?? []
    } else {
      ElMessage.error(res.data.message || '获取答题详情失败')
    }
  } catch {
    ElMessage.error('获取答题详情失败')
  } finally {
    detailLoading.value = false
  }
}

// ===== 打开修改对话框 =====
async function openEdit(row: StudentScoreItem) {
  editVisible.value = true
  editStudentName.value = row.studentName
  editSubject.value = row.subject
  editExamCode = row.examCode
  editStudentId = row.studentId
  editLoading.value = true
  try {
    const res = await getStudentExamAnswers(row.examCode, row.studentId)
    if (res.data.code === 0) {
      // 深拷贝避免直接修改原数据
      editAnswers.value = (res.data.data ?? []).map(a => ({ ...a }))
    } else {
      ElMessage.error(res.data.message || '获取答题详情失败')
    }
  } catch {
    ElMessage.error('获取答题详情失败')
  } finally {
    editLoading.value = false
  }
}

function clearEdit() {
  editAnswers.value = []
  editStudentName.value = ''
  editSubject.value = ''
  editExamCode = 0
  editStudentId = 0
}

// ===== 保存单条答题修改 =====
async function saveAnswer(row: ExamAnswerRecord) {
  try {
    const res = await updateExamAnswer(row.answerId, {
      studentAnswer: row.studentAnswer,
      isCorrect: row.isCorrect,
      score: row.score
    })
    if (res.data.code === 0) {
      ElMessage.success('保存成功')
    } else {
      ElMessage.error(res.data.message || '保存失败')
    }
  } catch {
    ElMessage.error('保存失败')
  }
}

// ===== 删除考试记录 =====
async function deleteRecord(row: StudentScoreItem) {
  try {
    const res = await resetExamRecord(row.examCode, row.studentId)
    if (res.data.code === 0) {
      ElMessage.success('已删除该学生的考试记录')
      // 刷新列表
      await loadStudentScores()
    } else {
      ElMessage.error(res.data.message || '删除失败')
    }
  } catch {
    ElMessage.error('删除失败')
  }
}

onMounted(() => {
  loadDistribution()
  loadExamComparison()
  loadExamOptions()
})
</script>

<style scoped>
.statistics { display: flex; flex-direction: column; }
.chart-card { border-radius: 8px; }
.card-header-row { display: flex; align-items: center; justify-content: space-between; width: 100%; }
.chart-summary { display: flex; gap: 8px; justify-content: center; margin-top: 8px; }

/* ===== 对话框公用的表头样式 ===== */
.detail-dialog-header,
.edit-dialog-header {
  display: flex;
  align-items: center;
  gap: 16px;
}
.detail-dialog-title,
.edit-dialog-title {
  font-size: 16px;
  font-weight: 700;
  color: var(--color-text-title);
}
.detail-dialog-meta,
.edit-dialog-meta {
  display: flex;
  align-items: center;
  gap: 6px;
}
.meta-sep {
  color: var(--color-text-muted);
  font-size: 12px;
}

/* ===== 题目单元格（详情 & 修改共用） ===== */
.question-cell {
  text-align: left;
  padding: 2px 0;
}
.question-head {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 4px;
}
.question-num {
  font-size: 12px;
  color: var(--color-text-muted);
  font-weight: 600;
}
.question-text {
  margin: 0 0 4px 0;
  line-height: 1.6;
  font-size: 13px;
  color: var(--color-text-title);
}
.question-options {
  display: flex;
  flex-wrap: wrap;
  gap: 4px 12px;
}
.option-item {
  font-size: 12px;
  color: var(--color-text-body);
  padding: 1px 6px;
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
:deep(.detail-row-correct),
:deep(.edit-row-correct) {
  --el-table-tr-bg: #f6fef9;
}
:deep(.detail-row-wrong),
:deep(.edit-row-wrong) {
  --el-table-tr-bg: #fef6f6;
}

/* ===== 修改对话框特有 ===== */
/* 判题开关 */
.judge-toggle {
  display: flex;
  align-items: center;
  gap: 6px;
  justify-content: center;
}
.judge-label {
  font-size: 12px;
  font-weight: 600;
  white-space: nowrap;
}
.judge-right { color: #059669; }
.judge-wrong { color: #dc2626; }

/* 错误答案输入框高亮 */
:deep(.is-wrong-input .el-input__wrapper) {
  box-shadow: 0 0 0 1px #fca5a5 inset !important;
}

/* 底部 */
.edit-dialog-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  width: 100%;
}
.edit-footer-hint {
  font-size: 12px;
  color: var(--color-text-muted);
}
</style>
