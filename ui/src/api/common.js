import request from './request'

/**
 * 业务模块通用接口模版
 * 将 {module} 替换为你的业务路径，如 'user', 'role', 'order'
 */
const createApi = (module) => ({
    // 分页查询
    page(params) {
        return request({ url: `/${module}/page`, method: 'get', params })
    },
    // 新增
    add(data) {
        return request({ url: `/${module}/add`, method: 'post', data })
    },
    // 修改
    update(id, data) {
        return request({ url: `/${module}/${id}`, method: 'put', data })
    },
    // 删除（支持批量）
    delete(ids) {
        return request({ url: `/${module}/delete`, method: 'delete', data: ids })
    },
    // 状态切换 (特定业务接口可在此扩展)
    changeStatus(id, status) {
        return request({ url: `/${module}/${id}/status/${status}`, method: 'patch' })
    }
})

export default createApi