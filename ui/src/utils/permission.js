import router from '@/router/index'
import menuApi from '@/api/menu'
import { useUserStore } from '@/store/user'

const whiteList = ['/login', '/register', '/404']
const modules = import.meta.glob('../views/**/*.vue')

router.beforeEach(async (to, from, next) => {
    const userStore = useUserStore()
    const hasToken = localStorage.getItem('token')

    if (hasToken) {
        if (to.path === '/login') {
            next({ path: '/' })
        } else {
            if (userStore.routes.length === 0) {
                try {
                    const res = await menuApi.getRouters()
                    const rewriteRoutes = filterAsyncRouter(res.data)

                    // 1. 核心修复：手动添加主控台到菜单显示列表
                    const dashboardMenu = {
                        path: '/index/dashboard',
                        menuName: '主控台',
                        icon: 'House',
                        children: []
                    }

                    // 2. 将主控台放在动态路由最前面，供侧边栏渲染
                    userStore.setRoutes([dashboardMenu, ...rewriteRoutes])

                    // 3. 动态注册后端路由到 Layout 名下
                    rewriteRoutes.forEach(route => {
                        if (route.children && route.children.length > 0) {
                            route.children.forEach(child => {
                                const fullPath = `${route.path}/${child.path}`.replace(/\/+/g, '/')
                                router.addRoute('Layout', {
                                    ...child,
                                    path: fullPath,
                                    meta: { title: child.menuName, icon: child.icon }
                                })
                            })
                        }
                    })

                    // 4. 最后添加通配符路由
                    router.addRoute({
                        path: '/:pathMatch(.*)*',
                        redirect: '/404'
                    })

                    // 确保路由注册完成后再跳转
                    next({ ...to, replace: true })
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
        if (whiteList.includes(to.path)) {
            next()
        } else {
            next(`/login`)
        }
    }
})

function filterAsyncRouter(asyncRouterMap) {
    return asyncRouterMap.filter(route => {
        if (!route.path.startsWith('/')) {
            route.path = '/' + route.path
        }
        if (route.children && route.children.length) {
            route.children.forEach(child => {
                if (child.component) {
                    child.component = loadView(child.component)
                }
            })
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