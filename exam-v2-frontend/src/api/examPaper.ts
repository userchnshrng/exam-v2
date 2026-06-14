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
