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
     * @param {Object} params { pageNum, pageSize, applicantName, type, status, ... }
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
     * @param {Number|String} id 留言ID
     * @param {Number|String} handlerId 处理人(用户)ID
     */
    assignHandler(id, handlerId) {
        return request({
            url: `/inquiry/assign/${id}/${handlerId}`,
            method: 'put'
        })
    },

    /**
     * B端-填写跟进记录
     * @param {Object} data { id, result, status }
     * 注意：后端接口未加 @RequestBody，参数通过 Query String 传递
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
     * @param {Number|String} id 留言ID
     */
    convertToEnterprise(id) {
        return request({
            url: `/inquiry/convert/${id}`,
            method: 'post'
        })
    }
}

export default inquiryApi