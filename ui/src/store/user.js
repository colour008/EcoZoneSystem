import { defineStore } from 'pinia'

export const useUserStore = defineStore('user', {
    state: () => ({
        token: localStorage.getItem('token') || '',
        // 刷新页面时，尝试从本地恢复用户信息
        userInfo: JSON.parse(localStorage.getItem('userInfo') || '{}')
    }),
    actions: {
        setToken(token) {
            this.token = token
            localStorage.setItem('token', token)
        },
        setUserInfo(info) {
            this.userInfo = info
            // 将对象转为字符串持久化
            localStorage.setItem('userInfo', JSON.stringify(info))
        },
        logout() {
            this.token = ''
            this.userInfo = {}
            localStorage.removeItem('token')
            localStorage.removeItem('userInfo') // 记得清理
        }
    }
})