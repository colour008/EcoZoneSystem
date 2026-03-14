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
        component: () => import('@/views/Index.vue'), // 母版页面
        redirect: '/index/dashboard', // 默认展示主控台
        children: [
            {
                path: 'dashboard',
                name: 'Dashboard',
                meta: { title: '主控台', icon: 'House' },
                component: () => import('@/views/dashboard/Index.vue')
            },
            {
                path: 'user/list',
                name: 'UserList',
                meta: { title: '用户列表' , icon: 'User'},
                component: () => import('@/views/user/List.vue')
            },
            {
                path: 'access/list',
                name: 'AccessList',
                meta: { title: '权限列表', icon: 'Key' },
                component: () => import('@/views/access/List.vue')
            }
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

// 全局路由守卫
router.beforeEach((to, from) => {
    // Vue Router 4 推荐直接返回 true 或不返回，而不是调用 next()
    return true
})

export default router