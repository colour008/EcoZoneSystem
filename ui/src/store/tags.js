import {defineStore} from 'pinia'
import router from '@/router' // 核心修复：直接引入路由实例，避免 useRouter 外部调用报错

export const useTagsStore = defineStore('tags', {
    state: () => ({
        // 保存已打开的标签页（完整路由对象，包含name/title/path等核心字段）
        visitedViews: [
            // 初始化强制添加主控台，确保始终存在
            {title: '主控台', name: 'Dashboard', path: '/index/dashboard'}
        ]
    }),
    actions: {
        /**
         * 添加标签页（核心修复：去重+强制保留主控台）
         */
        addView(view) {
            // 过滤无效路由/登录页
            if (view.path === '/login' || view.path === '/') return

            // 主控台特殊处理：始终保留，不重复添加
            const dashboardPath = '/index/dashboard'
            if (view.path === dashboardPath) return

            // 用路由name+path双重去重，避免重复添加
            const isExist = this.visitedViews.some(
                v => v.path === view.path && v.name === view.name
            )
            if (isExist) return

            // 存储完整路由对象
            this.visitedViews.push({
                title: view.meta?.title || '未知页面',
                name: view.name,
                path: view.path,
                icon: view.meta?.icon || 'Document' // 存储图标，若无则给默认值
            })
        },

        /**
         * 删除标签页（核心修复：禁止删除主控台、修复跳转逻辑）
         */
        delView(viewPath) {
            const dashboardPath = '/index/dashboard'
            // 禁止删除主控台
            if (viewPath === dashboardPath) return

            const visitedViews = this.visitedViews
            const index = visitedViews.findIndex(v => v.path === viewPath)
            if (index === -1) return

            // 删除标签
            visitedViews.splice(index, 1)
        },

        /**
         * 清空所有标签页（保留主控台）
         */
        delAllViews() {
            const dashboardPath = '/index/dashboard'
            // 只保留主控台
            this.visitedViews = this.visitedViews.filter(v => v.path === dashboardPath)
            router.push(dashboardPath) // 使用导入的 router 实例进行跳转
        }
    }
})