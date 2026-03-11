// src/api/login.js
import request from './request'

// 登录接口
export function loginApi(data) {
    return request({
        url: '/auth/login',
        method: 'POST',
        data
    })
}