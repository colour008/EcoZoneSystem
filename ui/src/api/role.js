import request from './request'

const roleApi = {
    /**
     * 分页查询角色
     * @param params
     * @returns {*}
     */
    page(params) {
        return request({url: '/role/page', method: 'get', params})
    },

    /**
     * 增加角色
     * @param data
     * @returns {*}
     */
    add(data) {
        return request({url: '/role/add', method: 'post', data})
    },

    /**
     * 修改角色
     * @param id
     * @param data
     * @returns {*}
     */
    update(id, data) {
        return request({url: `/role/${id}`, method: 'put', data})
    },

    /**
     * 删除角色
     * @param ids
     * @returns {*}
     */
    delete(ids) {
        return request({url: '/role/delete', method: 'delete', data: ids})
    },

    /**
     * 获取权限树
     * @param roleId
     * @returns {*}
     */
    getRoleMenus(roleId) {
        return request({url: `/role/${roleId}/menus`, method: 'get'})
    },

    /**
     * 保存权限
     * @param roleId
     * @param menuIds
     * @returns {*}
     */
    saveRoleMenus(roleId, menuIds) {
        return request({url: `/role/${roleId}/menus`, method: 'post', data: menuIds})
    }
}
export default roleApi