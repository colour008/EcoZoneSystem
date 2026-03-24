import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/store/user'
import NProgress from 'nprogress'
import 'nprogress/nprogress.css'

// 配置 NProgress（去掉加载圈，只留顶线）
NProgress.configure({ showSpinner: false })

const routes = [
    {
        path: '/login',
        name: 'Login',
        component: () => import('@/views/login/Login.vue'),
        meta: { title: '用户登录' }
    },
    {
        path: '/register',
        name: 'Register',
        component: () => import('@/views/login/Register.vue'),
        meta: { title: '企业注册' }
    },
    {
        path: '/404',
        name: '404',
        component: () => import('@/views/404.vue'),
        meta: { title: '页面不存在' }
    },
    // 1. C端公共门户路由 (上下布局)
    {
        path: '/',
        component: () => import('@/views/portal/layout/index.vue'),
        redirect: '/home',
        children: [
            {
                path: 'home',
                name: 'PortalHome',
                component: () => import('@/views/portal/Home.vue'),
                meta: { title: '园区首页' }
            },
            {
                path: 'policy',
                name: 'PortalPolicy',
                component: () => import('@/views/portal/Policy.vue'),
                meta: { title: '政策法规' }
            },
            {
                path: 'news',
                name: 'PortalNews',
                component: () => import('@/views/portal/News.vue'),
                meta: { title: '园区动态' }
            },
            {
                path: 'enterprise',
                name: 'PortalEnterprise',
                component: () => import('@/views/portal/Enterprise.vue'),
                meta: { title: '企业风采'}
            },
            {
                path: 'contact',
                name: 'PortalContact',
                component: () => import('@/views/portal/Contact.vue'),
                meta: { title: '联系我们'}
            }
        ]
    },
    // 2. B/C端管理后台路由 (侧边栏布局)
    {
        path: '/index',
        name: 'Layout',
        component: () => import('@/views/Index.vue'),
        redirect: '/index/dashboard',
        meta: { requireAuth: true }, // 整个后台目录都需要登录
        children: [
            {
                path: 'dashboard',
                name: 'Dashboard',
                meta: { title: '工作台', icon: 'House' },
                component: () => import('@/views/dashboard/Index.vue')
            }
        ]
    }
]

const router = createRouter({
    history: createWebHistory(),
    routes,
    // 切换页面自动回到顶部
    scrollBehavior: () => ({ left: 0, top: 0 })
})

// --- 全局前置守卫 ---
router.beforeEach((to, from, next) => {
    NProgress.start()

    // 1. 动态设置页面 Title
    const baseTitle = '经济开发区管理平台'
    document.title = to.meta.title ? `${to.meta.title} - ${baseTitle}` : baseTitle

    // 2. 权限校验
    const userStore = useUserStore()
    const isAuthenticated = !!userStore.token

    if (to.matched.some(record => record.meta.requireAuth)) {
        if (!isAuthenticated) {
            // 未登录，拦截并跳转到登录页，带上当前路径以便登录后跳回
            next({
                path: '/login',
                query: { redirect: to.fullPath }
            })
        } else {
            next()
        }
    } else {
        // 不需要权限的页面，如果是已登录状态去 login，直接重定向到首页
        if (to.path === '/login' && isAuthenticated) {
            next({ path: '/' })
        } else {
            next()
        }
    }
})

// --- 全局后置守卫 ---
router.afterEach(() => {
    NProgress.done()
})

export default router