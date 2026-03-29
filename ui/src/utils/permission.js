import router from '@/router/index'
import menuApi from '@/api/menu'
import {useUserStore} from '@/store/user'
import {resolvePath} from '@/utils/path'
import NProgress from 'nprogress'
import {ElMessage} from "element-plus";

// 1. 白名单配置
const whiteList = ['/login', '/register', '/404', '/home', '/policy', '/enterprise', '/news', '/contact', '/notice']
const modules = import.meta.glob('../views/**/*.vue')

// 移除 next 参数，改用 return 返回值
router.beforeEach(async (to, from) => {
    NProgress.start()

    const baseTitle = '经济开发区管理平台'
    document.title = to.meta.title ? `${to.meta.title} - ${baseTitle}` : baseTitle

    const userStore = useUserStore()
    const hasToken = localStorage.getItem('token')

    if (hasToken) {
        if (to.path === '/login') {
            return {path: '/home'}
        } else {
            // 2. 检查动态路由是否已加载
            if (userStore.routes.length === 0) {
                try {
                    const res = await menuApi.getRouters()
                    // 过滤并处理从后端拿到的 B 端管理后台路由
                    const rewriteRoutes = filterAsyncRouter(res.data)

                    // 设置 Dashboard 标题
                    const isEnterprise = userStore.roles.includes('ROLE_ENTERPRISE')
                    const dashboardTitle = isEnterprise ? '企业服务中心' : '管理工作台'
                    // 1. 先物理挂载 Dashboard 路由到 Layout 名下
                    router.addRoute('Layout', {
                        path: '/index/dashboard',
                        name: 'Dashboard',
                        component: () => import('@/views/dashboard/Index.vue'),
                        meta: {title: dashboardTitle, icon: 'House'}
                    })
                    const dashboardMenu = {
                        path: '/index/dashboard',
                        menuName: dashboardTitle,
                        icon: 'House',
                        children: []
                    }

                    userStore.setRoutes([dashboardMenu, ...rewriteRoutes])

                    // 3. 循环挂载 B 端动态路由到 'Layout' 下
                    rewriteRoutes.forEach(route => {
                        if (route.children && route.children.length > 0) {
                            route.children.forEach(child => {
                                if (child.isExternal !== 1 && !child.path.startsWith('http')) {
                                    const fullPath = resolvePath(route.path, child.path)
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

                    // 4. 追加 404 捕获（必须在所有路由加载完后再添加）
                    router.addRoute({
                        path: '/:pathMatch(.*)*',
                        redirect: '/404'
                    })

                    return {...to, replace: true}
                } catch (error) {
                    console.error('路由加载失败', error)
                    userStore.logout()
                    return `/login?redirect=${to.path}`
                }
            } else {
                // 5. 角色权限拦截 (针对移动端 /m 路径的简单拦截)
                if (to.path.startsWith('/m/worker') &&
                    !userStore.roles.includes('ROLE_WORKER') &&
                    !userStore.roles.includes('ROLE_STAFF') &&
                    !userStore.roles.includes('ROLE_ADMIN')) {
                    ElMessage.error('您没有权限访问工人端')
                    return {path: '/home'}
                }
                return true
            }
        }
    } else {
        // 6. 未登录处理
        if (whiteList.includes(to.path)) {
            return true
        } else {
            return `/login?redirect=${to.path}`
        }
    }
})

router.afterEach(() => {
    NProgress.done()
})

// 辅助函数：解析后端返回的组件路径
function filterAsyncRouter(asyncRouterMap) {
    return asyncRouterMap.filter(route => {
        if (route.path && !route.path.startsWith('/') && !route.path.startsWith('http')) {
            route.path = '/' + route.path
        }
        if (route.type === 'C') { // 菜单类型为 C (菜单)
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