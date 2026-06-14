import http from './http'
import type { ApiResponse } from './notice'

export interface ImportResult {
    success: number
    fail: number
    total: number
    errors: string[]
}

/** 导入学生 */
export function importStudents(file: File) {
    const fd = new FormData()
    fd.append('file', file)
    return http.post<ApiResponse<ImportResult>>('/api/import/students', fd, {
        headers: { 'Content-Type': 'multipart/form-data' }
    })
}

/** 导入选择题 */
export function importQuestions(file: File) {
    const fd = new FormData()
    fd.append('file', file)
    return http.post<ApiResponse<ImportResult>>('/api/import/questions', fd, {
        headers: { 'Content-Type': 'multipart/form-data' }
    })
}

/** 导入考试 */
export function importExams(file: File) {
    const fd = new FormData()
    fd.append('file', file)
    return http.post<ApiResponse<ImportResult>>('/api/import/exams', fd, {
        headers: { 'Content-Type': 'multipart/form-data' }
    })
}

/** 下载导出文件 */
export function downloadExport(type: string, params?: Record<string, any>) {
    return http.get(`/api/export/${type}`, {
        params,
        responseType: 'blob'
    }).then(res => {
        const blob = new Blob([res.data], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' })
        const url = window.URL.createObjectURL(blob)
        const a = document.createElement('a')
        a.href = url
        const disposition = res.headers['content-disposition'] || ''
        const match = disposition.match(/filename\*=UTF-8''(.+)/)
        a.download = match ? decodeURIComponent(match[1]) : `${type}.xlsx`
        a.click()
        window.URL.revokeObjectURL(url)
    })
}
