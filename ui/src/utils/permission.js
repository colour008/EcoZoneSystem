import {resolvePath} from '@/utils/path'
import router from '@/router/index'
import menuApi from '@/api/menu'
import {useUserStore} from '@/store/user'

// 1. 【修改】将门户的路径加入白名单，允许游客访问
const whiteList = ['/login', '/register', '/404', '/home', '/policy']
const modules = import.meta.glob('../views/**/*.vue')

router.beforeEach(async (to, from, next) => {
    const userStore = useUserStore()
    const hasToken = localStorage.getItem('token')

    if (hasToken) {
        if (to.path === '/login') {
            // 已登录访问 login，直接跳转到控制台
            next({path: '/index/dashboard'})
        } else {
            if (userStore.routes.length === 0) {
                try {
                    const res = await menuApi.getRouters()
                    const rewriteRoutes = filterAsyncRouter(res.data)

                    // 2. 【增强】根据角色动态判断工作台的名称
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

                    router.addRoute({
                        path: '/:pathMatch(.*)*',
                        redirect: '/404'
                    })

                    next({...to, replace: true})
                } catch (error) {
                    console.error('路由加载失败', error)
                    userStore.logout()
                    next(`/login`)
                }
            } else {
                next()
            }
        }
    } else {
        // 3. 【修改】判断白名单的逻辑
        if (whiteList.includes(to.path) || to.path === '/') {
            next()
        } else {
            // 未登录且不在白名单，跳转到登录页，并记录原本想去的路径
            next(`/login?redirect=${to.path}`)
        }
    }
})

function filterAsyncRouter(asyncRouterMap) {
    // ... 保持原有逻辑不变
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
    // ... 保持原有逻辑不变
    let path = view.replace(/^\//, '').replace(/\.vue$/, '')
    const filePath = `../views/${path}.vue`
    if (modules[filePath]) {
        return modules[filePath]
    } else {
        console.error(`🔴 找不到组件: ${filePath}`)
        return () => import('@/views/404.vue')
    }
}