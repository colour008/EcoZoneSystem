import {createRouter, createWebHistory} from 'vue-router'
import {useUserStore} from '@/store/user'
import NProgress from 'nprogress'
import 'nprogress/nprogress.css'

NProgress.configure({showSpinner: false})

const routes = [
    {
        path: '/login',
        name: 'Login',
        component: () => import('@/views/login/Login.vue'),
        meta: {title: '用户登录'}
    },
    {
        path: '/register',
        name: 'Register',
        component: () => import('@/views/login/Register.vue'),
        meta: {title: '企业注册'}
    },
    {
        path: '/404',
        name: '404',
        component: () => import('@/views/404.vue'),
        meta: {title: '页面不存在'}
    },
    // 1. PC端公共门户路由
    {
        path: '/',
        component: () => import('@/views/portal/layout/index.vue'),
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
                meta: {title: '政策中心'}
            },
            {
                path: 'news',
                name: 'PortalNews',
                component: () => import('@/views/portal/News.vue'),
                meta: {title: '要闻导读'}
            },
            {
                path: 'notice',
                name: 'PortalNotice',
                component: () => import('@/views/portal/Notice.vue'),
                meta: {title: '通知公告'}
            },
            {
                path: 'enterprise',
                name: 'PortalEnterprise',
                component: () => import('@/views/portal/Enterprise.vue'),
                meta: {title: '企业风采'}
            },
            {
                path: 'my-enterprise',
                name: 'PortalMyEnterprise',
                component: () => import('@/views/portal/MyEnterprise.vue'),
                meta: {title: '我的企业'}
            },
            {
                path: 'contact',
                name: 'PortalContact',
                component: () => import('@/views/portal/Contact.vue'),
                meta: {title: '联系我们'}
            }
        ]
    },
    // 2. PC端管理后台路由 (Layout)
    {
        path: '/index',
        name: 'Layout',
        component: () => import('@/views/Index.vue'),
        redirect: '/index/dashboard',
        meta: {requireAuth: true},
        children: [
            {
                path: 'dashboard',
                name: 'Dashboard',
                meta: {title: '工作台', icon: 'House'},
                component: () => import('@/views/dashboard/Index.vue')
            }
        ]
    },
    // 3. H5 移动端路由
    {
        path: '/m',
        name: 'MobileLayout',
        component: () => import('@/views/mobile/Layout.vue'),
        redirect: '/m/worker/list',
        meta: {requireAuth: true, isMobile: true},
        children: [
            // 工人端页面
            {
                path: 'worker/list',
                name: 'WorkerTaskList',
                component: () => import('@/views/mobile/worker/TaskList.vue'),
                meta: {title: '任务列表'}
            },
            {
                path: 'worker/detail/:id',
                name: 'WorkerTaskDetail',
                component: () => import('@/views/mobile/worker/TaskDetail.vue'),
                meta: {title: '任务处理'}
            },
            // 企业移动端页面
            {
                path: 'enterprise/apply',
                name: 'MobileApply',
                component: () => import('@/views/mobile/enterprise/Apply.vue'),
                meta: {title: '诉求提报'}
            },
            {
                path: 'enterprise/order',
                name: 'MobileMyOrder',
                component: () => import('@/views/mobile/enterprise/MyOrder.vue'),
                meta: {title: '我的诉求'}
            }
        ]
    }
]

const router = createRouter({
    history: createWebHistory(),
    routes,
    scrollBehavior: () => ({left: 0, top: 0})
})

export default router