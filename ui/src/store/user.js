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
            // 这里的 info 对应后端返回的 data.user 对象
            this.userInfo = {
                ...info,
                // 如果后端没给，默认给个大数值（999）表示最低权限
                topRoleSort: info.topRoleSort ?? 999
            }
            localStorage.setItem('userInfo', JSON.stringify(this.userInfo))
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
            this.userInfo = { id: null, topRoleSort: 999 } // 初始化结构
            this.roles = []
            this.permissions = []
            this.routes = []
            localStorage.removeItem('token')
            localStorage.removeItem('userInfo')
            localStorage.removeItem('roles')
            localStorage.removeItem('permissions')
        }
    }
})