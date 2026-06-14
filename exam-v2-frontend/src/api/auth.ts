import http from './http'

export interface LoginRequest {
    username: string
    password: string
}

export interface LoginUser {
    role: 'ADMIN' | 'TEACHER' | 'STUDENT'
    tableName: string
    userInfo: Record<string, unknown>
}

export interface ApiResponse<T> {
    code: number
    message: string
    data: T
}

export function login(data: LoginRequest) {
    return http.post<ApiResponse<LoginUser>>('/api/login', data)
}