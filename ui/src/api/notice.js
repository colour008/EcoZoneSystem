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
     * B端-批量逻辑删除公告
     * @param {Array} ids ID数组，如 [1, 2]
     */
    deleteNotice(ids) {
        // 后端接收 @PathVariable List<Long> ids，Spring 会自动解析 1,2,3 这种格式
        return request({
            url: `/notice/${ids.join(',')}`,
            method: 'delete'
        })
    },

    /**
     * B端-获取激活用户列表
     * @returns {*}
     */
    getActiveUsers() {
        return request({
            url: '/notice/active-users',
            method: 'get'
        })
    },

    /**
     * 通用-获取公告详情（C端调用会自动标记已读）
     */
    getDetail(id) {
        return request({
            url: `/notice/detail/${id}`,
            method: 'get'
        })
    },

    // ================== C 端：门户/移动端接口 ==================

    /**
     * C端-获取公开列表（政策/动态/公告）
     * @param {Object} params { type: 1, pageNum: 1, pageSize: 10, title: '' }
     */
    getPublicList(params) {
        return request({
            url: '/notice/list/public',
            method: 'get',
            params
        })
    },

    /**
     * C端-获取当前用户未读消息数
     */
    getUnreadCount() {
        return request({
            url: '/notice/unread-count',
            method: 'get'
        })
    },
}

export default noticeApi