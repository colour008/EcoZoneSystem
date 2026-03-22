import {createRouter, createWebHistory} from 'vue-router'

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
    // 1. 【新增】C端公共门户路由 (不需要登录)
    {
        path: '/',
        name: 'PortalLayout',
        component: () => import('@/views/portal/layout/index.vue'), // 门户专用的上下布局外壳
        redirect: '/home',
        children: [
            {
                path: 'home',
                name: 'PortalHome',
                component: () => import('@/views/portal/Home.vue'),
                meta: {title: '园区首页'}
            },
            {
                path: 'policy',
                name: 'PortalPolicy',
                component: () => import('@/views/portal/Policy.vue'),
                meta: {title: '政策法规'}
            },
            {
                path: 'article/:id',
                name: 'PortalArticle',
                component: () => import('@/views/portal/Article.vue'),
                meta: { title: '文章详情' }
            },
            {
                path: 'my-enterprise',
                name: 'MyEnterprise',
                meta: { title: '我的入驻申请', requireAuth: true }, // C端需要登录
                component: () => import('@/views/portal/MyEnterprise.vue')
            }
        ]
    },
    // 2. 【保留】B/C端登录后的控制台路由外壳 (侧边栏布局)
    {
        path: '/index',
        name: 'Layout',
        component: () => import('@/views/Index.vue'), // 你的侧边栏 Layout
        redirect: '/index/dashboard',
        children: [
            {
                path: 'dashboard',
                name: 'Dashboard',
                meta: {title: '工作台', icon: 'House'},
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