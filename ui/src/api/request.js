import axios from 'axios'

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
    (res) => {
        // 正常返回整个 Result 对象
        return res.data
    },
    (err) => {
        // 优先取后端业务异常消息
        const backendMsg = err.response?.data?.msg
        const finalMsg = backendMsg || '请求失败，请稍后重试'

        // 这里不再弹出，而是把消息抛给页面处理
        return Promise.reject(finalMsg)
    }
)

export default request