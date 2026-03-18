import { createRouter, createWebHistory } from 'vue-router'

const routes = [
    {
        path: '/login',
        name: 'Login',
        component: () => import('@/views/login/Login.vue')
    },
    {
        path: '/register',
        name: 'Register',
        component: () => import('@/views/login/Register.vue')
    },
    {
        path: '/',
        redirect: '/index/dashboard'
    },
    {
        path: '/index',
        name: 'Layout', // 确保这个 Name 存在，供 addRoute 使用
        component: () => import('@/views/Index.vue'),
        children: [
            // 基础面板可以保留在这里
            {
                path: 'dashboard',
                name: 'Dashboard',
                meta: { title: '主控台', icon: 'House' },
                component: () => import('@/views/dashboard/Index.vue')
            }
        ]
    },
    {
        path: '/404',
        component: () => import('@/views/404.vue')
    }
]

const router = createRouter({
    history: createWebHistory(),
    routes
})

export default router