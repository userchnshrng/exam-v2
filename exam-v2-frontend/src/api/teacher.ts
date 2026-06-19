import http from './http'
import type { ApiResponse, PageResult } from './notice'

// 教师数据结构
export interface Teacher {
    teacherId: number | null
    teacherName: string
    institute: string
    sex: string
    tel: string
    email: string
    pwd: string       // 新增时必填，编辑时选填（空则不修改密码）
    cardId: string
    type: string       // 职称
    role: string
}

/** 分页列表 + 搜索 */
export function listTeachers(keyword: string, page: number, size: number) {
    return http.get<ApiResponse<PageResult<Teacher>>>('/api/teachers', {
        params: { keyword, page, size }
    })
}

/** 查单条 */
export function getTeacherById(id: number) {
    return http.get<ApiResponse<Teacher>>(`/api/teachers/${id}`)
}

/** 新增 */
export function createTeacher(data: Teacher) {
    return http.post<ApiResponse<Teacher>>('/api/teachers', data)
}

/** 更新 */
export function updateTeacher(id: number, data: Teacher) {
    return http.put<ApiResponse<Teacher>>(`/api/teachers/${id}`, data)
}

/** 删除 */
export function deleteTeacher(id: number) {
    return http.delete<ApiResponse<null>>(`/api/teachers/${id}`)
}

/** 公开接口：按工号查询教师基本信息（登录页演示卡片动态显示用） */
export function getTeacherPublicInfo(username: string) {
    return http.get<ApiResponse<{ teacherId: number; teacherName: string }>>('/api/teachers/public/info', {
        params: { username }
    })
}
