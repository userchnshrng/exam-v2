import axios from 'axios'
import { ElMessage } from 'element-plus'

const http = axios.create({
    baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080',
    timeout: 15000
})

// ---- 请求拦截：自动附带用户信息 ----
http.interceptors.request.use((config) => {
    try {
        const raw = localStorage.getItem('exam-v2-user')
        if (raw) {
            const user = JSON.parse(raw)
            if (user.username) config.headers['X-User-Id'] = String(user.username)
            if (user.role) config.headers['X-User-Role'] = String(user.role)
            if (user.tableName) config.headers['X-User-Table'] = String(user.tableName)
        }
    } catch { /* ignore */ }
    return config
})

// ---- 响应拦截：统一错误提示 ----
http.interceptors.response.use(
    (response) => {
        const body = response.data
        // 后端业务错误
        if (body && typeof body === 'object' && 'code' in body && body.code !== 0) {
            // 不在这里弹错误，让各调用方自己判断
        }
        return response
    },
    (error) => {
        if (error.response) {
            const status = error.response.status
            const messages: Record<number, string> = {
                400: '请求参数有误，请检查输入内容',
                401: '请先登录',
                403: '没有权限执行此操作',
                404: '请求的数据不存在',
                500: '服务器繁忙，请稍后再试',
            }
            ElMessage.error(messages[status] || `请求失败 (${status})`)
        } else if (error.code === 'ECONNABORTED') {
            ElMessage.error('请求超时，请检查网络连接')
        } else {
            ElMessage.error('网络异常，请检查后端服务是否启动')
        }
        return Promise.reject(error)
    }
)

export default http
