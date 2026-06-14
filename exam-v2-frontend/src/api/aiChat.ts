import http from './http'
import type { ApiResponse } from './notice'

export interface ChatMessage {
    role: 'user' | 'assistant'
    content: string
}

export interface ChatRequest {
    message: string
    history: ChatMessage[]
}

export interface ChatResponse {
    reply: string
    provider: string
    timestamp: number
}

export function sendChatMessage(data: ChatRequest) {
    return http.post<ApiResponse<ChatResponse>>('/api/ai/chat', data)
}
