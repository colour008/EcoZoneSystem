import request from './request'

const noticeApi = {
    // ================== B端：园区管理接口 ==================

    /**
     * B端-新增或修改公告(草稿)
     */
    save(data) {
        return request({
            url: '/notice/save',
            method: 'post',
            data
        })
    },

    /**
     * B端-发布公告
     */
    publish(id) {
        return request({
            url: `/notice/publish/${id}`,
            method: 'put'
        })
    },

    /**
     * B端-撤回公告
     */
    recall(id) {
        return request({
            url: `/notice/recall/${id}`,
            method: 'put'
        })
    },

    /**
     * B端-归档公告
     */
    archive(id) {
        return request({
            url: `/notice/archive/${id}`,
            method: 'put'
        })
    },

    /**
     * B端-获取公告列表
     */
    page(params) {
        return request({
            url: '/notice/page',
            method: 'get',
            params
        })
    },

    /**
     * 获取激活用户列表
     * @returns {*}
     */
    getActiveUsers() {
        return request({
            url: '/notice/active-users',
            method: 'get'
        })
    },

    /**
     * 通用-获取公告详情
     */
    getDetail(id) {
        return request({
            url: `/notice/detail/${id}`,
            method: 'get'
        })
    }
}

export default noticeApi