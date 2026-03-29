import request from './request'

const workOrderApi = {
    // ================== C端：企业自助接口 ==================

    /**
     * C端-提报工单
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
     */
    getMyPage(params) {
        return request({
            url: '/work-order/my/page',
            method: 'get',
            params
        })
    },

    /**
     * C端-评价工单
     */
    evaluate(data) {
        return request({
            url: '/work-order/evaluate',
            method: 'put',
            data
        })
    },

    // ================== B端：园区管控接口 (PC管理端) ==================

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
     * 核心修改：B端-委派/指派工单
     * @param {Object} data - { orderId: 1, workerId: 2 }
     */
    dispatch(data) {
        return request({
            url: '/work-order/dispatch',
            method: 'put',
            data
        })
    },

    // ================== H5端：工人执行接口 (移动端) ==================

    /**
     * 新增：H5端-获取分配给当前工人的工单
     */
    getWorkerTaskPage(params) {
        return request({
            url: '/work-order/worker/page',
            method: 'get',
            params
        })
    },

    /**
     * B端/H5端-处理反馈 (工人提交凭证结案)
     * @param {Object} data - (id, remark, images)
     */
    process(data) {
        return request({
            url: '/work-order/process',
            method: 'put',
            data
        })
    },

    /**
     * B端/H5端根据ID获取详情
     * @param id
     * @returns {*}
     */
    getById(id) {
        return request({
            url: `/work-order/${id}`, // 这里的路径根据你后端的实际详情接口修改
            method: 'get'
        })
    },

    /**
     * 新增：获取工单状态统计数量
     * 用于通知中心展示待受理和处理中的数量
     */
    getStatistics() {
        return request({
            url: '/work-order/statistics',
            method: 'get'
        })
    }
}

export default workOrderApi