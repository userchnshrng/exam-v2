import http from './http'
import type { ApiResponse, PageResult } from './notice'

export interface ScoreRecord {
    scoreId: number
    examCode: number
    studentId: number
    subject: string
    ptScore: number | null
    etScore: number | null
    score: number | null
    answerDate: string
}

export function listScores(studentId: number, page: number, size: number) {
    return http.get<ApiResponse<PageResult<ScoreRecord>>>('/api/scores', {
        params: { studentId, page, size }
    })
}

export function getScoreById(id: number) {
    return http.get<ApiResponse<ScoreRecord>>(`/api/scores/${id}`)
}
