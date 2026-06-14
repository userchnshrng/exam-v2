import http from './http'
import type { ApiResponse, PageResult } from './notice'

export interface MessageItem {
    id: number | null
    title: string
    content: string
    time: string
}

export interface ReplyItem {
    messageId: number
    replayId: number
    replay: string
    replayTime: string
}

export function listMessages(page: number, size: number) {
    return http.get<ApiResponse<PageResult<MessageItem>>>('/api/messages', { params: { page, size } })
}

export function createMessage(data: MessageItem) {
    return http.post<ApiResponse<MessageItem>>('/api/messages', data)
}

export function getReplies(messageId: number) {
    return http.get<ApiResponse<ReplyItem[]>>(`/api/messages/${messageId}/replies`)
}

export function createReply(messageId: number, data: ReplyItem) {
    return http.post<ApiResponse<ReplyItem>>(`/api/messages/${messageId}/replies`, data)
}
