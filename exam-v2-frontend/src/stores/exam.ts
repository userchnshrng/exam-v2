import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { ExamQuestion, AnswerItem } from '@/api/examPaper'
import type { ExamManage } from '@/api/exam'

export const useExamStore = defineStore('exam', () => {
    // ---- 考试基本信息 ----
    const examInfo = ref<ExamManage | null>(null)
    const questions = ref<ExamQuestion[]>([])

    // ---- 答题状态 ----
    const currentIndex = ref(0)
    const answers = ref<Record<number, string>>({})   // questionId → answer

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

    function saveAnswer(questionId: number, answer: string) {
        answers.value[questionId] = answer
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

    function collectAnswers(): AnswerItem[] {
        return questions.value.map(q => ({
            questionType: q.questionType,
            questionId: q.questionId,
            answer: answers.value[q.questionId] || ''
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
        startExam, startTimer, stopTimer, saveAnswer, nextQuestion,
        prevQuestion, jumpTo, collectAnswers, markSubmitted, resetExam
    }
})
