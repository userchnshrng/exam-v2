import http from './http'
import type { ApiResponse, PageResult } from './notice'

// ---------- 选择题 ----------
export interface MultiQuestion {
    questionId: number | null
    subject: string
    question: string
    answerA: string
    answerB: string
    answerC: string
    answerD: string
    rightAnswer: string
    analysis: string
    score: number | null
    section: string
    level: string
}

// ---------- 填空题 ----------
export interface FillQuestion {
    questionId: number | null
    subject: string
    question: string
    answer: string
    analysis: string
    score: number | null
    level: string
    section: string
}

// ---------- 判断题 ----------
export interface JudgeQuestion {
    questionId: number | null
    subject: string
    question: string
    answer: string      // T / F
    analysis: string
    score: number | null
    level: string
    section: string
}

// ===== 选择题 API =====
export function listMulti(keyword: string, page: number, size: number) {
    return http.get<ApiResponse<PageResult<MultiQuestion>>>('/api/questions/multi', { params: { keyword, page, size } })
}
export function createMulti(data: MultiQuestion) { return http.post<ApiResponse<MultiQuestion>>('/api/questions/multi', data) }
export function updateMulti(id: number, data: MultiQuestion) { return http.put<ApiResponse<MultiQuestion>>(`/api/questions/multi/${id}`, data) }
export function deleteMulti(id: number) { return http.delete(`/api/questions/multi/${id}`) }

// ===== 填空题 API =====
export function listFill(keyword: string, page: number, size: number) {
    return http.get<ApiResponse<PageResult<FillQuestion>>>('/api/questions/fill', { params: { keyword, page, size } })
}
export function createFill(data: FillQuestion) { return http.post<ApiResponse<FillQuestion>>('/api/questions/fill', data) }
export function updateFill(id: number, data: FillQuestion) { return http.put<ApiResponse<FillQuestion>>(`/api/questions/fill/${id}`, data) }
export function deleteFill(id: number) { return http.delete(`/api/questions/fill/${id}`) }

// ===== 判断题 API =====
export function listJudge(keyword: string, page: number, size: number) {
    return http.get<ApiResponse<PageResult<JudgeQuestion>>>('/api/questions/judge', { params: { keyword, page, size } })
}
export function createJudge(data: JudgeQuestion) { return http.post<ApiResponse<JudgeQuestion>>('/api/questions/judge', data) }
export function updateJudge(id: number, data: JudgeQuestion) { return http.put<ApiResponse<JudgeQuestion>>(`/api/questions/judge/${id}`, data) }
export function deleteJudge(id: number) { return http.delete(`/api/questions/judge/${id}`) }
