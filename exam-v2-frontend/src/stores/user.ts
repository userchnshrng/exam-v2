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

        return { username: username || displayName, displayName, role, tableName }
    }

    const username = firstString(record, ['username', 'account', 'aid', 'tid', 'sid'])
    const displayName = firstString(record, ['displayName', 'name']) || username || roleLabels[role]

    if (!username && !displayName) return null

    return { username: username || displayName, displayName, role, tableName }
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