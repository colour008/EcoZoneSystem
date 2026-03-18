import request from './request'

/**
 * 菜单权限相关接口
 */
const menuApi = {
    /**
     * 获取全量菜单树列表
     * 用于：菜单管理页面的树形表格展示
     */
    list() {
        return request({
            url: '/menu/list',
            method: 'get'
        })
    },

    /**
     * 获取当前登录用户的动态路由树
     * 用于：permission.js 渲染侧边栏
     */
    getRouters() {
        return request({
            url: '/menu/getRouters',
            method: 'get'
        })
    },

    /**
     * 新增菜单
     */
    add(data) {
        return request({
            url: '/menu/add',
            method: 'post',
            data
        })
    },

    /**
     * 修改菜单
     */
    update(id, data) {
        return request({
            url: `/menu/${id}`,
            method: 'put',
            data
        })
    },

    /**
     * 删除菜单
     */
    delete(id) {
        return request({
            url: `/menu/${id}`,
            method: 'delete'
        })
    },
}

export default menuApi