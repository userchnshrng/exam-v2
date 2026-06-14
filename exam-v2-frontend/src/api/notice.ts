import http from './http'

// 后端统一返回结构（与 auth.ts 保持一致）
export interface ApiResponse<T> {
    code: number
    message: string
    data: T
}

// 分页结果结构
export interface PageResult<T> {
    records: T[]
    total: number
    page: number
    size: number
}

// 公告数据结构
export interface Notice {
    noticeId: number | null
    content: string
    createTime: string
}

/** 分页列表 + 关键词搜索 */
export function listNotices(keyword: string, page: number, size: number) {
    return http.get<ApiResponse<PageResult<Notice>>>('/api/notices', {
        params: { keyword, page, size }
    })
}

/** 查单条 */
export function getNoticeById(id: number) {
    return http.get<ApiResponse<Notice>>(`/api/notices/${id}`)
}

/** 新增 */
export function createNotice(data: Notice) {
    return http.post<ApiResponse<Notice>>('/api/notices', data)
}

/** 更新 */
export function updateNotice(id: number, data: Notice) {
    return http.put<ApiResponse<Notice>>(`/api/notices/${id}`, data)
}

/** 删除 */
export function deleteNotice(id: number) {
    return http.delete<ApiResponse<null>>(`/api/notices/${id}`)
}
