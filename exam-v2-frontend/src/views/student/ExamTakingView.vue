<template>
  <div class="exam-taking">
    <!-- ===== 顶部栏 ===== -->
    <div class="exam-header">
      <div class="exam-header__left">
        <h2>{{ examStore.examInfo?.description || '考试中' }}</h2>
        <span class="exam-header__subject">{{ examStore.examInfo?.source }}</span>
      </div>
      <div class="exam-header__center">
        <el-progress :percentage="examStore.progress" :stroke-width="8" style="width:200px" />
        <span style="margin-left:8px;color:#86909c;">{{ examStore.answeredCount }}/{{ examStore.totalQuestions }}</span>
      </div>
      <div class="exam-header__right">
        <span class="timer-label">剩余时间</span>
        <span class="timer-value" :class="{ 'timer-danger': examStore.remainingSeconds <= 300 }">
          {{ examStore.remainingDisplay }}
        </span>
      </div>
    </div>

    <!-- ===== 主体 ===== -->
    <div class="exam-body">
      <!-- 题目区 -->
      <div class="exam-question-area">
        <div v-if="examStore.currentQuestion" class="question-card">
          <div class="question-header">
            <el-tag size="small" :type="tagType(examStore.currentQuestion.questionType)">
              {{ typeLabel(examStore.currentQuestion.questionType) }}
            </el-tag>
            <span class="question-score">（{{ examStore.currentQuestion.score }} 分）</span>
          </div>
          <div class="question-text">{{ examStore.currentIndex + 1 }}. {{ examStore.currentQuestion.question }}</div>

          <!-- 选择题 -->
          <el-radio-group
              v-if="examStore.currentQuestion.questionType === 1"
              v-model="currentAnswer"
              class="answer-options"
              @change="onAnswerChange">
            <el-radio v-for="opt in ['A','B','C','D']" :key="opt" :value="opt" class="answer-option">
              {{ opt }}. {{ getOptionText(opt) }}
            </el-radio>
          </el-radio-group>

          <!-- 填空题 -->
          <div v-else-if="examStore.currentQuestion.questionType === 2" class="fill-answer">
            <el-input v-model="currentAnswer" placeholder="请输入你的答案" @input="onAnswerChange" style="max-width:400px" />
          </div>

          <!-- 判断题 -->
          <el-radio-group
              v-else-if="examStore.currentQuestion.questionType === 3"
              v-model="currentAnswer"
              class="answer-options"
              @change="onAnswerChange">
            <el-radio value="T" class="answer-option">正确 (T)</el-radio>
            <el-radio value="F" class="answer-option">错误 (F)</el-radio>
          </el-radio-group>
        </div>

        <!-- 底部导航按钮 -->
        <div class="question-nav-btns">
          <el-button :disabled="examStore.isFirstQuestion" @click="examStore.prevQuestion()">
            上一题
          </el-button>
          <el-button v-if="!examStore.isLastQuestion" type="primary" @click="examStore.nextQuestion()">
            下一题
          </el-button>
          <el-popconfirm v-if="examStore.isLastQuestion && !examStore.submitted"
                         title="确定要提交试卷吗？提交后无法修改。" @confirm="handleSubmit">
            <template #reference>
              <el-button type="danger">提交试卷</el-button>
            </template>
          </el-popconfirm>
        </div>
      </div>

      <!-- 答题卡侧边栏 -->
      <div class="exam-answer-sheet">
        <div class="answer-sheet-title">答题卡</div>
        <div class="answer-sheet-grid">
          <div
              v-for="(q, idx) in examStore.questions"
              :key="q.questionId"
              class="sheet-item"
              :class="{
                'sheet-item--active': idx === examStore.currentIndex,
                'sheet-item--answered': examStore.isAnswered(q.questionType, q.questionId)
              }"
              @click="examStore.jumpTo(idx)"
          >
            {{ idx + 1 }}
          </div>
        </div>
        <el-button type="danger" plain class="submit-btn" @click="handleSubmit"
                   :disabled="examStore.submitted">
          交卷
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, watch, onMounted, onBeforeUnmount } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useExamStore } from '@/stores/exam'
import { useUserStore } from '@/stores/user'
import { getExamQuestions, submitExam } from '@/api/examPaper'
import { getExamById } from '@/api/exam'

const route = useRoute()
const router = useRouter()
const examStore = useExamStore()
const userStore = useUserStore()

const examCode = Number(route.params.examCode)
const currentAnswer = ref('')
const loading = ref(false)

// 题型辅助
function tagType(t: number) { return t === 1 ? '' : t === 2 ? 'success' : 'warning' }
function typeLabel(t: number) { return t === 1 ? '单选题' : t === 2 ? '填空题' : '判断题' }
function getOptionText(opt: string): string {
  const q = examStore.currentQuestion as any
  if (opt === 'A') return q?.answerA || ''
  if (opt === 'B') return q?.answerB || ''
  if (opt === 'C') return q?.answerC || ''
  if (opt === 'D') return q?.answerD || ''
  return ''
}

// 切换题目时同步回显已有答案（使用复合键读取）
watch(() => examStore.currentIndex, () => {
  const q = examStore.currentQuestion
  if (q) currentAnswer.value = examStore.getSavedAnswer(q.questionType, q.questionId)
})

function onAnswerChange() {
  const q = examStore.currentQuestion
  if (q) examStore.saveAnswer(q.questionType, q.questionId, currentAnswer.value)
}

// 超时自动提交
watch(() => examStore.isTimeout, (val) => {
  if (val && !examStore.submitted) {
    ElMessage.warning('考试时间已到，系统将自动提交试卷')
    handleSubmit()
  }
})

async function initExam() {
  loading.value = true
  try {
    // 获取考试信息
    const examRes = await getExamById(examCode)
    if (examRes.data.code !== 0 || !examRes.data.data) {
      ElMessage.error('考试不存在')
      router.replace('/student/my-exams')
      return
    }
    const examInfo = examRes.data.data

    // 获取题目
    const qRes = await getExamQuestions(examCode)
    if (qRes.data.code !== 0 || !qRes.data.data || !qRes.data.data.length) {
      if (qRes.data.code !== 0) {
        ElMessage.error(qRes.data.message || '获取题目失败')
      } else {
        ElMessage.error('该考试暂无题目，请联系教师确认试卷已组卷')
      }
      router.replace('/student/my-exams')
      return
    }

    examStore.startExam(examInfo, qRes.data.data)
    currentAnswer.value = ''

    // ===== 诊断日志：页面加载时输出关键数据摘要（上线前删除） =====
    console.group('🔍 [考试诊断] 页面初始化')
    console.log('用户 userId:', userStore.userId, '| username:', userStore.username, '| role:', userStore.role)
    console.log('考试 examCode:', examCode, '| description:', examInfo.description, '| source:', examInfo.source)
    console.log('题目数:', qRes.data.data.length)
    console.log('前 5 题 questionId:', qRes.data.data.slice(0, 5).map((q: any) => ({ id: q.questionId, type: q.questionType, subject: q.subject })))
    // 检测是否有跨题型重复的 questionId
    const idMap = new Map<string, number>()
    qRes.data.data.forEach((q: any) => {
      const k = `${q.questionType}:${q.questionId}`
      idMap.set(k, (idMap.get(k) || 0) + 1)
    })
    const dupes = [...idMap.entries()].filter(([, c]) => c > 1)
    if (dupes.length) console.warn('⚠️ 发现同一试卷内 (questionType:questionId) 重复:', dupes)
    console.groupEnd()
  } finally {
    loading.value = false
  }
}

async function handleSubmit() {
  if (examStore.submitted) return

  // ========== 阶段 1：确认弹窗 ==========
  try {
    await ElMessageBox.confirm(
      `你已完成 ${examStore.answeredCount}/${examStore.totalQuestions} 题，确定提交？`,
      '确认交卷',
      { confirmButtonText: '确定提交', cancelButtonText: '继续答题', type: 'warning' }
    )
  } catch { return }

  // ========== 阶段 2：前端参数合法性自检 ==========
  const studentId = userStore.userId
  if (!studentId || typeof studentId !== 'number' || studentId <= 0) {
    ElMessage.error(
      `学生 ID 无效（当前值：${userStore.userId}，原始 username：${userStore.username}）。请退出重新登录。`
    )
    return
  }

  if (!examCode || Number.isNaN(examCode) || examCode <= 0) {
    ElMessage.error(`考试编号无效（当前值：${examCode}），请从考试列表重新进入。`)
    return
  }

  if (!examStore.questions.length) {
    ElMessage.error('题目列表为空，无法提交。请刷新页面重试。')
    return
  }

  const answers = examStore.collectAnswers()
  if (!answers.length) {
    ElMessage.error('答案收集失败（answers 数组为空），请重试。')
    return
  }

  // ========== 阶段 3：调试输出（上线前删除） ==========
  const payload = { examCode, studentId, answers }
  console.group('📦 [交卷调试] 请求 Payload')
  console.log('URL:', `/api/paper/${examCode}/submit`)
  console.log('Body:', JSON.stringify(payload, null, 2))
  console.table(payload.answers.slice(0, 10).map(a => ({
    questionType: a.questionType,
    questionId: a.questionId,
    answer: a.answer || '(空)'
  })))
  console.log('用户信息:', {
    rawUsername: userStore.username,
    userId: userStore.userId,
    role: userStore.role,
    displayName: userStore.displayName,
    localStorageUser: localStorage.getItem('exam-v2-user')
  })
  console.log('考试信息:', {
    examCode,
    totalQuestions: examStore.totalQuestions,
    answeredCount: examStore.answeredCount
  })
  console.groupEnd()

  // ========== 阶段 4：发送请求 ==========
  try {
    const res = await submitExam(examCode, { examCode, studentId, answers })
    examStore.markSubmitted()
    if (res.data.code === 0 && res.data.data) {
      const result = res.data.data
      ElMessage.success(`交卷成功！得分：${result.totalScore} 分（${result.correctCount}/${result.totalCount}）`)
    } else {
      // 后端返回了 200 但业务 code ≠ 0
      ElMessage.warning(res.data.message || '提交结果异常')
    }
    router.replace(`/student/score-query`)
  } catch (err: any) {
    // 分层提取错误原因，取代原来的笼统 "提交失败"
    let detail = ''
    if (err?.response?.data) {
      // 后端通过 GlobalExceptionHandler 返回的 JSON 错误体
      const body = err.response.data
      detail = body.message || body.msg || JSON.stringify(body)
    } else if (err?.response?.status) {
      detail = `HTTP ${err.response.status}（无响应体）`
    } else if (err?.message) {
      detail = err.message
    } else {
      detail = '未知网络错误'
    }
    console.error('❌ 交卷失败详情:', detail, err)
    ElMessage.error(`提交失败：${detail}`)
  }
}

// 离开页面时停止计时并清理
onBeforeUnmount(() => {
  if (!examStore.submitted) {
    examStore.resetExam()
  }
})

onMounted(() => initExam())
</script>

<style scoped>
/* 全屏布局 */
.exam-taking {
  position: fixed; inset: 0; z-index: 1000;
  display: flex; flex-direction: column;
  background-color: #f0f2f5;
}

/* 顶部栏 */
.exam-header {
  height: 60px; flex-shrink: 0;
  background: #fff; border-bottom: 1px solid #e6e8eb;
  display: flex; align-items: center; justify-content: space-between;
  padding: 0 24px;
}
.exam-header__left h2 { margin: 0; font-size: 18px; color: #1d2129; }
.exam-header__subject { font-size: 13px; color: #86909c; margin-left: 12px; }
.exam-header__center { display: flex; align-items: center; }
.exam-header__right { display: flex; align-items: center; gap: 8px; }
.timer-label { font-size: 14px; color: #86909c; }
.timer-value { font-size: 28px; font-weight: 700; color: #1d2129; font-variant-numeric: tabular-nums; }
.timer-danger { color: #f53f3f; animation: pulse 1s infinite; }
@keyframes pulse { 50% { opacity: 0.5; } }

/* 主体 */
.exam-body {
  flex: 1; display: flex; overflow: hidden;
}

/* 题目区 */
.exam-question-area {
  flex: 1; padding: 24px; overflow-y: auto;
  display: flex; flex-direction: column;
}
.question-card {
  background: #fff; border-radius: 12px; padding: 24px;
  box-shadow: 0 1px 4px rgba(0,0,0,0.04); flex: 1;
}
.question-header { display: flex; align-items: center; gap: 8px; margin-bottom: 16px; }
.question-score { color: #86909c; font-size: 13px; }
.question-text { font-size: 16px; line-height: 1.8; color: #1d2129; margin-bottom: 24px; }

.answer-options { display: flex; flex-direction: column; gap: 12px; }
.answer-option { padding: 12px 16px; border: 1px solid #e6e8eb; border-radius: 8px;
  margin: 0; width: 100%; }
.answer-option:hover { border-color: #3370ff; background: #f2f7ff; }

.fill-answer { margin-top: 8px; }

.question-nav-btns { display: flex; gap: 12px; justify-content: center; margin-top: 20px; padding-bottom: 8px; }

/* 答题卡 */
.exam-answer-sheet {
  width: 260px; background: #fff; border-left: 1px solid #e6e8eb;
  display: flex; flex-direction: column; align-items: center;
  padding: 16px; overflow-y: auto; flex-shrink: 0;
}
.answer-sheet-title { font-size: 15px; font-weight: 600; color: #1d2129; margin-bottom: 16px; }
.answer-sheet-grid {
  display: grid; grid-template-columns: repeat(5, 1fr); gap: 8px;
  width: 100%;
}
.sheet-item {
  width: 100%; aspect-ratio: 1; display: flex; align-items: center; justify-content: center;
  border: 1px solid #e6e8eb; border-radius: 6px; font-size: 13px; color: #4e5969;
  cursor: pointer; transition: all .2s;
}
.sheet-item:hover { border-color: #3370ff; }
.sheet-item--active { border-color: #3370ff; background: #f2f7ff; color: #3370ff; font-weight: 600; }
.sheet-item--answered { background: #e8ffea; border-color: #00b42a; color: #00b42a; }
.sheet-item--active.sheet-item--answered { background: #f2f7ff; border-color: #3370ff; color: #3370ff; }

.submit-btn { width: 100%; margin-top: 16px; }
</style>
