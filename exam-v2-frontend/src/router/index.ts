import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'
import LoginView from '@/views/LoginView.vue'
import AdminLayout from '@/layouts/AdminLayout.vue'
import TeacherLayout from '@/layouts/TeacherLayout.vue'
import StudentLayout from '@/layouts/StudentLayout.vue'
import PlaceholderView from '@/views/dashboard/PlaceholderView.vue'

// 路由结构规划：通过嵌套路由，将对应的页面分配到专属布局框架中
const routes = [
    {
        path: '/login',
        name: 'login',
        component: LoginView
    },
    {
        path: '/',
        redirect: '/login'
    },
    // 1. 管理员后台路由分支
    {
        path: '/admin',
        component: AdminLayout,
        meta: { role: 'ADMIN' },
        children: [
            { path: 'home', name: 'admin-home', component: PlaceholderView, meta: { title: '管理员首页', description: '全站基础数据、班级及整体运行概览。' } },
            { path: 'teacher-management', name: 'admin-teachers', component: PlaceholderView, meta: { title: '教师管理', description: '管理学校所有授课教师的账号开设、状态与基础信息。' } },
            { path: 'notice-management', name: 'admin-notices', component: PlaceholderView, meta: { title: '公告管理', description: '发布全校性质的通知公告、考试合规守则等。' } }
        ]
    },
    // 2. 教师后台路由分支
    {
        path: '/teacher',
        component: TeacherLayout,
        meta: { role: 'TEACHER' },
        children: [
            { path: 'home', name: 'teacher-home', component: PlaceholderView, meta: { title: '教师首页', description: '日常教学、考务安排与高频工作站入口。' } },
            { path: 'exam-management', name: 'teacher-exams', component: PlaceholderView, meta: { title: '考试 management', description: '创建在线考试计划、设定考试时长、编排关联试卷。' } },
            { path: 'question-bank', name: 'teacher-questions', component: PlaceholderView, meta: { title: '题库管理', description: '各学科核心题目的录入、分类标签化维护与批量导入。' } },
            { path: 'student-management', name: 'teacher-students', component: PlaceholderView, meta: { title: '学生管理', description: '维护当前所授班级、课程的学生白名单与学号校验。' } },
            { path: 'score-query', name: 'teacher-scores', component: PlaceholderView, meta: { title: '成绩查询', description: '多维度智能统计班级考试得分，提供详尽的答卷错题分析。' } },
            { path: 'notice-management', name: 'teacher-notices', component: PlaceholderView, meta: { title: '公告管理', description: '发布面向具体所带班级、考场的通知消息。' } }
        ]
    },
    // 3. 学生后台路由分支
    {
        path: '/student',
        component: StudentLayout,
        meta: { role: 'STUDENT' },
        children: [
            { path: 'home', name: 'student-home', component: PlaceholderView, meta: { title: '学生首页', description: '查看今日紧迫考试项目，接收最新发布的考务通知。' } },
            { path: 'my-exams', name: 'student-exams', component: PlaceholderView, meta: { title: '我的考试', description: '查看进行中、未开始或历史已考科目，直接进入在线考场。' } },
            { path: 'score-query', name: 'student-scores', component: PlaceholderView, meta: { title: '成绩查询', description: '查阅已批改完成的试卷得分、错题详细解析及教师综合评语。' } },
            { path: 'message-center', name: 'student-messages', component: PlaceholderView, meta: { title: '消息中心', description: '实时接收考试时间变更通知、成绩发布提醒等各类私信。' } }
        ]
    },
    // 4. 兜底路由：防止输错网址出现空白
    {
        path: '/:pathMatch(.*)*',
        redirect: '/login'
    }
]

const router = createRouter({
    history: createWebHistory(),
    routes
})

// 核心路由守卫逻辑
router.beforeEach((to) => {
    const userStore = useUserStore()

    // 逻辑 A: 未登录拦截
    if (to.path !== '/login' && !userStore.isLoggedIn) {
        return '/login'
    }

    // 逻辑 B: 已登录防重定向
    if (to.path === '/login' && userStore.isLoggedIn) {
        if (userStore.role === 'ADMIN') return '/admin/home'
        if (userStore.role === 'TEACHER') return '/teacher/home'
        if (userStore.role === 'STUDENT') return '/student/home'
        return '/login'
    }

    // 逻辑 C: 越权与防跨角色串门拦截
    // 寻找当前目标路由树上是否绑定了角色限制的要求
    const matchedRoleRoute = to.matched.find(record => record.meta && record.meta.role)
    if (matchedRoleRoute && matchedRoleRoute.meta.role !== userStore.role) {
        // 如果用户的实际角色不符，强行拉回各自正确的后台首页
        if (userStore.role === 'ADMIN') return '/admin/home'
        if (userStore.role === 'TEACHER') return '/teacher/home'
        if (userStore.role === 'STUDENT') return '/student/home'
        return '/login'
    }

    return true
})

export default router