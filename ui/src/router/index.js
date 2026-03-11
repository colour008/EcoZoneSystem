// src/router/index.js
import { createRouter, createWebHistory } from 'vue-router'

const routes = [
    {
        path: '/login',
        name: 'Login',
        component: () => import('@/views/login/Login.vue')
    },
    {
        path: '/',
        redirect: '/index' // 根路径自动重定向到后台主页
    },
    {
        path: '/index',
        name: 'Layout',
        component: () => import('@/views/Index.vue'), // 这是我们的母版页面
        redirect: '/index/dashboard', // 默认展示主控台
        children: [
            // 下面这些子路由会渲染在 Index.vue 的 <router-view> 中
            {
                path: 'dashboard',
                name: 'Dashboard',
                meta: { title: '主控台' },
                component: () => import('@/views/dashboard/Index.vue') // 你需要新建这个空文件占位
            },
            {
                path: 'access/list',
                name: 'AccessList',
                meta: { title: '权限管理' },
                component: () => import('@/views/access/List.vue') // 你需要新建这个空文件占位
            }
            // 后续阶段 3 的动态路由会通过 router.addRoute 挂载到这里
        ]
    },
    {
        path: '/404',
        component: () => import('@/views/404.vue')
    },
    {
        path: '/:pathMatch(.*)*',
        redirect: '/404'
    }
]

const router = createRouter({
    history: createWebHistory(),
    routes
})

export default router