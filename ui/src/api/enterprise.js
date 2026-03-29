import request from './request'

const enterpriseApi = {
    // ================== C端自助接口 ==================

    /**
     * 提交入驻申请 (初次提交或驳回后重新提交)
     */
    apply(data) {
        return request({
            url: '/enterprise/apply',
            method: 'post',
            data
        })
    },

    /**
     * 获取当前登录用户的企业入驻信息
     */
    getMyEnterprise() {
        return request({
            url: '/enterprise/mine',
            method: 'get'
        })
    },

    /**
     * C端用户修改自己的企业联系方式/信息
     */
    updateMyEnterprise(data) {
        return request({
            url: '/enterprise/mine/update',
            method: 'put',
            data
        })
    },

    /**
     * C端提交迁出申请 (新增)
     * @param {String} reason - 迁出理由
     */
    applyMoveOut(reason) {
        return request({
            url: '/enterprise/mine/move-out/apply',
            method: 'put',
            params: {reason}
        })
    },

    /**
     * 企业风采
     * @param params
     * @returns {*}
     */
    getEnterpriseShowPage(params) {
        return request({
            url: '/enterprise/show/page',
            method: 'get',
            params: params
        })
    },

    // ================== B端管控接口 ==================

    /**
     * 分页查询企业列表
     */
    page(params) {
        return request({
            url: '/enterprise/page',
            method: 'get',
            params
        })
    },

    /**
     * 获取全量企业列表 (不分页)
     */
    listAll() {
        return request({
            url: '/enterprise/list',
            method: 'get'
        })
    },

    /**
     * 获取企业详情
     */
    getDetail(id) {
        return request({
            url: `/enterprise/${id}`,
            method: 'get'
        })
    },

    /**
     * 审核入驻申请 (0 -> 1或2)
     */
    audit(id, status, auditOpinion) {
        return request({
            url: `/enterprise/audit/${id}`,
            method: 'put',
            params: {
                status,
                auditOpinion
            }
        })
    },

    /**
     * 审核迁出申请 (4 -> 3或1) (修正)
     * @param id 企业ID
     * @param status 3:同意迁出, 1:驳回申请恢复正常
     * @param opinion 审核意见
     */
    auditMoveOut(id, status, opinion) {
        return request({
            url: `/enterprise/audit-move-out/${id}`,
            method: 'put',
            params: {
                status,
                opinion
            }
        })
    },

    /**
     * 获取审核历史流水记录
     */
    getAuditHistory(id) {
        return request({
            url: `/enterprise/audit/history/${id}`,
            method: 'get'
        })
    },

    /**
     * 管理员修改企业信息
     */
    update(data) {
        return request({
            url: '/enterprise/update',
            method: 'put',
            data
        })
    },

    /**
     * 删除企业记录
     */
    delete(ids) {
        return request({
            url: `/enterprise/${ids}`,
            method: 'delete'
        })
    },

    /**
     * 获取待审核数量 (包含 status 0 和 4)
     */
    getPendingCount() {
        return request({
            url: '/enterprise/pending/count',
            method: 'get'
        })
    },

    /**
     * C端-获取我的申请提醒数量 (统计状态 0, 2, 4)
     */
    getMyNoticeCount() {
        return request({
            url: '/enterprise/mine/notice/count',
            method: 'get'
        })
    }
}

export default enterpriseApi