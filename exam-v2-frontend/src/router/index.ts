import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'
import LoginView from '@/views/LoginView.vue'
import AdminLayout from '@/layouts/AdminLayout.vue'
import TeacherLayout from '@/layouts/TeacherLayout.vue'
import StudentLayout from '@/layouts/StudentLayout.vue'
import PlaceholderView from '@/views/dashboard/PlaceholderView.vue'
import NoticeManageView from '@/views/notice/NoticeManageView.vue'
import TeacherManageView from '@/views/admin/TeacherManageView.vue'
import StudentManageView from '@/views/teacher/StudentManageView.vue'
import ExamManageView from '@/views/teacher/ExamManageView.vue'
import QuestionManageView from '@/views/teacher/QuestionManageView.vue'
import MyExamsView from '@/views/student/MyExamsView.vue'
import ExamDetailView from '@/views/student/ExamDetailView.vue'
import ExamTakingView from '@/views/student/ExamTakingView.vue'
import ScoreQueryView from '@/views/student/ScoreQueryView.vue'
import MessageCenterView from '@/views/student/MessageCenterView.vue'
import DataImportView from '@/views/admin/DataImportView.vue'
import DataExportView from '@/views/admin/DataExportView.vue'
import StatisticsView from '@/views/teacher/StatisticsView.vue'


const routes = [
    { path: '/login', name: 'login', component: LoginView },
    { path: '/', redirect: '/login' },
    // 1. 管理员后台
    {
        path: '/admin',
        component: AdminLayout,
        meta: { role: 'ADMIN' },
        children: [
            { path: 'home', name: 'admin-home', component: PlaceholderView, meta: { title: '管理员首页', description: '全站基础数据概览。' } },
            { path: 'teacher-management', name: 'admin-teachers', component: TeacherManageView, meta: { title: '教师管理', description: '管理全校授课教师账号。' } },
            { path: 'student-management', name: 'admin-students', component: StudentManageView, meta: { title: '学生管理', description: '管理全校在读学生账号。' } },
            { path: 'exam-management', name: 'admin-exams', component: ExamManageView, meta: { title: '考试管理', description: '管理全校考试计划。' } },
            { path: 'data-import', name: 'admin-import', component: DataImportView, meta: { title: '数据导入', description: 'Excel 批量导入学生、教师、考试数据。' } },
            { path: 'data-export', name: 'admin-export', component: DataExportView, meta: { title: '数据导出', description: '导出学生、教师、成绩、考试数据为 Excel。' } },
            { path: 'notice-management', name: 'admin-notices', component: NoticeManageView, meta: { title: '公告管理', description: '发布全校通知公告。' } }
        ]
    },
    // 2. 教师后台
    {
        path: '/teacher',
        component: TeacherLayout,
        meta: { role: 'TEACHER' },
        children: [
            { path: 'home', name: 'teacher-home', component: PlaceholderView, meta: { title: '教师首页', description: '日常教学、考务安排。' } },
            { path: 'exam-management', name: 'teacher-exams', component: ExamManageView, meta: { title: '考试管理', description: '创建在线考试计划。' } },
            { path: 'question-bank', name: 'teacher-questions', component: QuestionManageView, meta: { title: '题库管理', description: '选择题、填空、判断题维护与导入。' } },
            { path: 'student-management', name: 'teacher-students', component: StudentManageView, meta: { title: '学生管理', description: '维护学生白名单与学号。' } },
            { path: 'statistics', name: 'teacher-statistics', component: StatisticsView, meta: { title: '成绩统计', description: '图表化成绩分布与多维度对比分析。' } },
            { path: 'notice-management', name: 'teacher-notices', component: NoticeManageView, meta: { title: '公告管理', description: '发布班级考务通知。' } }
        ]
    },
    // 3. 学生后台
    {
        path: '/student',
        component: StudentLayout,
        meta: { role: 'STUDENT' },
        children: [
            { path: 'home', name: 'student-home', component: PlaceholderView, meta: { title: '学生首页', description: '查看紧迫考试项目。' } },
            { path: 'my-exams', name: 'student-exams', component: MyExamsView, meta: { title: '我的考试', description: '可参加考试列表。' } },
            { path: 'exam-detail/:examCode', name: 'student-exam-detail', component: ExamDetailView, meta: { title: '考试详情', description: '考试说明与须知。' } },
            { path: 'score-query', name: 'student-scores', component: ScoreQueryView, meta: { title: '成绩查询', description: '历史考试成绩与得分。' } },
            { path: 'message-center', name: 'student-messages', component: MessageCenterView, meta: { title: '消息中心', description: '通知与留言回复。' } }
        ]
    },
    // 4. 全屏考试
    { path: '/exam/taking/:examCode', name: 'exam-taking', component: ExamTakingView },

    // 6. 兜底
    { path: '/:pathMatch(.*)*', redirect: '/login' }
]

const router = createRouter({
    history: createWebHistory(),
    routes
})

router.beforeEach((to) => {
    const userStore = useUserStore()

    if (to.path !== '/login' && !userStore.isLoggedIn) return '/login'

    if (to.path === '/login' && userStore.isLoggedIn) {
        if (userStore.role === 'ADMIN') return '/admin/home'
        if (userStore.role === 'TEACHER') return '/teacher/home'
        if (userStore.role === 'STUDENT') return '/student/home'
        return '/login'
    }

    const matchedRoleRoute = to.matched.find(record => record.meta && record.meta.role)
    if (matchedRoleRoute && matchedRoleRoute.meta.role !== userStore.role) {
        if (userStore.role === 'ADMIN') return '/admin/home'
        if (userStore.role === 'TEACHER') return '/teacher/home'
        if (userStore.role === 'STUDENT') return '/student/home'
        return '/login'
    }

    if (to.path.startsWith('/exam/taking/') && userStore.role !== 'STUDENT') return '/login'

    return true
})

export default router
