import {resolvePath} from '@/utils/path'
import router from '@/router/index'
import menuApi from '@/api/menu'
import {useUserStore} from '@/store/user'
import NProgress from 'nprogress'

// 1. 门户路径白名单，允许游客访问
const whiteList = ['/login', '/register', '/404', '/home', '/policy', '/enterprise', '/news', '/contact']
const modules = import.meta.glob('../views/**/*.vue')

// --- 全局前置守卫 ---
router.beforeEach(async (to, from, next) => {
    NProgress.start()

    // 动态设置页面 Title
    const baseTitle = '经济开发区管理平台'
    document.title = to.meta.title ? `${to.meta.title} - ${baseTitle}` : baseTitle

    const userStore = useUserStore()
    const hasToken = localStorage.getItem('token')

    if (hasToken) {
        // 如果已登录用户访问登录页，统一跳转到门户首页，不再区分角色
        if (to.path === '/login') {
            next({path: '/home'})
        } else {
            // 判断是否已经加载过动态路由
            if (userStore.routes.length === 0) {
                try {
                    const res = await menuApi.getRouters()
                    const rewriteRoutes = filterAsyncRouter(res.data)

                    // 根据角色动态判断工作台的名称
                    const isEnterprise = userStore.roles.includes('ROLE_ENTERPRISE')
                    const dashboardTitle = isEnterprise ? '企业工作台' : '系统主控台'

                    const dashboardMenu = {
                        path: '/index/dashboard',
                        menuName: dashboardTitle,
                        icon: 'House',
                        children: []
                    }

                    userStore.setRoutes([dashboardMenu, ...rewriteRoutes])

                    rewriteRoutes.forEach(route => {
                        if (route.children && route.children.length > 0) {
                            route.children.forEach(child => {
                                if (child.isExternal !== 1 && !child.path.startsWith('http')) {
                                    const fullPath = resolvePath(route.path, child.path)
                                    // B端和C端登录后的私有页面，都挂载到 'Layout' 下
                                    router.addRoute('Layout', {
                                        path: fullPath,
                                        name: fullPath.replace(/\//g, '_'),
                                        component: child.component,
                                        meta: {
                                            title: child.menuName,
                                            icon: child.icon,
                                            parentTitle: route.menuName
                                        }
                                    })
                                }
                            })
                        }
                    })

                    // 动态路由加载完毕后，追加 404 捕获
                    router.addRoute({
                        path: '/:pathMatch(.*)*',
                        redirect: '/404'
                    })

                    // hack方法 确保addRoute已完成
                    next({...to, replace: true})
                } catch (error) {
                    console.error('路由加载失败', error)
                    userStore.logout()
                    next(`/login`)
                }
            } else {
                // 路由已存在，直接放行
                next()
            }
        }
    } else {
        // 未登录状态
        if (whiteList.includes(to.path) || to.path === '/') {
            // 在白名单内，直接放行
            next()
        } else {
            // 不在白名单，跳转到登录页，并记录原本想去的路径
            next(`/login?redirect=${to.path}`)
        }
    }
})

// --- 全局后置守卫 ---
router.afterEach(() => {
    NProgress.done()
})

function filterAsyncRouter(asyncRouterMap) {
    return asyncRouterMap.filter(route => {
        if (route.path && !route.path.startsWith('/') && !route.path.startsWith('http')) {
            route.path = '/' + route.path
        }
        if (route.type === 'C') {
            if (route.isExternal === 1) {
                route.component = null
            } else if (route.component) {
                route.component = loadView(route.component)
            }
        }
        if (route.children && route.children.length) {
            route.children = filterAsyncRouter(route.children)
        }
        return true
    })
}

function loadView(view) {
    let path = view.replace(/^\//, '').replace(/\.vue$/, '')
    const filePath = `../views/${path}.vue`
    if (modules[filePath]) {
        return modules[filePath]
    } else {
        console.error(`🔴 找不到组件: ${filePath}`)
        return () => import('@/views/404.vue')
    }
}