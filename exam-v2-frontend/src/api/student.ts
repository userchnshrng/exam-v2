import http from './http'
import type { ApiResponse, PageResult } from './notice'

export interface Student {
    studentId: number | null
    studentName: string
    grade: string
    major: string
    clazz: string
    institute: string
    tel: string
    email: string
    pwd: string
    cardId: string
    sex: string
    role: string
}

export function listStudents(keyword: string, page: number, size: number) {
    return http.get<ApiResponse<PageResult<Student>>>('/api/students', {
        params: { keyword, page, size }
    })
}

export function getStudentById(id: number) {
    return http.get<ApiResponse<Student>>(`/api/students/${id}`)
}

export function createStudent(data: Student) {
    return http.post<ApiResponse<Student>>('/api/students', data)
}

export function updateStudent(id: number, data: Student) {
    return http.put<ApiResponse<Student>>(`/api/students/${id}`, data)
}

export function deleteStudent(id: number) {
    return http.delete<ApiResponse<null>>(`/api/students/${id}`)
}
