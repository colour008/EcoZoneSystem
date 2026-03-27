import request from './request'

const workOrderApi = {
    // ================== C端：企业自助接口 ==================

    /**
     * C端-提报工单
     * @param {Object} data - 工单信息 (title, content, type, images, contactPerson, contactPhone)
     */
    submit(data) {
        return request({
            url: '/work-order/apply',
            method: 'post',
            data
        })
    },

    /**
     * C端-获取“我的”工单分页列表
     * @param {Object} params - 分页与过滤参数 (pageNum, pageSize, status, type, title)
     */
    getMyPage(params) {
        return request({
            url: '/work-order/my/page',
            method: 'get',
            params
        })
    },

    /**
     * C端-评价工单 (状态为已办结 2 时可评价)
     * @param {Object} data - (id, score, commentText)
     */
    evaluate(data) {
        return request({
            url: '/work-order/evaluate',
            method: 'put',
            data
        })
    },

    // ================== B端：园区管控接口 ==================

    /**
     * B端-分页查询全园工单列表
     */
    page(params) {
        return request({
            url: '/work-order/page',
            method: 'get',
            params
        })
    },

    /**
     * B端-受理/分派工单
     * @param {Long} id - 工单ID
     * @param {Long} handlerId - 指派的处理人ID
     */
    accept(id, handlerId) {
        return request({
            url: `/work-order/accept/${id}`,
            method: 'put',
            params: { handlerId }
        })
    },

    /**
     * B端-处理反馈 (结案)
     * @param {Object} data - (id, remark)
     */
    process(data) {
        return request({
            url: '/work-order/process',
            method: 'put',
            data
        })
    }
}

export default workOrderApi