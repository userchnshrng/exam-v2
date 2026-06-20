import http from './http'
import type { ApiResponse } from './notice'

// ---- 统一考题 ----
export interface ExamQuestion {
    questionId: number
    questionType: number  // 1选择 2填空 3判断
    subject: string
    question: string
    score: number
    answerA?: string
    answerB?: string
    answerC?: string
    answerD?: string
}

// ---- 提交答案 ----
export interface AnswerItem {
    questionType: number
    questionId: number
    answer: string
}

export interface SubmitRequest {
    examCode: number
    studentId: number
    answers: AnswerItem[]
}

// ---- 提交结果 ----
export interface AnswerResult {
    questionId: number
    questionType: number
    studentAnswer: string
    correctAnswer: string
    isCorrect: boolean
    score: number
}

export interface SubmitResult {
    totalScore: number
    correctCount: number
    totalCount: number
    details: AnswerResult[]
}

/** 获取考试题目（不含答案） */
export function getExamQuestions(examCode: number) {
    return http.get<ApiResponse<ExamQuestion[]>>(`/api/paper/${examCode}/questions`)
}

/** 提交试卷 */
export function submitExam(examCode: number, data: SubmitRequest) {
    return http.post<ApiResponse<SubmitResult>>(`/api/paper/${examCode}/submit`, data)
}

/** 查看答题详情 */
export function getAnswerDetails(examCode: number, studentId: number) {
    return http.get<ApiResponse<AnswerResult[]>>(`/api/paper/${examCode}/answers/${studentId}`)
}

// ---- 组卷（教师端题目-试卷关联管理） ----

export interface ComposeQuestion {
    questionId: number
    questionType: number   // 1=选择 2=填空 3=判断
    subject: string
    question: string
    score: number
    section: string
    level: string
    inPaper: boolean
    // 选择题特有
    answerA?: string
    answerB?: string
    answerC?: string
    answerD?: string
    rightAnswer?: string
    // 填空/判断通用
    answer?: string
    analysis?: string
}

/** 获取组卷数据（某试卷在某科目下的全部题目，含已选标记） */
export function getComposeData(paperId: number, subject: string) {
    return http.get<ApiResponse<ComposeQuestion[]>>(`/api/paper/compose/${paperId}`, { params: { subject } })
}

/** 添加题目到试卷 */
export function addQuestionToPaper(paperId: number, questionType: number, questionId: number) {
    return http.post<ApiResponse<null>>(`/api/paper/compose/${paperId}/add`, { questionType, questionId })
}

/** 从试卷移除题目 */
export function removeQuestionFromPaper(paperId: number, questionType: number, questionId: number) {
    return http.post<ApiResponse<null>>(`/api/paper/compose/${paperId}/remove`, { questionType, questionId })
}

/** 一键自动组卷 */
export function autoCompose(paperId: number, subject: string) {
    return http.post<ApiResponse<{ added: number; message: string }>>(`/api/paper/compose/${paperId}/auto`, null, { params: { subject } })
}
