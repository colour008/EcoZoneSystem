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

// 注册接口
export const registerApi = (data) => {
    return request({
        url: '/auth/register',
        method: 'post',
        data
    })
}