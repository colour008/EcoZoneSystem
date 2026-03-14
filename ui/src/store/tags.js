import { defineStore, acceptHMRUpdate} from 'pinia'
import router from '@/router'

export const useTagsStore = defineStore('tags', {
    state: () => ({
        // 优化：尝试从本地存储读取，防止刷新或热更新丢失标签
        visitedViews: JSON.parse(localStorage.getItem('tags_views')) || [
            { title: '主控台', name: 'Dashboard', path: '/index/dashboard', icon: 'House' }
        ]
    }),
    actions: {
        /**
         * 持久化存储（内部调用）
         */
        saveToLocal() {
            localStorage.setItem('tags_views', JSON.stringify(this.visitedViews))
        },

        /**
         * 添加标签页
         */
        addView(view) {
            if (!view.name || view.path === '/login' || view.path === '/') return

            const isExist = this.visitedViews.some(v => v.path === view.path)

            if (!isExist) {
                this.visitedViews.push({
                    title: view.meta?.title || '未知页面',
                    name: view.name,
                    path: view.path,
                    icon: view.meta?.icon || 'Document'
                })
                this.saveToLocal()
            }
        },

        /**
         * 删除标签页（增强跳转逻辑）
         * @param {String} viewPath 要删除的路径
         * @param {String} currentPath 当前浏览器地址栏路径
         */
        delView(viewPath, currentPath) {
            const dashboardPath = '/index/dashboard'
            if (viewPath === dashboardPath) return

            const index = this.visitedViews.findIndex(v => v.path === viewPath)
            if (index === -1) return

            // 1. 执行删除
            this.visitedViews.splice(index, 1)
            this.saveToLocal()

            // 2. 核心优化：如果删除的是当前正在访问的页面，自动跳转
            if (viewPath === currentPath) {
                // 尝试跳转到上一个标签，如果没有则回退到主控台
                const nextView = this.visitedViews[index - 1] || this.visitedViews[index] || { path: dashboardPath }
                router.push(nextView.path)
            }
        },

        /**
         * 删除其他标签（右键菜单常用功能）
         */
        delOthersViews(currentView) {
            const dashboardPath = '/index/dashboard'
            this.visitedViews = this.visitedViews.filter(v => {
                return v.path === dashboardPath || v.path === currentView.path
            })
            this.saveToLocal()
        },

        /**
         * 清空所有标签
         */
        delAllViews() {
            const dashboardPath = '/index/dashboard'
            this.visitedViews = [{ title: '主控台', name: 'Dashboard', path: dashboardPath, icon: 'House' }]
            this.saveToLocal()
            if (router.currentRoute.value.path !== dashboardPath) {
                router.push(dashboardPath)
            }
        }
    }
})

// 核心优化：手动处理 HMR 模块热替换
if (import.meta.hot) {
    import.meta.hot.accept(acceptHMRUpdate(useTagsStore, import.meta.hot))
}