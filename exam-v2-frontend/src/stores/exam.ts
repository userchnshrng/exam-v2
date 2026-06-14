import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { ExamQuestion, AnswerItem } from '@/api/examPaper'
import type { ExamManage } from '@/api/exam'

/**
 * 生成 answers 映射表的复合键。
 * 因为 multi_question / fill_question / judge_question 三表各自独立自增，
 * questionId 会跨题型重复（如选择题 ID=10001 与判断题 ID=10001 同时存在），
 * 仅用 questionId 做 key 会导致不同题型的答案互相覆盖。
 * 格式："questionType:questionId"（如 "1:10001"）
 */
function answerKey(questionType: number, questionId: number): string {
    return `${questionType}:${questionId}`
}

export const useExamStore = defineStore('exam', () => {
    // ---- 考试基本信息 ----
    const examInfo = ref<ExamManage | null>(null)
    const questions = ref<ExamQuestion[]>([])

    // ---- 答题状态 ----
    const currentIndex = ref(0)
    /** 复合键 "questionType:questionId" → 学生答案 */
    const answers = ref<Record<string, string>>({})

    // ---- 计时 ----
    const totalSeconds = ref(0)
    const remainingSeconds = ref(0)
    const startTime = ref<number | null>(null)
    const submitted = ref(false)
    let timerHandle: ReturnType<typeof setInterval> | null = null

    // ---- 计算 ----
    const currentQuestion = computed(() => questions.value[currentIndex.value] || null)
    const totalQuestions = computed(() => questions.value.length)
    const answeredCount = computed(() => Object.keys(answers.value).length)
    const progress = computed(() => totalQuestions.value > 0
        ? Math.round((answeredCount.value / totalQuestions.value) * 100) : 0)
    const isLastQuestion = computed(() => currentIndex.value >= totalQuestions.value - 1)
    const isFirstQuestion = computed(() => currentIndex.value === 0)

    const remainingDisplay = computed(() => {
        const m = Math.floor(remainingSeconds.value / 60)
        const s = remainingSeconds.value % 60
        return `${String(m).padStart(2, '0')}:${String(s).padStart(2, '0')}`
    })
    const isTimeout = computed(() => remainingSeconds.value <= 0 && !submitted.value)

    // ---- 操作 ----
    function startExam(info: ExamManage, questionList: ExamQuestion[]) {
        examInfo.value = info
        questions.value = questionList
        answers.value = {}
        currentIndex.value = 0
        submitted.value = false
        totalSeconds.value = (info.totalTime || 60) * 60
        remainingSeconds.value = totalSeconds.value
        startTime.value = Date.now()
        startTimer()
    }

    function startTimer() {
        stopTimer()
        timerHandle = setInterval(() => {
            if (remainingSeconds.value > 0) {
                remainingSeconds.value--
            }
            if (remainingSeconds.value <= 0) {
                stopTimer()
            }
        }, 1000)
    }

    function stopTimer() {
        if (timerHandle) {
            clearInterval(timerHandle)
            timerHandle = null
        }
    }

    /** 保存答案 — 使用复合键避免跨题型 ID 碰撞 */
    function saveAnswer(questionType: number, questionId: number, answer: string) {
        answers.value[answerKey(questionType, questionId)] = answer
    }

    /** 读取已保存的答案 */
    function getSavedAnswer(questionType: number, questionId: number): string {
        return answers.value[answerKey(questionType, questionId)] || ''
    }

    /** 判断某题是否已作答（供答题卡模板使用） */
    function isAnswered(questionType: number, questionId: number): boolean {
        const val = answers.value[answerKey(questionType, questionId)]
        return val !== undefined && val !== ''
    }

    function nextQuestion() {
        if (currentIndex.value < totalQuestions.value - 1) {
            currentIndex.value++
        }
    }

    function prevQuestion() {
        if (currentIndex.value > 0) {
            currentIndex.value--
        }
    }

    function jumpTo(index: number) {
        if (index >= 0 && index < totalQuestions.value) {
            currentIndex.value = index
        }
    }

    /** 收集全部答案，用于提交请求 */
    function collectAnswers(): AnswerItem[] {
        return questions.value.map(q => ({
            questionType: q.questionType,
            questionId: q.questionId,
            answer: getSavedAnswer(q.questionType, q.questionId)
        }))
    }

    function markSubmitted() {
        submitted.value = true
        stopTimer()
    }

    function resetExam() {
        stopTimer()
        examInfo.value = null
        questions.value = []
        answers.value = {}
        currentIndex.value = 0
        remainingSeconds.value = 0
        totalSeconds.value = 0
        startTime.value = null
        submitted.value = false
    }

    return {
        examInfo, questions, currentIndex, answers, totalSeconds,
        remainingSeconds, startTime, submitted,
        currentQuestion, totalQuestions, answeredCount, progress,
        isLastQuestion, isFirstQuestion, remainingDisplay, isTimeout,
        startExam, startTimer, stopTimer, saveAnswer, getSavedAnswer, isAnswered,
        nextQuestion, prevQuestion, jumpTo, collectAnswers, markSubmitted, resetExam
    }
})
