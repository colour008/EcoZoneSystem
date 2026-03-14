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
     */
    update(data) {
        return request({
            url: '/user/update',
            method: 'put',
            data
        })
    },

    /**
     * 删除用户
     * @param {Long} id 用户ID
     */
    delete(id) {
        return request({
            url: `/user/delete/${id}`,
            method: 'delete'
        })
    }
}

export default userApi