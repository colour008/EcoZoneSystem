// src/store/tags.js
import { defineStore } from 'pinia'

export const useTagsStore = defineStore('tags', {
    state: () => ({
        visitedViews: [] // 保存已打开的标签页对象
    }),
    actions: {
        addView(view) {
            // 如果标签页已经存在，则不重复添加
            if (this.visitedViews.some(v => v.path === view.path)) return
            this.visitedViews.push(Object.assign({}, view))
        },
        delView(viewPath) {
            // 删除指定路径的标签页
            const index = this.visitedViews.findIndex(v => v.path === viewPath)
            if (index > -1) {
                this.visitedViews.splice(index, 1)
            }
        }
    }
})