import type { UserRole } from '@/types/user'

// 定义单个菜单项的结构
export interface DashboardMenuItem {
  key: string       // 唯一标识，与菜单激活状态绑定
  label: string     // 菜单中显示的文字
  path?: string     // 点击时跳转的路由路径
  action?: 'logout' // 触发的特殊动作，例如退出登录
}

// 为三种不同的角色配置独立完整的菜单
export const dashboardMenus: Record<UserRole, DashboardMenuItem[]> = {
  ADMIN: [
    { key: '/admin/home', label: '首页', path: '/admin/home' },
    { key: '/admin/teacher-management', label: '教师管理', path: '/admin/teacher-management' },
    { key: '/admin/notice-management', label: '公告管理', path: '/admin/notice-management' },
    { key: 'logout', label: '退出登录', action: 'logout' }
  ],
  TEACHER: [
    { key: '/teacher/home', label: '首页', path: '/teacher/home' },
    { key: '/teacher/exam-management', label: '考试管理', path: '/teacher/exam-management' },
    { key: '/teacher/question-bank', label: '题库管理', path: '/teacher/question-bank' },
    { key: '/teacher/student-management', label: '学生管理', path: '/teacher/student-management' },
    { key: '/teacher/score-query', label: '成绩查询', path: '/teacher/score-query' },
    { key: '/teacher/notice-management', label: '公告管理', path: '/teacher/notice-management' },
    { key: 'logout', label: '退出登录', action: 'logout' }
  ],
  STUDENT: [
    { key: '/student/home', label: '首页', path: '/student/home' },
    { key: '/student/my-exams', label: '我的考试', path: '/student/my-exams' },
    { key: '/student/score-query', label: '成绩查询', path: '/student/score-query' },
    { key: '/student/message-center', label: '消息中心', path: '/student/message-center' },
    { key: 'logout', label: '退出登录', action: 'logout' }
  ]
}