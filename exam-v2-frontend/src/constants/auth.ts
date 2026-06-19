import type { LoggedInUser, UserRole } from '@/types/user'

// 角色与其对应的中文标签映射表
export const roleLabels: Record<UserRole, string> = {
  ADMIN: '管理员',
  TEACHER: '教师',
  STUDENT: '学生'
}

// 角色成功登录后的默认后台首页跳转路径
export const roleHomePaths: Record<UserRole, string> = {
  ADMIN: '/admin/home',
  TEACHER: '/teacher/home',
  STUDENT: '/student/home'
}

// 第三阶段专用：内置的演示测试账号（第四阶段可直接替换为后端真实登录接口）
export const demoAccounts: Array<LoggedInUser & { password: string }> = [
  {
    username: '9527',
    password: '123456',
    displayName: '系统管理员',
    role: 'ADMIN',
    tableName: 'adminuser',
    userId: 9527
  },
  {
    username: '20081001',
    password: '123456',
    displayName: '张授课老师',
    role: 'TEACHER',
    tableName: 'teacher',
    userId: 20081001
  }
]

// 获取特定角色的首页路径
export function getHomePath(role: UserRole) {
  return roleHomePaths[role]
}

// 获取角色的中文显示名
export function getRoleLabel(role: UserRole) {
  return roleLabels[role]
}

// 模拟登录：匹配输入的账号和密码
export function matchDemoAccount(username: string, password: string) {
  return demoAccounts.find((item) => item.username === username && item.password === password) || null
}