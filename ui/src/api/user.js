import request from './request'

/**
 * 用户管理模块接口集成
 */
const userApi = {
    /**
     * 获取全部用户列表
     */
    list() {
        return request({
            url: '/user/list',
            method: 'get'
        })
    },

    /**
     * 分页查询用户列表
     * @param {Object} params { pageNum, pageSize, username, ... }
     */
    page(params) {
        return request({
            url: '/user/page',
            method: 'get',
            params
        })
    },

    /**
     * 新增用户
     * @param {Object} data 用户实体信息
     */
    add(data) {
        return request({
            url: '/user/add',
            method: 'post',
            data
        })
    },

    /**
     * 更新用户
     * @param {Long} id 用户ID
     * @param {Object} data 更新的数据
     */
    update(id, data) {
        return request({
            url: `/user/${id}`,
            method: 'put',
            data
        })
    },

    /**
     * 删除用户
     * @param {Array} ids ID数组，例如 [1] 或 [1, 2, 3]
     */
    delete(ids) {
        return request({
            url: '/user/delete',
            method: 'delete',
            data: ids
        })
    },

    /**
     * 重置密码
     * @param {Long} id 用户ID
     */
    resetPassword(id) {
        return request({
            url: `/user/${id}/password/reset`,
            method: 'patch'
        })
    }
}

export default userApi