import request from './request'

const inquiryApi = {
    // ================== C端：公开入口 (游客可见) ==================

    /**
     * C端-提交意向留言
     * @param {Object} data { applicantName, companyName, type, contactPhone, remark }
     */
    submitPublicInquiry(data) {
        return request({
            url: '/inquiry/public/submit',
            method: 'post',
            data
        })
    },

    // ================== B端：园区管理接口 ==================

    /**
     * B端-获取意向留言分页列表
     */
    getAdminPage(params) {
        return request({
            url: '/inquiry/page',
            method: 'get',
            params
        })
    },

    /**
     * B端-分配跟进人员
     */
    assignHandler(id, handlerId) {
        return request({
            url: `/inquiry/assign/${id}/${handlerId}`,
            method: 'put'
        })
    },

    /**
     * B端-填写跟进记录 (会触发 biz_inquiry_record 插入)
     * 参数通过 query 传递以匹配后端接收方式
     */
    recordFollowUp(data) {
        return request({
            url: '/inquiry/record',
            method: 'put',
            params: {
                id: data.id,
                result: data.result,
                status: data.status
            }
        })
    },

    /**
     * B端-一键转入驻
     */
    convertToEnterprise(id) {
        return request({
            url: `/inquiry/convert/${id}`,
            method: 'post'
        })
    },

    /**
     * B端-删除无效留言 (逻辑删除)
     */
    deleteInquiry(id) {
        return request({
            url: `/inquiry/${id}`,
            method: 'delete'
        })
    },

    /**
     * B端-获取某条留言的所有历史跟进记录
     */
    getFollowRecords(inquiryId) {
        return request({
            url: `/inquiry/records/${inquiryId}`,
            method: 'get'
        })
    }
}

export default inquiryApi