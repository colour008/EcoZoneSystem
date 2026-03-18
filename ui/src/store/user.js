import { defineStore } from 'pinia'

export const useUserStore = defineStore('user', {
    state: () => ({
        token: localStorage.getItem('token') || '',
        // 1. 增加 roles 和 permissions 的初始化
        userInfo: JSON.parse(localStorage.getItem('userInfo') || '{}'),
        roles: JSON.parse(localStorage.getItem('roles') || '[]'),
        permissions: JSON.parse(localStorage.getItem('permissions') || '[]'),
        // 动态路由树，仅存内存中，刷新页面会重新通过 getRouters 获取
        routes: []
    }),
    actions: {
        setToken(token) {
            this.token = token
            localStorage.setItem('token', token)
        },
        setUserInfo(info) {
            this.userInfo = info
            localStorage.setItem('userInfo', JSON.stringify(info))
        },
        // 2. 实现 setRoles
        setRoles(roles) {
            this.roles = roles || []
            localStorage.setItem('roles', JSON.stringify(this.roles))
        },
        // 3. 实现 setPermissions
        setPermissions(permissions) {
            this.permissions = permissions || []
            localStorage.setItem('permissions', JSON.stringify(this.permissions))
        },
        // 存储转换后的路由对象
        setRoutes(routes) {
            this.routes = routes
        },
        logout() {
            this.token = ''
            this.userInfo = {}
            this.roles = []
            this.permissions = []
            this.routes = [] // 清理路由
            localStorage.removeItem('token')
            localStorage.removeItem('userInfo')
            // 4. 记得清理新增的存储
            localStorage.removeItem('roles')
            localStorage.removeItem('permissions')
        }
    }
})