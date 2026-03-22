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
     * 审核入驻申请
     * @param id 企业ID
     * @param status 1:通过, 2:驳回
     * @param auditOpinion 审核意见 (驳回时必填)
     */
    audit(id, status, auditOpinion) {
        return request({
            url: `/enterprise/audit/${id}`,
            method: 'put',
            params: { status, auditOpinion }
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
     * 办理企业迁出
     */
    moveOut(id) {
        return request({
            url: `/enterprise/move-out/${id}`,
            method: 'put'
        })
    },

    /**
     * 删除企业记录
     * 注意：后端定义为 @DeleteMapping("/{id}")，故传参放在 URL 中
     */
    delete(id) {
        return request({
            url: `/enterprise/${id}`,
            method: 'delete'
        })
    }
}

export default enterpriseApi