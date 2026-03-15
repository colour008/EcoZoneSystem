import {createRouter, createWebHistory} from 'vue-router'
import {useUserStore} from '@/store/user'

const routes = [
    {
        path: '/login',
        name: 'Login',
        component: () => import('@/views/login/Login.vue')
    },
    {
        path: '/register', // 添加注册路由
        name: 'Register',
        component: () => import('@/views/login/Register.vue')
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
                meta: {title: '主控台', icon: 'House'},
                component: () => import('@/views/dashboard/Index.vue')
            },
            {
                path: 'user/list',
                name: 'UserList',
                meta: {title: '用户列表', icon: 'User'},
                component: () => import('@/views/user/List.vue')
            },
            {
                path: 'access/list',
                name: 'AccessList',
                meta: {title: '权限列表', icon: 'Key'},
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
    const userStore = useUserStore()

    // 白名单
    const whiteList = ['/login', '/register', '/404']

    // 登录、注册、404页面拦截逻辑
    if (!whiteList.includes(to.path) && !userStore.token) {
        return '/login'
    }

    return true
})

export default router