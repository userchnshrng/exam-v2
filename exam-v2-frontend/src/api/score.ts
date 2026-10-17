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

/** 仅管理员可按唯一成绩编号删除一次考试记录，不影响其他次成绩。 */
export function deleteScoreRecord(scoreId: number) {
    return http.delete<ApiResponse<null>>(`/api/scores/${scoreId}`)
}

export interface StudentExamRecord {
    scoreId: number | null
    examCode: number
    examName: string | null
    subject: string | null
    answerDate: string | null
    etScore: number | null
}

export function getStudentExamRecords(studentId: number) {
    return http.get<ApiResponse<StudentExamRecord[]>>(`/api/scores/students/${studentId}/records`)
}

export function deleteUnscoredAnswers(studentId: number, examCode: number) {
    return http.delete<ApiResponse<null>>(`/api/scores/students/${studentId}/unscored/${examCode}`)
}
