import { defineStore } from 'pinia'
import { roleLabels } from '@/constants/auth'
import type { LoggedInUser, UserRole } from '@/types/user'

const storageKey = 'exam-v2-user'
const roles: UserRole[] = ['ADMIN', 'TEACHER', 'STUDENT']

function isUserRole(value: unknown): value is UserRole {
    return typeof value === 'string' && roles.includes(value as UserRole)
}

function firstString(record: Record<string, unknown>, keys: string[]) {
    for (const key of keys) {
        const value = record[key]
        if (typeof value === 'string' && value.trim()) {
            return value.trim()
        }
    }
    return ''
}

// 标准化从本地存储或接口返回的数据，防止脏数据注入
function normalizeUser(raw: unknown): LoggedInUser | null {
    if (!raw || typeof raw !== 'object') return null

    const record = raw as Record<string, unknown>
    const role = record.role
    if (!isUserRole(role)) return null

    const tableName = typeof record.tableName === 'string' ? record.tableName : undefined

    // 提取数据库主键 ID（优先级：显式 userId > 各表 ID 字段 > Number(username)）
    function extractUserId(rec: Record<string, unknown>): number | undefined {
        if (typeof rec.userId === 'number' && !isNaN(rec.userId)) return rec.userId
        const idFields = ['adminid', 'teacherid', 'studentid', 'adminId', 'teacherId', 'studentId', 'id']
        for (const f of idFields) {
            const v = rec[f]
            if (typeof v === 'number' && !isNaN(v)) return v
        }
        return undefined
    }

    if ('userInfo' in record && record.userInfo && typeof record.userInfo === 'object') {
        const userInfo = record.userInfo as Record<string, unknown>
        const username =
            firstString(userInfo, ['username', 'account', 'aid', 'tid', 'sid', 'adminid', 'teacherid', 'studentid']) ||
            firstString(record, ['username', 'account'])
        const displayName =
            firstString(userInfo, ['displayName', 'name', 'aname', 'tname', 'sname', 'adminname', 'teachername', 'studentname']) ||
            username ||
            roleLabels[role]

        if (!username && !displayName) return null

        const userId = extractUserId(record) ?? extractUserId(userInfo) ?? (Number(username) || undefined)
        return { username: username || displayName, displayName, role, tableName, userId }
    }

    const username = firstString(record, ['username', 'account', 'aid', 'tid', 'sid'])
    const displayName = firstString(record, ['displayName', 'name']) || username || roleLabels[role]

    if (!username && !displayName) return null

    const userId = extractUserId(record) ?? (Number(username) || undefined)
    return { username: username || displayName, displayName, role, tableName, userId }
}

// 初始化时自动读取浏览器的 localStorage，保持状态不随页面刷新而丢失
function readUser(): LoggedInUser | null {
    const raw = localStorage.getItem(storageKey)
    if (!raw) return null
    try {
        return normalizeUser(JSON.parse(raw))
    } catch {
        return null
    }
}

export const useUserStore = defineStore('user', {
    state: () => ({
        user: readUser() as LoggedInUser | null
    }),
    getters: {
        role: (state) => state.user?.role || '',
        displayName: (state) => state.user?.displayName || '',
        username: (state) => state.user?.username || '',
        userId: (state) => state.user?.userId ?? (Number(state.user?.username) || undefined),
        isLoggedIn: (state) => Boolean(state.user) // 转换成布尔值表示是否已登录
    },
    actions: {
        // 设置并保存用户状态
        setUser(user: LoggedInUser) {
            this.user = user
            localStorage.setItem(storageKey, JSON.stringify(user))
        },
        // 清理用户状态（常用于注销）
        clearUser() {
            this.user = null
            localStorage.removeItem(storageKey)
        }
    }
})