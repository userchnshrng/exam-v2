import http from './http'
import type { ApiResponse } from './notice'

/** 答题记录 */
export interface ExamAnswerRecord {
    answerId: number
    examCode: number
    studentId: number
    /** 1=选择题, 2=填空题, 3=判断题 */
    questionType: number
    questionId: number
    studentAnswer: string
    correctAnswer: string
    isCorrect: boolean
    score: number
    /** 题目题干 */
    questionContent?: string
    /** 选择题选项 */
    optionA?: string
    optionB?: string
    optionC?: string
    optionD?: string
}

/** 查询指定考试的答题详情（学生本人） */
export function getExamAnswers(examCode: number) {
    return http.get<ApiResponse<ExamAnswerRecord[]>>(`/api/exam-answers/${examCode}`)
}

/** 教师查看指定学生在指定考试中的答题详情 */
export function getStudentExamAnswers(examCode: number, studentId: number) {
    return http.get<ApiResponse<ExamAnswerRecord[]>>(`/api/exam-answers/${examCode}/${studentId}`)
}

/** 教师修改单条答题记录 */
export function updateExamAnswer(answerId: number, data: Partial<ExamAnswerRecord>) {
    return http.put<ApiResponse<null>>(`/api/exam-answers/${answerId}`, data)
}

/** 教师重置某学生在某场考试的全部记录（答题 + 成绩） */
export function resetExamRecord(examCode: number, studentId: number) {
    return http.delete<ApiResponse<null>>(`/api/exam-answers/${examCode}/${studentId}`)
}
