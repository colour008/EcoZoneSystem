import request from './request'

/**
 * 获取全部用户列表
 * @returns {Promise}
 */
export function getUserList() {
    return request({
        url: '/user/list',
        method: 'get'
    })
}

/**
 * 分页查询用户列表
 * @param {Object} params 分页+搜索参数
 * @returns {Promise}
 */
export function getUserPage(params) {
    return request({
        url: '/user/page',
        method: 'get',
        params
    })
}