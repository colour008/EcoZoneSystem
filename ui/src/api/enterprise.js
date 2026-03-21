import request from './request'

const enterpriseApi = {
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
     * 提交入驻申请
     */
    apply(data) {
        return request({
            url: '/enterprise/apply',
            method: 'post',
            data
        })
    },

    /**
     * 审核入驻申请
     * @param id 企业ID
     * @param status 1:通过, 2:驳回
     */
    audit(id, status) {
        return request({
            url: `/enterprise/audit/${id}`,
            method: 'put',
            params: { status }
        })
    }
}

export default enterpriseApi