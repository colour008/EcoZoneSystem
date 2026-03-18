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
     * 新增菜单
     * @param {Object} data 菜单信息 (parentId, menuName, type, perms, path, component等)
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
     * @param {Number} id 菜单ID
     * @param {Object} data 修改后的菜单数据
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
     * @param {Number} id 菜单ID
     * 注意：后端会校验是否存在子菜单或是否已分配给角色
     */
    delete(id) {
        return request({
            url: `/menu/${id}`,
            method: 'delete'
        })
    },

    // 获取当前用户的路由
    getRouters() {
        return request({
            url: '/menu/getRouters',
            method: 'get'
        })
    }
}

export default menuApi