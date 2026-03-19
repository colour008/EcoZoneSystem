import { resolvePath } from '@/utils/path' // 根据你的路径别名修改
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

                    // 3. 动态注册后端路由
                    rewriteRoutes.forEach(route => {
                        // 如果该目录有子菜单
                        if (route.children && route.children.length > 0) {
                            route.children.forEach(child => {
                                // 排除外链：不注册到本地路由表
                                if (child.isExternal !== 1 && !child.path.startsWith('http')) {
                                    const fullPath = resolvePath(route.path, child.path)

                                    router.addRoute('Layout', {
                                        path: fullPath,
                                        name: fullPath.replace(/\//g, '_'),
                                        component: child.component,
                                        meta: {
                                            // 【核心修复】确保这里有 title，面包屑才能通过 route.matched 拿到它
                                            title: child.menuName,
                                            icon: child.icon,
                                            // 可选：存一下父级标题，方便面包屑做特殊处理
                                            parentTitle: route.menuName
                                        }
                                    })
                                }
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
        // 1. 处理路径规范
        if (route.path && !route.path.startsWith('/') && !route.path.startsWith('http')) {
            route.path = '/' + route.path
        }

        // 2. 如果是目录(M)，通常不需要 component 或者指向一个 ParentView
        // 如果是菜单(C)，加载真实组件
        if (route.type === 'C') {
            if (route.isExternal === 1) {
                // 外链不需要加载组件
                route.component = null
            } else if (route.component) {
                route.component = loadView(route.component)
            }
        }

        // 3. 递归处理子菜单
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