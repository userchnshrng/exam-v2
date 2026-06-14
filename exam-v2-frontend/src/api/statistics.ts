import http from './http'
import type { ApiResponse } from './notice'

export interface DistributionItem {
    name: string
    value: number
}

export interface ScoreDistribution {
    distribution: DistributionItem[]
    totalCount: number
    average: number
}

export interface ExamComparisonItem {
    examCode: number
    examName: string
    avgScore: number
    maxScore: number
    minScore: number
    count: number
}

export interface StudentScoreItem {
    studentId: number
    score: number
    subject: string
    date: string
}

export interface ExamOption {
    examCode: number
    examName: string
}

export function getScoreDistribution() {
    return http.get<ApiResponse<ScoreDistribution>>('/api/statistics/score-distribution')
}

export function getExamComparison() {
    return http.get<ApiResponse<ExamComparisonItem[]>>('/api/statistics/exam-comparison')
}

export function getExamStudentScores(examCode: number) {
    return http.get<ApiResponse<StudentScoreItem[]>>(`/api/statistics/exam/${examCode}/students`)
}

export function getExamOptions() {
    return http.get<ApiResponse<ExamOption[]>>('/api/statistics/exam-options')
}
