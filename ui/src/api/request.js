import axios from 'axios'
import {ElMessage} from "element-plus";

const request = axios.create({
    baseURL: '/api',
    timeout: 10000
})

// 请求拦截
request.interceptors.request.use(config => {
    const token = localStorage.getItem('token')
    if (token) {
        config.headers.Authorization = 'Bearer ' + token
    }
    return config
})

// 响应拦截
request.interceptors.response.use(
    (response) => {
        const res = response.data // 这里的 res 就是后端的 Result 对象

        // 1. 检查通用状态码
        if (res.code !== 200) {
            // 系统级错误（如 500, 401, 403）
            ElMessage.error(res.msg || '系统错误')
            return Promise.reject(new Error(res.msg || 'Error'))
        }

        // 2. 检查业务状态码 (bizCode)
        // 假设 bizCode !== 0 代表业务逻辑错误（如：余额不足、号源不足）
        if (res.bizCode !== 0) {
            ElMessage.warning(res.msg || '业务处理异常')
            // 这里你可以选择 reject，也可以选择 resolve 并让页面处理特定的 bizCode
            return Promise.reject(res)
        }

        // 3. 只有 code=200 且 bizCode=0 才会走到这里
        // 直接返回 res，这样页面可以通过 res.data 拿到业务数据，通过 res.msg 拿到成功提示
        return res
    },
    (err) => {
        // 处理 HTTP 协议层面的错误（如 404, 502, 网络断开）
        const message = err.response?.data?.msg || '网络连接异常'
        ElMessage.error(message)
        return Promise.reject(err)
    }
)

export default request