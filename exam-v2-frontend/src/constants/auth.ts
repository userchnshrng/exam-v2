import type { LoggedInUser, UserRole } from '@/types/user'

export const roleLabels: Record<UserRole, string> = {
  ADMIN: '管理员',
  TEACHER: '教师',
  STUDENT: '学生'
}

export const roleHomePaths: Record<UserRole, string> = {
  ADMIN: '/admin/home',
  TEACHER: '/teacher/home',
  STUDENT: '/student/home'
}

export const demoAccounts: Array<LoggedInUser & { password: string }> = [
  {
    username: '9527',
    password: '123456',
    displayName: '系统管理员',
    role: 'ADMIN',
    tableName: 'adminuser'
  },
  {
    username: '20081001',
    password: '123456',
    displayName: '授课教师',
    role: 'TEACHER',
    tableName: 'teacher'
  },
  {
    username: '20154084',
    password: '123456',
    displayName: '演示学生',
    role: 'STUDENT',
    tableName: 'student'
  }
]

export function getHomePath(role: UserRole) {
  return roleHomePaths[role]
}

export function getRoleLabel(role: UserRole) {
  return roleLabels[role]
}

export function matchDemoAccount(username: string, password: string) {
  return demoAccounts.find((item) => item.username === username && item.password === password) || null
}
