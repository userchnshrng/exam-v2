import http from './http'
import type { ApiResponse, PageResult } from './notice'

export interface ExamManage {
    examCode: number | null
    description: string
    source: string
    paperId: number | null
    examDate: string
    totalTime: number | null
    grade: string
    term: string
    major: string
    institute: string
    totalScore: number | null
    type: string
    tips: string
}

export function listExams(keyword: string, page: number, size: number) {
    return http.get<ApiResponse<PageResult<ExamManage>>>('/api/exams', {
        params: { keyword, page, size }
    })
}

export function getExamById(id: number) {
    return http.get<ApiResponse<ExamManage>>(`/api/exams/${id}`)
}

export function createExam(data: ExamManage) {
    return http.post<ApiResponse<ExamManage>>('/api/exams', data)
}

export function updateExam(id: number, data: ExamManage) {
    return http.put<ApiResponse<ExamManage>>(`/api/exams/${id}`, data)
}

export function deleteExam(id: number) {
    return http.delete<ApiResponse<null>>(`/api/exams/${id}`)
}
