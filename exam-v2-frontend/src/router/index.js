import { createRouter, createWebHistory } from 'vue-router';
import { useUserStore } from '@/stores/user';
import LoginView from '@/views/LoginView.vue';
import AdminHomeView from '@/views/AdminHomeView.vue';
import TeacherHomeView from '@/views/TeacherHomeView.vue';
import StudentHomeView from '@/views/StudentHomeView.vue';
import MainLayout from '@/layouts/MainLayout.vue';
const routes = [
    { path: '/login', name: 'login', component: LoginView },
    {
        path: '/',
        component: MainLayout,
        children: [
            { path: '', redirect: '/login' },
            { path: 'admin/home', name: 'admin-home', component: AdminHomeView },
            { path: 'teacher/home', name: 'teacher-home', component: TeacherHomeView },
            { path: 'student/home', name: 'student-home', component: StudentHomeView }
        ]
    }
];
const router = createRouter({
    history: createWebHistory(),
    routes
});
router.beforeEach((to) => {
    const userStore = useUserStore();
    if (to.path !== '/login' && !userStore.isLoggedIn) {
        return '/login';
    }
    if (to.path === '/login' && userStore.isLoggedIn) {
        if (userStore.role === 'ADMIN')
            return '/admin/home';
        if (userStore.role === 'TEACHER')
            return '/teacher/home';
        if (userStore.role === 'STUDENT')
            return '/student/home';
        return '/login';
    }
    return true;
});
export default router;
//# sourceMappingURL=index.js.map